package com.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenSize {
    private static ScreenSize instance = null;
    private int screenWidth, screenHeight;

    public static ScreenSize getInstance(Context mContext) {
        if (instance == null) {
            synchronized (ScreenSize.class) {
                if (instance == null)
                    instance = new ScreenSize(mContext);
            }
        }
        return instance;
    }

    private ScreenSize(Context mContext) {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
        screenHeight = dm.heightPixels;// 获取屏幕分辨率高度
    }

    //获取屏幕宽度
    public int getScreenWidth() {
        return screenWidth;
    }

    //获取屏幕高度
    public int getScreenHeight() {
        return screenHeight;
    }
}
