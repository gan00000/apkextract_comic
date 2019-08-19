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

import com.core.base.utils.AppUtil;
import com.core.base.utils.PermissionUtil;
import com.core.base.utils.ToastUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ssract.one.R;
import com.ssract.one.bean.ApkInfoBean;
import com.ssract.one.utils.ApkFileUtil;
import com.ssract.one.utils.DialogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ApkInfoAdapter extends RecyclerView.Adapter<ApkInfoAdapter.MyViewHolder> {

    private Activity activity;
    List<ApkInfoBean> apkInfoBeans;

    public ApkInfoAdapter(Activity activity, List<ApkInfoBean> apkInfoBeans) {
        this.activity = activity;
        this.apkInfoBeans = apkInfoBeans;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View apkInfoItemView = LayoutInflater.from(activity).inflate(R.layout.main_rec_item, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(apkInfoItemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (apkInfoBeans == null || apkInfoBeans.isEmpty()){
            return;
        }

        final ApkInfoBean apkInfoBean = apkInfoBeans.get(i);
        String appName = apkInfoBean.getAppName();
        String packageName = apkInfoBean.getPackageName();

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

                        final String apkSaveDir = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + activity.getPackageName();
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
}
