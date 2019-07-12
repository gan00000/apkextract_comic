package com.core.base.utils;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.util.Log;

public class ResUtil {
	/**
	 * 获取资源
	 * 
	 * @param context
	 * @param packageName
	 * @param resourcesName
	 * @return
	 */
	public static int getResourcesIdByName(Context context, String packageName, String resourcesName) {

		Resources resources = context.getResources();
		int id = resources.getIdentifier(resourcesName, packageName, context.getPackageName());
		if (id == 0) {
			Log.w("ResUtil", "resource not find！resourcesName:" + resourcesName);
		}
		return id;
	}

	/**
	 * 获取布局ID
	 * 
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int findLayoutIdByName(Context context, String resourcesName) {
		return getResourcesIdByName(context, "layout", resourcesName);
	}
	/**
	 * 获取 color ID
	 * 
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int findColorIdByName(Context context, String resourcesName) {
		return getResourcesIdByName(context, "color", resourcesName);
	}
	/**
	 * 获取 array ID
	 * 
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int findArrayIdByName(Context context, String resourcesName) {
		return getResourcesIdByName(context, "array", resourcesName);
	}
	/**
	 * 获取String资源ID
	 * 
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int findStringIdByName(Context context, String resourcesName) {
		return getResourcesIdByName(context, "string", resourcesName);
	}

	public static String findStringByName(Context context, String resourcesName) {
		String res = "";
		try {
			res = context.getResources().getString(getResourcesIdByName(context, "string", resourcesName));
		} catch (Exception e) {
			PL.i( "resourcesName:" + resourcesName);
		}
		return res;
	}

	/**
	 * 获取view id资源
	 * 
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int findViewIdByName(Context context, String resourcesName) {
		return getResourcesIdByName(context, "id", resourcesName);
	}

	/**
	 * 获取drawable资源
	 * 
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int findDrawableIdByName(Context context, String resourcesName) {
		return getResourcesIdByName(context, "drawable", resourcesName);
	}

	/**
	 * 获取drawable资源
	 * 
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int findAnimIdByName(Context context, String resourcesName) {
		return getResourcesIdByName(context, "anim", resourcesName);
	}

	public static int findStyleIdByName(Context context, String resourcesName) {
		return getResourcesIdByName(context, "style", resourcesName);
	}

	/**
	 * 读取Assets中的文本
	 * 
	 * @param ctx
	 * @param filaName
	 * @return
	 */
	public static String getStringFromAssets(Context ctx, String filaName, String format) {
		try {
			InputStream is = ctx.getAssets().open(filaName + "." + format);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			return (new String(buffer)).trim();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取Application中的数据
	 * 
	 * @param context
	 * @param metadata
	 * @return
	 */
	public static JSONObject getApplicationMetaData(Context context, String... metadata) {
		ApplicationInfo appInfo = null;
		JSONObject dataObject = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (appInfo != null) {
				dataObject = new JSONObject();
				for (int i = 0; i < metadata.length; i++) {
					dataObject.put(metadata[i], appInfo.metaData.getString(metadata[i]));
				}
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataObject;
	}

	/**
	 * 获取Application中的数据
	 * 
	 * @param context
	 * @param metadata
	 * @return
	 */
	public static String getApplicationMetaData(Context context, String metadata) {
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (appInfo != null) {
			return appInfo.metaData.getString(metadata);
		}
		return null;
	}

	/**
	 * 获取Activity中的数据
	 * 
	 * @param context
	 * @param metadata
	 * @return
	 */
	public static String getActivityMetaData(Activity activity, String metadata) {
		ActivityInfo info = null;
		try {
			info = activity.getPackageManager().getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (info != null) {
			return info.metaData.getString(metadata);
		}
		return null;
	}

	/**
	 * 获取Activity中的数据
	 * 
	 * @param context
	 * @param metadata
	 * @return
	 */
	public static String getActivityMetaData(Context context, String metadata, Class<?> clazz) {
		ActivityInfo info = null;
		try {
			info = context.getPackageManager().getActivityInfo(new ComponentName(context, clazz), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (info != null) {
			return info.metaData.getString(metadata);
		}
		return null;
	}

	/**
	 * 获取Activity中的数据
	 * 
	 * @param context
	 * @param metadata
	 * @return
	 */
	public static JSONObject getActivityMetaData(Activity activity, String... metadata) {
		ActivityInfo info = null;
		JSONObject dataObject = null;
		try {
			info = activity.getPackageManager().getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
			if (info != null) {
				dataObject = new JSONObject();
				for (int i = 0; i < metadata.length; i++) {
					dataObject.put(metadata[i], info.metaData.getString(metadata[i]));
				}
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataObject;
	}

	/**
	 * 获取Service中的数据
	 * 
	 * @param context
	 * @param metadata
	 * @param serviceClass
	 * @return
	 */
	public static String getServiceMetaData(Context context, String metadata, Class<?> serviceClass) {
		ComponentName cn = new ComponentName(context, serviceClass);
		ServiceInfo info = null;
		try {
			info = context.getPackageManager().getServiceInfo(cn, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (info != null) {
			return info.metaData.getString(metadata);
		}
		return null;
	}

	/**
	 * 获取Application中的数据
	 * 
	 * @param context
	 * @param metadata
	 * @return
	 */
	public static String getReceiverMetaData(Context context, String metadata, Class<?> serviceClass) {
		ComponentName cn = new ComponentName(context, serviceClass);
		ActivityInfo info = null;
		try {
			info = context.getPackageManager().getReceiverInfo(cn, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (info != null) {
			return info.metaData.getString(metadata);
		}
		return null;
	}
}
