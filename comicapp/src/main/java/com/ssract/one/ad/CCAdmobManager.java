package com.ssract.one.ad;

import android.app.Activity;

import com.core.base.utils.PL;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class CCAdmobManager {


    private InterstitialAd mInterstitialAd;
    private Activity activity;

    public CCAdmobManager(Activity activity) {
        this.activity = activity;

        //在 Activity 的整个生命周期内，只需使用一个InterstitialAd对象，即可请求并展示多个插页式广告，因此该对象只需构建一次。
        mInterstitialAd = new InterstitialAd(activity);
    }

    public void loadAndShowInterstitialAd(String unitId){
        initInterstitialAd(unitId,true);
    }

    public void loadInterstitialAd(String unitId){
        initInterstitialAd(unitId,false);

    }

    private void initInterstitialAd(String unitId, boolean showImmediately) {
        //        mInterstitialAd.setAdUnitId("ca-app-pub-4247177623554873/7118605830");
        mInterstitialAd.setAdUnitId(unitId);
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("92AE37D588CFFF9DF177431BFB9AF7A9").build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                PL.i("AD onAdLoaded");
                if (showImmediately){
                    showInterstitialAd();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                // ERROR_CODE_INTERNAL_ERROR - 内部出现问题；例如，收到广告服务器的无效响应。
                // ERROR_CODE_INVALID_REQUEST - 广告请求无效；例如，广告单元 ID 不正确。
                // ERROR_CODE_NETWORK_ERROR - 由于网络连接问题，广告请求失败。
                // ERROR_CODE_NO_FILL - 广告请求成功，但由于缺少广告资源，未返回广告。

                PL.i("AD onAdFailedToLoad  errorCode:" + errorCode);

                logErrorCode(errorCode);


            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                PL.i("AD onAdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                PL.i("AD onAdClicked");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                PL.i("AD onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                PL.i("AD onAdClosed");
            }
        });
    }

    /**
     * 需要先调用loadInterstitialAd
     */
    public void showInterstitialAd(){

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            PL.i("The interstitial wasn't loaded yet.");
        }
    }



    public void showAdViewBanner(AdView mAdView) {

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                PL.i("AD onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                // ERROR_CODE_INTERNAL_ERROR - 内部出现问题；例如，收到广告服务器的无效响应。
                // ERROR_CODE_INVALID_REQUEST - 广告请求无效；例如，广告单元 ID 不正确。
                // ERROR_CODE_NETWORK_ERROR - 由于网络连接问题，广告请求失败。
                // ERROR_CODE_NO_FILL - 广告请求成功，但由于缺少广告资源，未返回广告。

                PL.i("AD onAdFailedToLoad  errorCode:" + errorCode);

                logErrorCode(errorCode);


            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                PL.i("AD onAdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                PL.i("AD onAdClicked");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                PL.i("AD onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                PL.i("AD onAdClosed");
            }
        });
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("92AE37D588CFFF9DF177431BFB9AF7A9")
                .build();
//        boolean isTestDevice = adRequest.isTestDevice(this);
//        if (isTestDevice){
//            PL.i("AD isTestDevice");
//        }else {
//            PL.i("AD isTestDevice");
//        }
        mAdView.loadAd(adRequest);
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
