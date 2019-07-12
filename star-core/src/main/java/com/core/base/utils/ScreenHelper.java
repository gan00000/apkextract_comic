package com.core.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;

/**
 * Created by gan on 2016/9/18.
 */
public class ScreenHelper {

    Activity activity;
    int screenWidth;

    int screenHeight;
    boolean isPhone;
    boolean isPortrait = false;

    public ScreenHelper(Activity activity) {
        this.activity = activity;
        init();

    }

    private void init(){
        if (activity == null){
            return;
        }
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        Configuration c = activity.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >  Build.VERSION_CODES.JELLY_BEAN) {
            screenWidth = (int) ((c.densityDpi / 160.0) * c.screenWidthDp);
            screenHeight = (int) ((c.densityDpi / 160.0) * c.screenHeightDp);
        }else {
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
        }

        if (c.orientation == Configuration.ORIENTATION_PORTRAIT){
            isPortrait = true;
        }

        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于6尺寸则为Pad
        if (screenInches >= 6.0) {
            isPhone = false;
        }else{
            isPhone = true;
        }
    }

    public int[] getAvailableScreen(Context context){
        int width;
        int height;
        //判断是否是手机
        if (isPhone) {
            if (isPortrait) {
                width = screenWidth * 7 / 8;
                height = screenHeight * 4 / 5;
            } else {
                width = screenWidth * 7 / 8;
                height = screenHeight * 7 / 8;
            }
        } else {
            if (isPortrait) {
                width = screenWidth * 2 / 3;
                height = screenHeight * 3 / 5;
            } else {
                width = screenWidth * 3 / 5;
                height = screenHeight * 7 / 8;
            }
        }
        return new int[]{width,height};
    }

    public boolean isTablet() {
        return !isPhone;
    }


    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public boolean isPhone() {
        return isPhone;
    }

    public boolean isPortrait() {
        return isPortrait;
    }


}
