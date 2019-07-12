package com.ssract.one;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.core.base.request.SRequestAsyncTask;
import com.core.base.utils.PL;
import com.core.base.utils.PermissionUtil;
import com.core.base.utils.SignatureUtil;
import com.ssract.one.adapter.ApkInfoAdapter;
import com.ssract.one.bean.ApkInfoBean;
import com.ssract.one.utils.ApkInfoBeanComparator;
import com.ssract.one.utils.DialogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;

    List<ApkInfoBean> apkInfoBeans;

    private ApkInfoAdapter apkInfoAdapter;
    ProgressDialog progressDialog;

    public static final String TAG = "MainActivity";

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.apk_info_rec);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = DialogUtil.createDialog(this, getResources().getText(R.string.app_loading));
        apkInfoBeans = new ArrayList<>();

        PermissionUtil.requestPermissions_STORAGE(this,102);

        PL.e(SignatureUtil.getSignatureSHA1WithColon(this,this.getPackageName()));

        new SRequestAsyncTask(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {

                getInstalledApk();
                if (!apkInfoBeans.isEmpty()){
                    Collections.sort(apkInfoBeans,new ApkInfoBeanComparator());
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                apkInfoAdapter = new ApkInfoAdapter(MainActivity.this, apkInfoBeans);
                mRecyclerView.setAdapter(apkInfoAdapter);

                progressDialog.dismiss();

            }
        }.asyncExcute();



    }


    private void getInstalledApk() {

        PackageManager packageManager = getPackageManager();
        List<PackageInfo> allPackages = packageManager.getInstalledPackages(0);
        if (allPackages == null){
            return;
        }
        for (int i = 0; i < allPackages.size(); i++) {
            PackageInfo packageInfo = allPackages.get(i);
            String path = packageInfo.applicationInfo.sourceDir;
            String name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            Log.i(TAG, path);
            Log.i(TAG, name);

            ApkInfoBean apkInfoBean = new ApkInfoBean();
            apkInfoBean.setAppName(name);
            apkInfoBean.setSourceDir(path);
            apkInfoBean.setPackageName(packageInfo.applicationInfo.packageName);
            Drawable iconDrawable = packageManager.getApplicationIcon(packageInfo.applicationInfo);
            apkInfoBean.setIconDrawable(iconDrawable);

            /* icon1和icon2其实是一样的 */
//            Drawable icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息
//            Drawable icon2 = appInfo.loadIcon(pm);

            try {
                if (isSystemApp(packageInfo)) {
                    Log.e(TAG, name + " is not user app");
                    apkInfoBean.setSystemApp(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            apkInfoBeans.add(apkInfoBean);
        }
    }


    public boolean isSystemApp(PackageInfo pInfo) {
        return (((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) && ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0));
    }
}
