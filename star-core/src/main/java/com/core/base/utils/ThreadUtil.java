package com.core.base.utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ThreadUtil {

	public static boolean isUiThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	public static void checkUiThread(Context context) {
		if (!isUiThread()) {
			PL.e("Calling this must from your main thread");
			Toast.makeText(context, "Calling this must from your main thread", Toast.LENGTH_SHORT).show();
			throw new IllegalStateException("Calling this must from your main thread");
		}
	}
	public static void checkUiThread() {
		if (!isUiThread()) {
			PL.e("Calling this must from your main thread");
			throw new IllegalStateException("Calling this must from your main thread");
		}
	}

}
