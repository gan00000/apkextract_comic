package com.core.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.core.base.js.UploadHandler;

/**
 * Created by gan on 2016/12/2.
 */

public class BaseWebChromeClient extends WebChromeClient {

    private static final String TAG = "BaseWebChromeClient";

    private Activity activity;

    private Fragment fragment;
    boolean isFragment = false;
    private ProgressBar progressBar;
    private UploadHandler handler;

    public BaseWebChromeClient(ProgressBar progressBar, Activity activity) {
        this.progressBar = progressBar;
        this.activity = activity;
        initWebChromeClient(activity);
    }

    /**
     *  fragment中使用
     * @param progressBar
     * @param fragment
     */
    public BaseWebChromeClient(ProgressBar progressBar, Fragment fragment) {
        this.progressBar = progressBar;
        this.fragment = fragment;
        this.isFragment = true;
        initWebChromeClient(activity);
    }

    public BaseWebChromeClient(Activity activity) {
        this.activity = activity;
        initWebChromeClient(activity);
    }

    public BaseWebChromeClient() {
        initWebChromeClient(null);
    }

    private void initWebChromeClient(Activity activity){
        if (isFragment && fragment != null){
            handler = new UploadHandler(fragment);

        }else if (activity != null){
            handler = new UploadHandler(activity);
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (progressBar !=null) {
            progressBar.setProgress(newProgress);
            if (newProgress > 90){
                progressBar.setVisibility(View.GONE);
            }else{
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }


    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        Log.d(TAG, "onShowFileChooser");
        if (handler != null) {
            handler.onShowFileChooser(filePathCallback,fileChooserParams);
        }
        return true;
    }


    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture){
        Log.d(TAG, "openFileChooser");
        if (handler != null) {
            handler.openFileChooser(uploadFile, acceptType, capture);
        }
    }

    // Android 2.x
    public void openFileChooser(ValueCallback<Uri> uploadFile){
        Log.d(TAG, "openFileChooser  openFileChooser(ValueCallback<Uri> uploadFile)");
        if (handler != null) {
            handler.openFileChooser(uploadFile, null, null);
        }
    }


    // Android 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        Log.d(TAG, "openFileChooser ValueCallback<Uri> uploadMsg, String acceptType");
    }


    public void onActivityResult(int requestCode,int resultCode, Intent intent){
        if (handler != null) {
            handler.onResult(requestCode,resultCode, intent);
        }
    }


    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

        String message = "Console: " + consoleMessage.message() + " " + consoleMessage.sourceId() +  ":" + consoleMessage.lineNumber();

        switch (consoleMessage.messageLevel()) {
            case TIP:
                Log.v(TAG, message);
                break;
            case LOG:
                 Log.i(TAG, message);
                break;
            case WARNING:
                Log.w(TAG, message);
                break;
            case ERROR:
                Log.e(TAG, message);
                break;
            case DEBUG:
                Log.d(TAG, message);
                break;
        }

        return true;
    }
}
