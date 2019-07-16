package com.example.alan.sdkdemo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class ZoomImageView extends View {

    private String TAG = "ZoomImageView";

    /**
     * 初始化状态常量
     */
    public static final int STATUS_INIT = 1;

    /**
     * 图片放大状态常量
     */
    public static final int STATUS_ZOOM_OUT = 2;

    /**
     * 图片缩小状态常量
     */
    public static final int STATUS_ZOOM_IN = 3;

    /**
     * 图片拖动状态常量
     */
    public static final int STATUS_MOVE = 4;
    private final int mTouchSlopSquare;

    /**
     * 用于对图片进行移动和缩放变换的矩阵
     */
    private Matrix matrix = new Matrix();

    /**
     * 待展示的Bitmap对象
     */
    private Bitmap sourceBitmap;

    /**
     * 记录当前操作的状态，可选值为STATUS_INIT、STATUS_ZOOM_OUT、STATUS_ZOOM_IN和STATUS_MOVE
     */
    private int currentStatus;

    /**
     * ZoomImageView控件的宽度
     */
    private int width;

    /**
     * ZoomImageView控件的高度
     */
    private int height;

    /**
     * 记录两指同时放在屏幕上时，中心点的横坐标值
     */
    private float centerPointX;

    /**
     * 记录两指同时放在屏幕上时，中心点的纵坐标值
     */
    private float centerPointY;

    /**
     * 记录当前图片的宽度，图片被缩放时，这个值会一起变动
     */
    private float currentBitmapWidth;

    /**
     * 记录当前图片的高度，图片被缩放时，这个值会一起变动
     */
    private float currentBitmapHeight;

    /**
     * 单指按下时的横坐标
     */
    private float downX = -1;

    /**
     * 单指按下时的纵坐标
     */
    private float downY = -1;

    /**
     * 记录上次手指移动时的横坐标
     */
    private float lastXMove = -1;

    /**
     * 记录上次手指移动时的纵坐标
     */
    private float lastYMove = -1;

    /**
     * 记录手指在横坐标方向上的移动距离
     */
    private float movedDistanceX;

    /**
     * 记录手指在纵坐标方向上的移动距离
     */
    private float movedDistanceY;

    /**
     * 记录图片在矩阵上的横向偏移值
     */
    private float totalTranslateX;

    /**
     * 记录图片在矩阵上的纵向偏移值
     */
    private float totalTranslateY;

    /**
     * 记录图片在矩阵上的总缩放比例
     */
    private float totalRatio;

    /**
     * 记录手指移动的距离所造成的缩放比例
     */
    private float scaledRatio;

    /**
     * 记录图片初始化时的缩放比例
     */
    private float initRatio;

    /**
     * 记录上次两指之间的距离
     */
    private double lastFingerDis;

    /**
     * 是否正在对图片做缩放或移动处理
     */
    private boolean handlerEvent;

    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    /**
     * 屏幕的高度
     */
    private int screenHeight;


    /**
     * ZoomImageView构造函数，将当前操作状态设为STATUS_INIT。
     *
     * @param context
     * @param attrs
     */
    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentStatus = STATUS_INIT;

        ViewConfiguration configuration = ViewConfiguration.get(context);
        int touchSlop = configuration.getScaledTouchSlop();
        mTouchSlopSquare = touchSlop * touchSlop;

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;

        setBackgroundColor(Color.BLACK);
    }

    /**
     * 将待展示的图片设置进来。
     *
     * @param bitmap 待展示的Bitmap对象
     */
    public void setImageBitmap(Bitmap bitmap) {
        sourceBitmap = bitmap;
        currentStatus = STATUS_INIT;
        matrix.reset();
        invalidate();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            // 分别获取到ZoomImageView的宽度和高度
            width = getWidth();
            height = getHeight();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    // 当有两个手指按在屏幕上时，计算两指之间的距离
                    lastFingerDis = distanceBetweenFingers(event);
                    result = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    // 只有单指按在屏幕上移动时，为拖动状态
                    float xMove = event.getX();
                    float yMove = event.getY();
                    if (lastXMove == -1 && lastYMove == -1) {
                        lastXMove = xMove;
                        lastYMove = yMove;
                    }
                    currentStatus = STATUS_MOVE;
                    movedDistanceX = xMove - lastXMove;
                    movedDistanceY = yMove - lastYMove;
                    int distance = (int) ((movedDistanceX * movedDistanceX) + (movedDistanceY * movedDistanceY));
                    if (distance > mTouchSlopSquare) {
                        // 进行边界检查，不允许将图片拖出边界
                        if (totalTranslateX + movedDistanceX > 0) {
                            movedDistanceX = 0;
                        } else if (width - (totalTranslateX + movedDistanceX) > currentBitmapWidth) {
                            movedDistanceX = 0;
                        }
                        if (totalTranslateY + movedDistanceY > 0) {
                            movedDistanceY = 0;
                        } else if (height - (totalTranslateY + movedDistanceY) > currentBitmapHeight) {
                            movedDistanceY = 0;
                        }
                        // 调用onDraw()方法绘制图片
                        invalidate();
                        lastXMove = xMove;
                        lastYMove = yMove;
                        result = true;
                    }
                } else if (event.getPointerCount() == 2) {
                    // 有两个手指按在屏幕上移动时，为缩放状态
                    centerPointBetweenFingers(event);
                    double fingerDis = distanceBetweenFingers(event);
                    if (fingerDis > lastFingerDis) {
                        currentStatus = STATUS_ZOOM_OUT;
                    } else {
                        currentStatus = STATUS_ZOOM_IN;
                    }
                    // 进行缩放倍数检查，最大只允许将图片放大4倍，最小可以缩小到初始化比例
                    if ((currentStatus == STATUS_ZOOM_OUT && totalRatio < 4 * initRatio)
                            || (currentStatus == STATUS_ZOOM_IN && totalRatio > initRatio)) {
                        scaledRatio = (float) (fingerDis / lastFingerDis);
                        totalRatio = totalRatio * scaledRatio;
                        if (totalRatio > 4 * initRatio) {
                            totalRatio = 4 * initRatio;
                        } else if (totalRatio < initRatio) {
                            totalRatio = initRatio;
                        }
                        // 调用onDraw()方法绘制图片
                        invalidate();
                        lastFingerDis = fingerDis;
                    }
                    result = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    // 手指离开屏幕时将临时值还原
                    lastXMove = -1;
                    lastYMove = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 手指离开屏幕时将临时值还原
                lastXMove = -1;
                lastYMove = -1;
                if (distanceDownUpPoint(downX, downY, event.getX(), event.getY()) < 5) {
                    downX = -1;
                    downY = -1;
                    if (onClickListener != null) {
                        onClickListener.onClick();
                    }
                    return false;
                } else {
                    //裁剪回调图片
                    if (onCutListener != null) {
                        cut();
                    }
                }
                if (handlerEvent) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                result = true;
                handlerEvent = false;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 根据currentStatus的值来决定对图片进行什么样的绘制操作。
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sourceBitmap == null) return;
        switch (currentStatus) {
            case STATUS_ZOOM_OUT:
            case STATUS_ZOOM_IN:
                zoom(canvas);
                break;
            case STATUS_MOVE:
                move(canvas);
                break;
            case STATUS_INIT:
                initBitmap(canvas);
            default:
                canvas.drawBitmap(sourceBitmap, matrix, null);
                break;
        }
    }

    void drawBitmap(Canvas canvas) {
        Bitmap bitmap = sourceBitmap;
        if (bitmap == null) return;
        canvas.drawBitmap(bitmap, matrix, null);
    }

    /**
     * 对图片进行裁剪处理，回调裁剪后的bitmap发送给远端
     */
    private void cut() {
        //当前图片的宽高
        float scaledWidth = sourceBitmap.getWidth() * totalRatio;
        float scaledHeight = sourceBitmap.getHeight() * totalRatio;
        // 根据手指移动的距离计算出总偏移值
        float translateX = totalTranslateX + movedDistanceX;
        float translateY = totalTranslateY + movedDistanceY;
        int x;
        int y;
        int cutWidth;
        int cutHeight;
        // 如果当前图片宽度小于屏幕宽度，则按当前图片宽度裁剪，否则按屏幕宽度裁剪
        if (scaledWidth < screenWidth) {
            x = 0;
            cutWidth = (int) (scaledWidth / totalRatio);
        } else {
            x = (int) Math.abs(translateX / totalRatio);
            cutWidth = (int) (screenWidth / totalRatio);
        }
        // 如果当前图片高度小于屏幕高度，则按当前图片高度裁剪，否则按屏幕高度裁剪
        if (scaledHeight < screenHeight) {
            y = 0;
            cutHeight = (int) (scaledHeight / totalRatio);
        } else {
            y = (int) Math.abs(translateY / totalRatio);
            cutHeight = (int) (screenHeight / totalRatio);
        }
        if ((x + cutWidth) <= sourceBitmap.getWidth() && (y + cutHeight) <= sourceBitmap.getHeight()) {
            Log.i(TAG, "裁剪： x:" + x + " y:" + y + " width:" + cutWidth + " height:" + cutHeight);
            //裁剪的bitmap的宽高，必须是4的倍数，才能保证编成i420视频没问题。
            onCutListener.onCutBitmap(Bitmap.createBitmap(sourceBitmap, x, y, (cutWidth / 2) * 2, (cutHeight / 2) * 2 , null, true));
        }
    }

    /**
     * 对图片进行缩放处理。
     *
     * @param canvas
     */
    private void zoom(Canvas canvas) {
        handlerEvent = true;
        matrix.reset();
        // 将图片按总缩放比例进行缩放
        matrix.postScale(totalRatio, totalRatio);
        float scaledWidth = sourceBitmap.getWidth() * totalRatio;
        float scaledHeight = sourceBitmap.getHeight() * totalRatio;
        float translateX;
        float translateY;
        // 如果当前图片宽度小于屏幕宽度，则按屏幕中心的横坐标进行水平缩放。否则按两指的中心点的横坐标进行水平缩放
        if (currentBitmapWidth < width) {
            translateX = (width - scaledWidth) / 2f;
        } else {
            translateX = totalTranslateX * scaledRatio + centerPointX * (1 - scaledRatio);
            // 进行边界检查，保证图片缩放后在水平方向上不会偏移出屏幕
            if (translateX > 0) {
                translateX = 0;
            } else if (width - translateX > scaledWidth) {
                translateX = width - scaledWidth;
            }
        }
        // 如果当前图片高度小于屏幕高度，则按屏幕中心的纵坐标进行垂直缩放。否则按两指的中心点的纵坐标进行垂直缩放
        if (currentBitmapHeight < height) {
            translateY = (height - scaledHeight) / 2f;
        } else {
            translateY = totalTranslateY * scaledRatio + centerPointY * (1 - scaledRatio);
            // 进行边界检查，保证图片缩放后在垂直方向上不会偏移出屏幕
            if (translateY > 0) {
                translateY = 0;
            } else if (height - translateY > scaledHeight) {
                translateY = height - scaledHeight;
            }
        }
        // 缩放后对图片进行偏移，以保证缩放后中心点位置不变
        matrix.postTranslate(translateX, translateY);
        totalTranslateX = translateX;
        totalTranslateY = translateY;
        currentBitmapWidth = scaledWidth;
        currentBitmapHeight = scaledHeight;
        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    /**
     * 对图片进行平移处理
     *
     * @param canvas
     */
    private void move(Canvas canvas) {
        handlerEvent = true;
        matrix.reset();
        // 根据手指移动的距离计算出总偏移值
        float translateX = totalTranslateX + movedDistanceX;
        float translateY = totalTranslateY + movedDistanceY;
        // 先按照已有的缩放比例对图片进行缩放
        matrix.postScale(totalRatio, totalRatio);
        // 再根据移动距离进行偏移
        matrix.postTranslate(translateX, translateY);
        totalTranslateX = translateX;
        totalTranslateY = translateY;
        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    /**
     * 对图片进行初始化操作，包括让图片居中，以及当图片大于屏幕宽高时对图片进行压缩。
     *
     * @param canvas
     */
    private void initBitmap(Canvas canvas) {
        if (sourceBitmap != null) {
            matrix.reset();
            int bitmapWidth = sourceBitmap.getWidth();
            int bitmapHeight = sourceBitmap.getHeight();

            Log.i(TAG, "width:" + width + " height:" + height + " bitmapWidth:" + bitmapWidth + " bitmapHeight:" + bitmapHeight);

            if (bitmapWidth > width || bitmapHeight > height) {
                if (bitmapWidth - width > bitmapHeight - height) {
                    // 当图片宽度大于屏幕宽度时，将图片等比例压缩，使它可以完全显示出来
                    float ratio = width / (bitmapWidth * 1.0f);
                    matrix.postScale(ratio, ratio);
                    float translateY = (height - (bitmapHeight * ratio)) / 2f;
                    // 在纵坐标方向上进行偏移，以保证图片居中显示
                    matrix.postTranslate(0, translateY);
                    totalTranslateY = translateY;
                    totalRatio = initRatio = ratio;
                } else {
                    // 当图片高度大于屏幕高度时，将图片等比例压缩，使它可以完全显示出来
                    float ratio = height / (bitmapHeight * 1.0f);
                    matrix.postScale(ratio, ratio);
                    float translateX = (width - (bitmapWidth * ratio)) / 2f;
                    // 在横坐标方向上进行偏移，以保证图片居中显示
                    matrix.postTranslate(translateX, 0);
                    totalTranslateX = translateX;
                    totalRatio = initRatio = ratio;
                }
                currentBitmapWidth = bitmapWidth * initRatio;
                currentBitmapHeight = bitmapHeight * initRatio;
            } else {
                if (width - bitmapWidth > height - bitmapHeight) {
                    float ratio = height / (bitmapHeight * 1.0f);
                    matrix.postScale(ratio, ratio);
                    float translateX = (width - (bitmapWidth * ratio)) / 2f;
                    // 在横坐标方向上进行偏移，以保证图片居中显示
                    matrix.postTranslate(translateX, 0);
                    totalTranslateX = translateX;
                    totalRatio = initRatio = ratio;
                } else {
                    float ratio = width / (bitmapWidth * 1.0f);
                    matrix.postScale(ratio, ratio);
                    float translateY = (height - (bitmapHeight * ratio)) / 2f;
                    // 在纵坐标方向上进行偏移，以保证图片居中显示
                    matrix.postTranslate(0, translateY);
                    totalTranslateY = translateY;
                    totalRatio = initRatio = ratio;
                }
                currentBitmapWidth = bitmapWidth;
                currentBitmapHeight = bitmapHeight;
            }
            canvas.drawBitmap(sourceBitmap, matrix, null);
        }
    }

    private double distanceDownUpPoint(float downX, float downY, float upX, float upY) {
        float disX = Math.abs(downX - upX);
        float disY = Math.abs(downY - upY);
        return Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * 计算两个手指之间的距离。
     *
     * @param event
     * @return 两个手指之间的距离
     */
    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * 计算两个手指之间中心点的坐标。
     *
     * @param event
     */
    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float yPoint0 = event.getY(0);
        float xPoint1 = event.getX(1);
        float yPoint1 = event.getY(1);
        centerPointX = (xPoint0 + xPoint1) / 2;
        centerPointY = (yPoint0 + yPoint1) / 2;
    }

    public void reset() {
        currentStatus = STATUS_INIT;
        matrix.reset();
        if (sourceBitmap != null) {
            sourceBitmap.recycle();
            sourceBitmap = null;
        }
    }

    private OnClickListener onClickListener;

    public void setOnClickListener (OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick();
    }

    private OnCutListener onCutListener;

    public void setOnCutListener(OnCutListener onCutListener) {
        this.onCutListener = onCutListener;
    }

    public interface OnCutListener {
        void onCutBitmap(Bitmap bitmap);
    }
}

