package com.ssract.one;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.core.base.utils.PL;
import com.core.base.utils.PermissionUtil;
import com.core.base.utils.SignatureUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ssract.one.adapter.ApkInfoAdapter;
import com.ssract.one.bean.ApkInfoBean;
import com.ssract.one.fragment.ApkInfoFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivityNew extends AppCompatActivity {


    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    FragmentManager fragmentManager;

    List<ApkInfoBean> apkInfoBeans;

    private ApkInfoAdapter apkInfoAdapter;
    ProgressDialog progressDialog;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    public static final String TAG = "MainActivity";
    private FirebaseAnalytics mFirebaseAnalytics;
    private String[] titles;
    private ArrayList mArrayList = new ArrayList();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        PermissionUtil.requestPermissions_STORAGE(this,102);
        PL.e(SignatureUtil.getSignatureSHA1WithColon(this,this.getPackageName()));
        reportEvent();

        titles = new String[]{getString(R.string.userapp),getString(R.string.systemapp),"ALL"};

        mTabLayout = findViewById(R.id.idTablout);
        mViewPager = findViewById(R.id.idViewPager);
        fragmentManager = getSupportFragmentManager();

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-4247177623554873~9500822679");

        for (int i = 0; i < titles.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab());
            mArrayList.add(ApkInfoFragment.newInstance(titles[i], "" + i));
        }

        mTabLayout.setupWithViewPager(mViewPager,false);

        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {

                PL.i("Fragment item:" + position);

                return (Fragment) mArrayList.get(position);
            }

            @Override
            public int getCount() {
                return titles.length;
            }
        });

        for(int i=0;i<titles.length;i++){
            mTabLayout.getTabAt(i).setText(titles[i]);
        }


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4247177623554873/7118605830");
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("92AE37D588CFFF9DF177431BFB9AF7A9").build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                PL.i("AD onAdLoaded");
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    PL.i("The interstitial wasn't loaded yet.");
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

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAdView();
            }
        }, 5000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.apptitlemenu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_comic:
                Toast.makeText(this,"action_comic button clicked",Toast.LENGTH_SHORT).show();
                reportEventForName("action_comic");

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void showAdView() {
        mAdView = findViewById(R.id.app_adView);
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

    private void reportEvent() {


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "mainactivity_create");
        mFirebaseAnalytics.logEvent("MainActivity_Create",bundle);

    }

    private void reportEventForName(String name) {

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(name,bundle);

    }

}
