package com.core.base.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

public class SignatureUtil {
	
	/**
	 * <p>Description: 获取APK包的Key Hash</p>
	 * @author GanYuanrong
	 * @param context
	 * @param pkgName
	 * @return
	 * @date 2014年12月5日
	 */
	public static String getHashKey(Context context,String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return "";
		}
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				return Base64.encodeToString(md.digest(), Base64.DEFAULT);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	

	/**
	 * <p>Description: 获取签名的MD5</p>
	 * @author GanYuanrong
	 * @param context
	 * @param pkgName
	 * @return
	 * @date 2014年12月5日
	 */
	public static String getSignatureMD5(Context context,String pkgName) {
		Signature[] arrayOfSignature = getRawSignature(context, pkgName);
		if ((arrayOfSignature == null) || (arrayOfSignature.length == 0)) {
			PL.e("signs is null");
			return "";
		}
		if (arrayOfSignature.length == 1) {
			return getMessageDigest(arrayOfSignature[0].toByteArray(),"MD5");
		}
		PL.w("arrayOfSignature.length:" + arrayOfSignature.length);
		return "";
	}
	
	/**
	 * <p>Description: 获取APK包的签名sha1</p>
	 * @author GanYuanrong
	 * @param context 上下文对象
	 * @param pkgName 包名
	 * @return 返回apk签名的sha1,不包括冒号
	 * @date 2014年12月5日
	 */
	public static String getSignatureSHA1(Context context,String pkgName) {
		Signature[] arrayOfSignature = getRawSignature(context, pkgName);
		if ((arrayOfSignature == null) || (arrayOfSignature.length == 0)) {
			PL.e("signs is null");
			return "";
		}
		if (arrayOfSignature.length == 1) {
			return getMessageDigest(arrayOfSignature[0].toByteArray(),"SHA-1");
		}
		PL.w("arrayOfSignature.length:" + arrayOfSignature.length);
		return "";
	}
	

	/**
	 * <p>Description: 获取APK包的签名sha1</p>
	 * @author GanYuanrong
	 * @param context 上下文对象
	 * @param pkgName 包名
	 * @return  返回apk签名的sha1,包括冒号
	 * @date 2014年12月5日
	 */
	public static String getSignatureSHA1WithColon(Context context,String pkgName){
		String sha1 = getSignatureSHA1(context, pkgName);
		if (!TextUtils.isEmpty(sha1) && sha1.length() > 2 && !sha1.contains(":")) {
			StringBuilder stringBuilder = new StringBuilder(sha1);
			for (int i = 2; i < stringBuilder.length(); i = i+2+1) {
				stringBuilder.insert(i, ':');
			}
			
			return stringBuilder.toString();
		}
		
		return "";
		
	}
	
	public static Signature[] getRawSignature(Context context, String pkgName) {
		PackageInfo localPackageInfo;
		if ((pkgName == null) || (pkgName.length() == 0)) {
			PL.e("getSignature, packageName is null");
			return null;
		}
		PackageManager localPackageManager = context.getPackageManager();
		try {
			localPackageInfo = localPackageManager.getPackageInfo(pkgName, 64);
			if (localPackageInfo == null) {
				PL.e("package info is null, packageName = " + pkgName);
				return null;
			}
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			PL.e("NameNotFoundException");
			return null;
		}
		return localPackageInfo.signatures;
	}

	
	public static  String getMessageDigest(byte[] paramArrayOfByte,String algorithm) {
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance(algorithm);
			localMessageDigest.update(paramArrayOfByte);
			byte[] resultArrayOfCharArray = localMessageDigest.digest();

			return byteArrayToHex(resultArrayOfCharArray);

		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return null;
	}

	public static String byteArrayToHex(byte[] byteArray) {

		// 首先初始化一个字符数组，用来存放每个16进制字符

		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

		// new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））

		char[] resultCharArray = new char[byteArray.length * 2];

		// 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去

		int index = 0;

		for (byte b : byteArray) {

			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];

			resultCharArray[index++] = hexDigits[b & 0xf];

		}

		// 字符数组组合成字符串返回

		return new String(resultCharArray);

	}
}
