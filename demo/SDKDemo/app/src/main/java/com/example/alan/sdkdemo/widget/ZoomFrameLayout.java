package com.example.alan.sdkdemo.widget;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

public class ZoomFrameLayout extends FrameLayout {

    private static final String TAG = "ZoomFrameLayout";

    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    /**
     * 屏幕的高度
     */
    private int screenHeight;

    /**
     * 记录视频在矩阵上的总缩放比例
     */
    private float totalRatio;

    /**
     * 记录手指移动的距离所造成的缩放比例
     */
    private float scaledRatio;

    /**
     * 记录视频初始化时的缩放比例
     */
    private float initRatio;

    /**
     * 记录上次两指之间的距离
     */
    private double lastFingerDis;

    /**
     * 记录此次事件（down move up）中是否消费了此事件
     */
    private boolean handlerEvent = false;

    /**
     * 处理单指拖动事件
     */
    private ViewDragHelper mDragHelper;

    /**
     * 单指按下时的横坐标
     */
    private float downX = -1;

    /**
     * 单指按下时的纵坐标
     */
    private float downY = -1;

    public ZoomFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ZoomFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ZoomFrameLayout(Context context) {
        this(context,null);
    }

    private void init() {
        initRatio = 1.0f;
        mDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = getMeasuredWidth();
        screenHeight = getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        if (event.getPointerCount() == 1) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (distanceDownUpPoint(downX, downY, event.getX(), event.getY()) < 10) {
                        downX = -1;
                        downY = -1;
                        if (onClickListener != null) {
                            onClickListener.onClick();
                        }
                    }
                    break;
            }
            // 只有单指按在屏幕上移动时，为拖动状态
            try {
                mDragHelper.processTouchEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    // 当有两个手指按在屏幕上时，计算两指之间的距离
                    lastFingerDis = distanceBetweenFingers(event);
                    result = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    // 有两个手指按在屏幕上移动时，为缩放状态
                    int fingerDis = (int) distanceBetweenFingers(event);
                    // 进行缩放倍数检查，最大只允许将视频放大4倍，最小可以缩小到初始化比例
                    if (totalRatio < 2 * initRatio || totalRatio > initRatio) {
                        scaledRatio = (float) (fingerDis / lastFingerDis);
                        totalRatio = totalRatio * scaledRatio;
                        if (totalRatio > 2 * initRatio) {
                            totalRatio = 2 * initRatio;
                        } else if (totalRatio < initRatio) {
                            totalRatio = initRatio;
                            requestLayout();
                        }
                        lastFingerDis = fingerDis;
                        result = true;
                        zoom();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (handlerEvent) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                result = true;
                handlerEvent = false;
                break;
        }
        return result;
    }

    /**
     * 对视频进行缩放处理。
     */
    private void zoom() {
        handlerEvent = true;
        ViewHelper.setScaleX(ZoomFrameLayout.this, totalRatio);// x方向上缩放
        ViewHelper.setScaleY(ZoomFrameLayout.this, totalRatio);// y方向上缩放
    }

    /**
     * 计算
     * @param downX
     * @param downY
     * @param upX
     * @param upY
     * @return
     */
    private double distanceDownUpPoint(float downX, float downY, float upX, float upY) {
        float disX = Math.abs(downX - upX);
        float disY = Math.abs(downY - upY);
        return Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * 计算两个手指之间的距离。
     *
     * @param event 事件
     * @return 两个手指之间的距离
     */
    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件
         *
         * @param child 当前触摸的子view
         * @param pointerId
         * @return true就捕获并解析；false不捕获
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (totalRatio > 1){
                return true;
            }
            return false;
        }

        /**
         * 控制水平方向上的位置
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.d(TAG,"left" + left + " screenWidth" +  screenWidth + " totalRatio" + totalRatio + " dx" + dx);
            if (left < (screenWidth - getWidth() * totalRatio) / 4)
                left = (int) (screenWidth - getWidth() * totalRatio) / 4;// 限制mainView可向左移动到的位置
            if (left > (getWidth() * totalRatio - screenWidth) / 4)
                left = (int) (getWidth() * totalRatio - screenWidth) / 4;// 限制mainView可向右移动到的位置

            return left;
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top < (screenHeight - getHeight() * totalRatio) / 4) {
                top = (int) (screenHeight - getHeight() * totalRatio) / 4;// 限制mainView可向上移动到的位置
            }
            if (top > (getHeight() * totalRatio - screenHeight) / 4) {
                top = (int) (getHeight() * totalRatio - screenHeight) / 4;// 限制mainView可向下移动到的位置
            }

            return top;
        }

    };

    private OnClickListener onClickListener;

    public void setOnClickListener (OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick();
    }
}