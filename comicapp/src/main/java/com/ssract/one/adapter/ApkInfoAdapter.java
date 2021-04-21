package com.ssract.one.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ccsky.sfish.R;
import com.core.base.utils.AppUtil;
import com.core.base.utils.PermissionUtil;
import com.core.base.utils.ToastUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ssract.one.bean.ApkInfoBean;
import com.ssract.one.utils.ApkFileUtil;
import com.ssract.one.utils.DialogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApkInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    List<ApkInfoBean> apkInfoBeans;
    private static final int ItemViewType_AD = 1;
    private static final int ItemViewType_APKINFO = 2;

    public ApkInfoAdapter(Activity activity, List<ApkInfoBean> beans) {
        this.activity = activity;
        apkInfoBeans = new ArrayList<>();

        for (int i = 0; i < beans.size(); i++) {

            ApkInfoBean mApkInfoBean = beans.get(i);
            if (i == 2){
                ApkInfoBean adBean = new ApkInfoBean();
                adBean.setAdView(true);
                apkInfoBeans.add(adBean);
            }else if (i - 6 >= 0 && ((i - 6) % 4 == 0)){
                ApkInfoBean adBean = new ApkInfoBean();
                adBean.setAdView(true);
                apkInfoBeans.add(adBean);
            }
            apkInfoBeans.add(mApkInfoBean);
        }

    }


    @Override
    public int getItemViewType(int position) {

        ApkInfoBean apkInfoBean = apkInfoBeans.get(position);
        if (apkInfoBean.isAdView()){
            return ItemViewType_AD;
        }
        return ItemViewType_APKINFO;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //根据不同的类型加载对应的布局
        switch (viewType) {
            case ItemViewType_AD:
                View mView = LayoutInflater.from(activity).inflate(R.layout.banner_adview_rec_item, parent, false);
                BannerAdViewHolder adViewViewHolder = new BannerAdViewHolder(mView);
                return adViewViewHolder;
            case ItemViewType_APKINFO:

                View apkInfoItemView = LayoutInflater.from(activity).inflate(R.layout.main_rec_item, parent, false);
                MyViewHolder myViewHolder = new MyViewHolder(apkInfoItemView);
                return myViewHolder;
            default:
                return null;
        }



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (apkInfoBeans == null || apkInfoBeans.isEmpty()){
            return;
        }

        final ApkInfoBean apkInfoBean = apkInfoBeans.get(position);
        String appName = apkInfoBean.getAppName();
        String packageName = apkInfoBean.getPackageName();


        if (holder instanceof MyViewHolder) {

            MyViewHolder myViewHolder = (MyViewHolder)holder;
            myViewHolder.nameTextView.setText(appName);
            myViewHolder.packageNameTextView.setText(packageName);
            myViewHolder.iconImageView.setImageDrawable(apkInfoBean.getIconDrawable());

            myViewHolder.menuTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);

                        Bundle bundle = new Bundle();
                        bundle.putString("appname", apkInfoBean.getAppName());
                        bundle.putString("packageName", apkInfoBean.getPackageName());
                        mFirebaseAnalytics.logEvent("extractapp",bundle);

                        if (PermissionUtil.requestPermissions_STORAGE(activity,102)){

                            final String apkSaveDir = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "extractapp";
                            ApkFileUtil.copyApp(apkInfoBean.getSourceDir(), apkSaveDir, apkInfoBean.getPackageName());
//                        ToastUtils.toast(activity,apkInfoBean.getAppName() + activity.getResources().getText(R.string.app_extra_success) + apkSaveDir);

//                        DialogUtil.alert(activity, apkInfoBean.getAppName() + activity.getResources().getText(R.string.app_extra_success) + apkSaveDir + ", open?",
//                                R.string.ok_confirm, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        ApkFileUtil.openDir(activity, apkSaveDir);
//                                    }
//                                });

                            String okMsg = apkInfoBean.getAppName() + activity.getResources().getText(R.string.app_extra_success) + apkSaveDir;
                            DialogUtil.alertOk(activity, okMsg, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                        }
                    } catch (IOException e) {
                        ToastUtils.toast(activity,apkInfoBean.getAppName() + activity.getResources().getText(R.string.app_extra_error));
                    }
                }
            });

            myViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtil.showInstalledAppDetails(activity,apkInfoBean.getPackageName());
                }
            });

        } else if (holder instanceof BannerAdViewHolder) {

            BannerAdViewHolder bannerAdViewHolder = (BannerAdViewHolder) holder;
            if (!bannerAdViewHolder.isLoadAd){
                AdRequest adRequest = new AdRequest.Builder().build();
                bannerAdViewHolder.bannerAdView.loadAd(adRequest);
                bannerAdViewHolder.isLoadAd = true;
            }

        }


    }


    @Override
    public int getItemCount() {
        if (apkInfoBeans == null || apkInfoBeans.isEmpty()){
            return 0;
        }
        return apkInfoBeans.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        View view;

        ImageView iconImageView;
        TextView nameTextView;
        TextView packageNameTextView;
        TextView menuTextView;

        public MyViewHolder(@NonNull View apkInfoItemView) {
            super(apkInfoItemView);

             this.view = apkInfoItemView;
             iconImageView = apkInfoItemView.findViewById(R.id.apk_info_icon);
             nameTextView = apkInfoItemView.findViewById(R.id.apk_info_name);
             packageNameTextView = apkInfoItemView.findViewById(R.id.apk_info_packagename);
             menuTextView = apkInfoItemView.findViewById(R.id.apk_info_menu_btn);
        }
    }

    class BannerAdViewHolder extends RecyclerView.ViewHolder {

        View view;

        AdView bannerAdView;
        boolean isLoadAd;

        public BannerAdViewHolder(@NonNull View mView) {
            super(mView);

            this.view = mView;
            bannerAdView = mView.findViewById(R.id.banner_adView);

        }
    }
}
