package com.core.base.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtils {

	public static void toast(Context context, String msg) {
		if (context != null && !TextUtils.isEmpty(msg)) {
			Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
		}
	}

	public static void toast(Context context, int msg) {
		if (context != null) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}

	public static void toast(Context context, int msg, int time) {
		if (context != null) {
			if (time == Toast.LENGTH_LONG) {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public static void toast(Context context, String msg, int time) {
		if (context != null) {
			if (time == Toast.LENGTH_LONG) {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/*public static void toast(Context context, View parentView,int bgRes, String msg, int time,int xOffset, int yOffset) {
		if (context != null) {
			LayoutInflater inflater = LayoutInflater.from(context
					.getApplicationContext());
			View layout = inflater.inflate(E_layout.efun_pd_toast_common, (ViewGroup) parentView.findViewById(E_id.toast_body_root));
			TextView text = (TextView) layout.findViewById(E_id.toast_text);
			text.setText(msg);
			if(bgRes != -1){
				text.setBackgroundResource(bgRes);
			}
			Toast toast = new Toast(context.getApplicationContext());
			toast.setGravity(Gravity.BOTTOM, xOffset, yOffset);
			if (time == Toast.LENGTH_LONG) {
				toast.setDuration(Toast.LENGTH_LONG);
			} else {
				toast.setDuration(Toast.LENGTH_SHORT);
			}
			toast.setView(layout);
			toast.show();
		}
	}*/
}
