package com.core.base.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.core.base.callback.SavePhotoCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ScreenshotUtil {

	/**
	 * 把当前activity转换为bitmap === 截图
	 * @param act  当前需要截图的Acitivity
	 * @param view 当前需要截图的view
	 * @return
	 */
	public static Bitmap convertViewToBitmap(Activity act, View view) {

		View mView = null;
		if (view != null) {
			mView = view;
		}else{
			mView = act.getWindow().getDecorView();
		}
		
		Bitmap bitmap = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(),
				Bitmap.Config.RGB_565);
		// 利用bitmap生成画布
		Canvas canvas = new Canvas(bitmap);
		// 把view中的内容绘制在画布上
		if (mView != null) {
			mView.draw(canvas);	
		}
		return bitmap;
	}
	
	/**
	 * 保存图片到相册
	 * @param context  上下文
	 * @param bitmap   需要保存的bitmap
	 */
	public static void insertImage(Context context, Bitmap bitmap){
		if (bitmap == null) {
			return;
		}
		if ( !PermissionUtil.hasSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			return ;
		}
		
		String path = MediaStore.Images.Media.insertImage(context.getApplicationContext().getContentResolver(), bitmap, "", "");
		PL.i("insertImage==path:" + path);
		MediaScanner mediaScanner = new MediaScanner(context.getApplicationContext());
		mediaScanner.scanFile(new File(path), "image/jpeg");
	}


	/**
	 *保存图片到相册Camera目录
	 * @param context
	 * @param bitmap        保存的图片bitmap
	 * @param savePhotoCallback  保存图片回调
     */
	public static void insertImage(Context context, Bitmap bitmap, SavePhotoCallback savePhotoCallback) {
		// Create screenshot directory if it doesn't exist

		if (bitmap == null) {
			if (savePhotoCallback != null) {
				savePhotoCallback.onSaveFailure();
			}
			return;
		}
		if (!(PermissionUtil.hasSelfPermission(context,
				Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
			if (context instanceof Activity) {
				PermissionUtil.requestPermission((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE, 1717);
			}
			if (savePhotoCallback != null) {
				savePhotoCallback.onSaveFailure();
			}
			return;
		}

		String dirName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Camera";
		File fileDir = new File(dirName);
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}
		long mImageTime = System.currentTimeMillis();//取当前系统时间
		// media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
		// for DATE_TAKEN
		long dateSeconds = mImageTime / 1000;
		String mImageFileName = dateSeconds + ".png"; //以保存时间命名
		String mImageFilePath = dirName + File.separator + mImageFileName; //注意这里的mImageFilePath是： 目录名称+文件名

		Long start = System.currentTimeMillis();
		try {
			File file = new File(mImageFilePath);
			OutputStream out = new FileOutputStream(file);
			try {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			} finally {
				out.close();
				if (bitmap != null) {
					bitmap.recycle();
					bitmap = null;
				}
			}
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));// 刷新系统相册
		} catch (Exception e) {
			e.printStackTrace();
			if (savePhotoCallback != null) {
				savePhotoCallback.onSaveFailure();
			}
		}
		if (savePhotoCallback != null) {
			savePhotoCallback.onSaveSuccess(mImageFilePath);
		}
		Long end = System.currentTimeMillis();
	}
}
