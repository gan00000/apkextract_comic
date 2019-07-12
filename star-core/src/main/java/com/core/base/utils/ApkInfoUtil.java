package com.core.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.LocaleList;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

/**
 * 本地信息帮助类
 */
public class ApkInfoUtil {
	
	private static String customizedUniqueId = "";

	public static String getApplicationName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo = getApplicationInfo(context);
		String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	/**
	 * 获取当前的包信息
	 *
	 * @param context 上下文
	 * @return packageInfo
	 */
	public static ApplicationInfo getApplicationInfo(@NonNull Context context) {

		try {
			return context.getApplicationInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getNavigationBarHeight(Context context) {
	    Resources resources = context.getResources();
	    int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
	    int height = 0 ;
	    if(resourceId > 0){//2.4添加，过滤没有导航栏的设备
	    	height = resources.getDimensionPixelSize(resourceId);
	    }
		PL.i( "Navi height:" + height);
	    return height;
	}
	
	/**
	 * <p>Description: </p>
	 * @param ctx  
	 * @return
	 * @date 2015年10月12日
	 * 
	 */
	
	public static String getMacAddress(Context ctx) {

		/*String macTmp = "";
		try {
			WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			macTmp = info.getMacAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return macTmp;*/
		return "";
	}


	public static  String getCustomizedUniqueIdOrAndroidId(Context ctx){
		String s = getCustomizedUniqueId(ctx);
		if (TextUtils.isEmpty(s)){
			s = getAndroidId(ctx);
		}
		return s;
	}

	/**
	 * <p>Description: 获取植入SD卡的uuid</p>
	 * @param ctx
	 * @return
	 * @date 2015年10月12日
	 */
	private static synchronized String getCustomizedUniqueId(Context ctx) {

		if (!TextUtils.isEmpty(customizedUniqueId) && customizedUniqueId.length() >= 30) {
			return customizedUniqueId;
		}
		if (SdcardUtil.isExternalStorageExist()) {
			String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			String dataPath = sdcardPath + File.separator + "Android" + File.separator + "data" + File.separator;
			
			String dataTempPathDir = dataPath + "stardata" + File.separator;
			File dir = new File(dataTempPathDir);
			if (!dir.exists()) {
				if(!dir.mkdirs()){
					PL.w("没有添加android.permission.WRITE_EXTERNAL_STORAGE权限?");
				}
			}
			
			if (!dir.exists() || !dir.isDirectory()) {
				return "";
			}

			String dataTempPath = dataTempPathDir +"stardata-uuid.txt";

			try {
				customizedUniqueId = FileUtil.readFile(dataTempPath);
				if (!TextUtils.isEmpty(customizedUniqueId)) {
					return customizedUniqueId;
				}
				String uuid = UUID.randomUUID().toString();
				if (FileUtil.writeFileData(ctx, dataTempPath, uuid)) {
					customizedUniqueId = uuid;
				}
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return customizedUniqueId;

		}
		return "";
	}
	

	public static String getImeiAddress(Context ctx) {
		/*String imei = "";
		try {
			if (PermissionUtil.hasSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE)) {
				TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
				imei = telephonyManager.getDeviceId();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return imei;*/
		return "";
	}

	public static String getLocalIpAddress(Context ctx){
		WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
	    WifiInfo info = wifi.getConnectionInfo();
	    int ipInt = info.getIpAddress();
		String ipTmp = String.format("%d.%d.%d.%d", (ipInt & 0xff), (ipInt >> 8 & 0xff), (ipInt >> 16 & 0xff), (ipInt >> 24 & 0xff));

	    return ipTmp;

	}
	
	public static String getAndroidId(Context ctx) {
		String mAndroidId = "";
		try {
			mAndroidId = android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
			if (TextUtils.isEmpty(mAndroidId)) {
				mAndroidId = android.provider.Settings.System.getString(ctx.getContentResolver(), android.provider.Settings.System.ANDROID_ID);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mAndroidId;
	}
	
	public static boolean isNetworkAvaiable(Context ctx){
	    ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
	    return (info != null && info.isConnected());
	}

	
	/**
	* <p>Title: isWifiAvailable</p>
	* <p>Description: 判断wifi是否可用</p>
	* @param context
	* @return
	*/
	public static boolean isWifiAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfoActivity = connectivityManager.getActiveNetworkInfo();
		if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
			return false;
		} else {
			// NetworkInfo不为null开始判断是网络类型
			int netType = mobNetInfoActivity.getType();
			PL.i("netType:" + netType);
			if (netType == ConnectivityManager.TYPE_WIFI) {
				// wifi net处理
				return true;
			}
		}
		return false;
	}
	
	/**
	* <p>Title: getDeviceType</p>
	* <p>Description: 获取手机设备厂商和型号</p>
	* @return
	*/
	public static String getDeviceType(){
		String manufacturer = android.os.Build.MANUFACTURER;
		String modle = android.os.Build.MODEL;
		if (manufacturer == null) {
			manufacturer = "";
		}
		if (modle == null) {
			modle = "";
		}
		String deviceType =  manufacturer + "@@" + modle;
		
		return deviceType;
	}
	
	/**
	* <p>Title: getOsVersion</p>
	* <p>Description: 获取手机系统版本</p>
	* @return
	*/
	public static String getOsVersion(){
		String systemVersion = android.os.Build.VERSION.RELEASE;
		return systemVersion == null ? "" : systemVersion;
	}
	
	public static String getVersionCode(Context context){
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			String version = String.valueOf(info.versionCode);
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getVersionName(Context context){
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static int checkFastMobileNetwork(Context context) {

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return NetworkType.NET_TYPE_2G; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return NetworkType.NET_TYPE_2G; // ~ 14-64 kbps
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return NetworkType.NET_TYPE_2G; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return NetworkType.NET_TYPE_3G; // ~ 400-1000 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return NetworkType.NET_TYPE_3G; // ~ 600-1400 kbps
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return NetworkType.NET_TYPE_2G; // ~ 100 kbps
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return NetworkType.NET_TYPE_3G; // ~ 2-14 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return NetworkType.NET_TYPE_3G; // ~ 700-1700 kbps
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return NetworkType.NET_TYPE_3G; // ~ 1-23 Mbps
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return NetworkType.NET_TYPE_3G; // ~ 400-7000 kbps
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return NetworkType.NET_TYPE_3G; // ~ 1-2 Mbps
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return NetworkType.NET_TYPE_3G; // ~ 5 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return NetworkType.NET_TYPE_3G; // ~ 10-20 Mbps
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return NetworkType.NET_TYPE_2G; // ~25 kbps
		case TelephonyManager.NETWORK_TYPE_LTE:
			return NetworkType.NET_TYPE_4G; // ~ 10+ Mbps         4g
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return NetworkType.NET_TYPE_UNKNOW;
		default:
			return NetworkType.NET_TYPE_UNKNOW;

		}
	}
	
	public static class NetworkType{
		public static final int NET_TYPE_UNKNOW = 0;
		public static final int NET_TYPE_WIFI = 1;
		public static final int NET_TYPE_2G = 2;
		public static final int NET_TYPE_3G = 3;
		public static final int NET_TYPE_4G = 4;
	}
	/***
	 * 判断Network具体类型（wifi,2g,3g,4g）
	 * 
	 * */
	public static int getNetworkType(Context mContext) {
		final ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo mobNetInfoActivity = connectivityManager.getActiveNetworkInfo();
		if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
			return NetworkType.NET_TYPE_UNKNOW;
		}
		// NetworkInfo不为null开始判断是网络类型
		int netType = mobNetInfoActivity.getType();
		String netTypeName = mobNetInfoActivity.getTypeName();
		if (netType == ConnectivityManager.TYPE_WIFI && netTypeName.equalsIgnoreCase("WIFI")) {
			// wifi net处理
			return NetworkType.NET_TYPE_WIFI;
		} 
		try {
			if (netType == ConnectivityManager.TYPE_MOBILE) {
				int type_mobile = checkFastMobileNetwork(mContext);
				switch (type_mobile) {
				case NetworkType.NET_TYPE_2G:
					return NetworkType.NET_TYPE_2G;
				case NetworkType.NET_TYPE_3G:
					return NetworkType.NET_TYPE_3G;
				case NetworkType.NET_TYPE_4G:
					return NetworkType.NET_TYPE_4G;
				default:
					return NetworkType.NET_TYPE_UNKNOW;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetworkType.NET_TYPE_UNKNOW;
	}


	public static String getSimOperator(Context context){
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSimOperator();
	}

	
	public static String getLocaleLanguage(){
		return Locale.getDefault().getLanguage();
	}
	
	public static String getOsLanguage(){
		return getLocaleLanguage();
	}


	/**
	 *
	 * Android7.0及之后版本，使用了LocaleList，Configuration中的语言设置可能获取的不同，而是生效于各自的Context。
	 	这会导致：Android7.0使用就的方式，有些Activity可能会显示为手机的系统语言

	 如果使用新的方法，那么所有的Context都需要设置(包括Application)，并且设置：configuration.setLocales(new LocaleList(locale));
	 * @param context
	 * @param newLocale
	 */
	public static void updateConfigurationLocale(Context context, Locale newLocale) {
		if (null == newLocale){
			return;
		}
		Resources resources = context.getResources();//获得res资源对象
		Configuration config = resources.getConfiguration();//获得设置对象
		PL.i("old onConfigurationChanged:" + config.toString());

		DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
		config.locale = newLocale; //简体中文
		resources.updateConfiguration(config, dm);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//Android7.0及之后通过Context来设置语言
			config.setLocales(new LocaleList(newLocale));
			context.createConfigurationContext(config);
		}

		PL.i("new onConfigurationChanged:" + config.toString());

	}

	/**
	 //透明状态栏
	 getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	 //透明导航栏
	 getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

	 * 获取状态栏高度
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
				"android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 获取导航栏高度
	 * @param context
	 * @return
	 */
	public static int getNavBarHeight(Context context) {

		int resourceId = 0;
		int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
		if (rid != 0){
			resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
			PL.d("高度："+resourceId);
			PL.d("高度："+context.getResources().getDimensionPixelSize(resourceId) +"");
			return context.getResources().getDimensionPixelSize(resourceId);
		}
		return 0;
	}


	/*public static boolean isNavigationBarShow(Activity activity){

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

			Display display = activity.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			Point realSize = new Point();
			display.getSize(size);//不包括虚拟导航栏高度
			display.getRealSize(realSize);//包括虚拟导航栏高度

			if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

				return realSize.x != size.x;

			}else {

				return realSize.y != size.y;
			}
		}else {
			boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
			boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
			if(menu || back) {
				return false;
			}else {
				return true;
			}
		}
	}*/

}
