package com.ssract.one.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ApkInfoBean {

    private String appName;
    private String packageName;
    private String sourceDir;
    private Bitmap iconBitmap;
    private Drawable iconDrawable;
    private boolean isSystemApp;

    private boolean isAdView;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        this.iconBitmap = iconBitmap;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }


    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public boolean isAdView() {
        return isAdView;
    }

    public void setAdView(boolean adView) {
        isAdView = adView;
    }
}
