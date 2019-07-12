package com.core.base.cipher;

import android.text.TextUtils;
import android.util.Base64;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
* <p>Title: DESCipher</p>
* <p>Description: 加密解密类</p>
* @author GanYuanrong
* @date 2014年2月13日
*/
public class DESCipher {
	
	private final static String DES = "DES";


	/**
	* <p>Title: encryptDES</p>
	* <p>Description: 进行DES数据加密</p>
	* @param encryptData 需要加密的数据
	* @param cipherKey 加密key（至少8位）
	* @return  返回DES加密之后通过base64编码的数据
	*/
	public static String encryptDES(String encryptData,String cipherKey) {
		String result = null;
		try {
			SecureRandom random = new SecureRandom();//加密的强随机数生成器
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);//密匙工厂
			KeySpec keySpec = new DESKeySpec(cipherKey.getBytes());//密钥规范
			SecretKey secretKey = keyFactory.generateSecret(keySpec);//生成密钥
			Cipher cipher = Cipher.getInstance(DES);//加密器
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, random);
			byte[] byteResult = cipher.doFinal(encryptData.getBytes());
			String d = SStringUtil.binaryToHexString(byteResult);
			PL.d("byteResult:" + d);
			result = Base64.encodeToString(byteResult, Base64.DEFAULT);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	* <p>Title: decryptDES</p>
	* <p>Description: 解密DES加密之后通过base64编码的数据</p>
	* @param base64String ES加密之后通过base64编码的数据
	* @param cipherKey 解密key（需要与加密key相同）
	* @return 返回原始数据
	*/
	public static String decryptDES(String base64String, String cipherKey) {
		String result = null;
		try {
			SecureRandom random = new SecureRandom();
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			KeySpec keySpec = new DESKeySpec(cipherKey.getBytes());
			SecretKey secretKey = keyFactory.generateSecret(keySpec);
			Cipher cipher = Cipher.getInstance(DES);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, random);
			byte[] byteResult = cipher.doFinal(Base64.decode(base64String, Base64.DEFAULT));
			result = new String(byteResult);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private static final String IV_3DES = "10000000";


	public static String encrypt3DES(String plainText, String secretKey) throws Exception {
		if (TextUtils.isEmpty(plainText) || TextUtils.isEmpty(secretKey)) {
			return null;
		}
		DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		Key deskey = keyfactory.generateSecret(spec);
		IvParameterSpec ips = new IvParameterSpec(IV_3DES.getBytes("utf-8"));
		Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
		byte[] encryptData = cipher.doFinal(plainText.getBytes("utf-8"));
		String base64_ciphertext = Base64.encodeToString(encryptData, Base64.DEFAULT);
		return base64_ciphertext;

	}

	public static String decrypt3DES(String encryptText, String secretKey) throws Exception {
		if (TextUtils.isEmpty(encryptText) || TextUtils.isEmpty(secretKey)) {
			return null;
		}
		DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes("utf-8"));
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		Key deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
		IvParameterSpec ips = new IvParameterSpec(IV_3DES.getBytes("utf-8"));
		cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
		byte[] decryptData = cipher.doFinal(Base64.decode(encryptText, Base64.DEFAULT));
		return new String(decryptData, "utf-8");

	}

}
