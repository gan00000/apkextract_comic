package com.core.base.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

public class ApkInstallUtil {
	
	/**
	 * 安装apk
	 * 
	 * @param apkPath
	 */
	public static void installApk(Context context, String apkPath) {
		PL.i("installApk："+apkPath);
		File apkfile = new File(apkPath);
		if (!apkfile.exists()) {
			PL.i("---------------没有发现要安装的文件------------------");
			return;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		//判断是否是AndroidN以及更高的版本
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", apkfile);
			intent.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
		} else {
			intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
		}

//		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		context.startActivity(intent);
		
	}

	
	public static boolean isInstallApp(Context context, String packageName) {
		
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			String pn = paklist.get(i).packageName;
			if (!TextUtils.isEmpty(pn) && pn.equals(packageName)) {
				return true;
			}
		}
		return false;
		
	}

}
