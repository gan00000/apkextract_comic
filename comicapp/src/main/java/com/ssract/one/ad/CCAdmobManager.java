package com.ssract.one.ad;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.core.base.utils.PL;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class CCAdmobManager {


    private static final String TAG = "CCAdmobManager";
    private InterstitialAd mInterstitialAd;
    private Activity activity;

    public CCAdmobManager(Activity activity) {
        this.activity = activity;

        //在 Activity 的整个生命周期内，只需使用一个InterstitialAd对象，即可请求并展示多个插页式广告，因此该对象只需构建一次。
        //mInterstitialAd = new InterstitialAd(activity);

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity,"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }

//    public void setInterstitialAdId(String unitId){
//        mInterstitialAd.setAdUnitId(unitId);
//    }

    public void loadAndShowInterstitialAd(String unitId){
        showInterstitialAd();
    }


    /**
     * 需要先调用loadInterstitialAd
     */
    public void showInterstitialAd(){

        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }


    private void logErrorCode(int errorCode) {
        switch (errorCode){

            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                PL.i("ERROR_CODE_INTERNAL_ERROR - 内部出现问题；例如，收到广告服务器的无效响应。");
                break;

            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                PL.i("ERROR_CODE_INVALID_REQUEST - 广告请求无效；例如，广告单元 ID 不正确。");
                break;

            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                PL.i("ERROR_CODE_NETWORK_ERROR - 由于网络连接问题，广告请求失败。");
                break;

            case AdRequest.ERROR_CODE_NO_FILL:
                PL.i("ERROR_CODE_NO_FILL - 广告请求成功，但由于缺少广告资源，未返回广告。");
                break;

            default:

                break;
        }
    }
}
