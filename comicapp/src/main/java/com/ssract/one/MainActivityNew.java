package com.ssract.one;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ccsky.sfish.R;
import com.ccsky.sfish.ui.SkyMainActivity;
import com.core.base.utils.PL;
import com.core.base.utils.PermissionUtil;
import com.core.base.utils.SignatureUtil;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.ssract.one.adapter.ApkInfoAdapter;
import com.ssract.one.bean.ApkInfoBean;
import com.ssract.one.fragment.ApkInfoFragment;

import java.io.IOException;
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

    private FirebaseRemoteConfig mFirebaseRemoteConfig;


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

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    String adverstingId = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext()).getId();
                    Log.d("adverstingId", "adverstingId = " + adverstingId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }


                return null;
            }
        }.execute();

        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [END get_remote_config_instance]

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. Also use Remote Config
        // Setting to set the minimum fetch interval.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
//                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        // [END enable_dev_mode]

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        titles = new String[]{getString(R.string.userapp),getString(R.string.systemapp),"ALL"};

        mTabLayout = findViewById(R.id.idTablout);
        mViewPager = findViewById(R.id.idViewPager);
        fragmentManager = getSupportFragmentManager();


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


//        Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showAdView();
//            }
//        }, 5000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRemoteConfig();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.apptitlemenu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    private Menu menu;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_comic:
                PL.i("action_comic button clicked");
                reportEventForName("action_comic_clicked");

                Intent mIntent = new Intent(this, SkyMainActivity.class);
                this.startActivity(mIntent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

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


    /**
     * Fetch a welcome message from the Remote Config service, and then activate it.
     */
    private void fetchRemoteConfig() {

        // [START fetch_config_with_callback]
        if (mFirebaseRemoteConfig == null){
            return;
        }
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        PL.i("Config fetchAndActivate onComplete");
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                           boolean isShowMic = mFirebaseRemoteConfig.getBoolean("cc_show_comic");

                           MainActivityNew.this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {

                                   MenuItem comicMenuItem = menu.findItem(R.id.action_comic);
                                   if (isShowMic){
                                       comicMenuItem.setVisible(true);
                                   }else {
                                       comicMenuItem.setVisible(false);
                                   }
                               }
                           });

                        }
                    }
                });
        // [END fetch_config_with_callback]
    }

}
