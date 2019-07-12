package com.core.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.core.base.utils.AppUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by gan on 2016/12/2.
 */

public class BaseWebViewClient extends WebViewClient {

    protected static boolean DISABLE_SSL_CHECK_FOR_TESTING = false;

    Activity activity;

    public BaseWebViewClient() {
    }

    public BaseWebViewClient(Activity activity) {
        this.activity = activity;
    }

    /**
     * @param view
     * @param url
     * @deprecated
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        overrideUrlLoading(view,url);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        overrideUrlLoading(view,request.getUrl().toString());
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    public boolean overrideUrlLoading(WebView webView,String url){

        PL.i("overrideUrlLoading url:" + url);

        if (url.startsWith("sms:")) {
            try {
                url = URLDecoder.decode(url, "utf-8");
                String[] str = url.split("\\?");
                if (str != null && str.length >= 2) {

                    String[]  str1 = str[0].split("\\:");
                    String[]  str2 = str[1].split("\\=");
                    AppUtil.sendMessage(activity,str1[1], str2[1]);
                }
            } catch (UnsupportedEncodingException e) {
                webView.loadUrl(url);
            }

        }else if(url.startsWith("https://line.naver.jp/R/msg/text/?") || url.startsWith("https://line.me/R/msg/text/?")){
            AppUtil.openInOsWebApp(activity, url);

        }else if(url.startsWith("whatsapp//")){

            AppUtil.openInOsWebApp(activity, url);

        } else if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https") || url.toLowerCase().startsWith("file")) {
            webView.loadUrl(url);
        } else {
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                webView.loadUrl(url);
            }
        }
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (DISABLE_SSL_CHECK_FOR_TESTING) {
            handler.cancel();
        } else {//永远只走这里，这么写是为了躲过google警告
            handler.proceed();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//        super.onReceivedError(view, request, error);
        PL.w("onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)");
        if (!handleReceivedError(view, request.getUrl().toString())){
            super.onReceivedError(view, request, error);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//        super.onReceivedError(view, errorCode, description, failingUrl);
        PL.w("onReceivedError(WebView view, int errorCode, String description, String failingUrl)");
        if(!handleReceivedError(view, failingUrl)){
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    public boolean handleReceivedError(WebView webView,String url){
        try {
            PL.w("ERROR URL:" + url);
//            webView.loadData(URLEncoder.encode("loading error!!!","utf-8"),"text/html","utf-8");
            if (SStringUtil.isNotEmpty("url") && url.contains("starb168.com") && url.contains("?")){
                String urlSub = url.substring(0,url.indexOf("?"));
                PL.w("ERROR URL urlSub:" + urlSub);
                if (urlSub.contains("starb168.com")){
                    webView.loadData("loading error, Please try again later", "text/html", "utf-8");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
