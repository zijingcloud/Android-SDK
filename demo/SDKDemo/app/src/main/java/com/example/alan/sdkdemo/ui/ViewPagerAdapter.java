package com.example.alan.sdkdemo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.alan.sdkdemo.widget.ZoomImageView;
import com.vcrtc.utils.BitmapUtil;
import com.vcrtc.webrtc.RTCManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author ricardo
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<String> imagePaths;

    public ViewPagerAdapter(Context context, List imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZoomImageView iv = new ZoomImageView(container.getContext(), null);

        if (onItemImageListener != null) {
            iv.setOnClickListener(() -> onItemImageListener.onClick());
            iv.setOnCutListener(bitmap -> onItemImageListener.onCutBitmap(bitmap));
        }

        Bitmap bitmap;
        if (RTCManager.isIsShitongPlatform()) {
            bitmap = BitmapUtil.formatBitmap16_9(imagePaths.get(position), 1920, 1080);
        } else {
            FileInputStream fis = null;

            try {
                fis = new FileInputStream(imagePaths.get(position));
            } catch (FileNotFoundException var17) {
                var17.printStackTrace();
            }

            bitmap = BitmapFactory.decodeStream(fis);

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        iv.setImageBitmap(bitmap);

        // 添加到ViewPager容器
        container.addView(iv);

        // 返回填充的View对象
        return iv;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ZoomImageView)object).reset();
        container.removeView((View) object);
    }

    private OnItemImageListener onItemImageListener;

    void setOnItemImageListener(OnItemImageListener onItemImageListener) {
        this.onItemImageListener = onItemImageListener;
    }

    public interface OnItemImageListener {
        void onClick();
        void onCutBitmap(Bitmap bitmap);
    }
}
