package com.ssract.one;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.core.base.utils.PL;
import com.core.base.utils.PermissionUtil;
import com.core.base.utils.SignatureUtil;
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
    }

    private void reportEvent() {


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "mainactivity_create");
        mFirebaseAnalytics.logEvent("MainActivity_Create",bundle);

    }

}
