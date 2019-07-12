package com.core.base.http;

import java.io.File;
import java.util.Map;

import org.json.JSONObject;

import com.core.base.utils.SStringUtil;

import android.text.TextUtils;

public class HttpRequest {
	
	public HttpRequest() {
		// TODO Auto-generated constructor stub
	}

	public static String get(String urlStr,Map<String, String> dataMap) {
		HttpRequestCore requestCore = new HttpRequestCore();
		return requestCore.excuteGetRequest(urlStr,dataMap).getResult();
	}

	public static String get(String urlStr) {
		return getReuqest(urlStr).getResult();
	}
	

	public static HttpResponse getReuqestIn2Url(String preUrl,String spaUrl) {
		HttpResponse s = getReuqest(preUrl);
		if (s != null && !TextUtils.isEmpty(s.getResult())) {
			return s;
		}
		if (SStringUtil.isNotEmpty(spaUrl)) {
			s = getReuqest(spaUrl);
		}
		return s;
	}

	public static HttpResponse getReuqest(String urlStr) {
		HttpRequestCore requestCore = new HttpRequestCore();
		return requestCore.excuteGetRequest(urlStr);
	}

	public static HttpResponse getReuqest(String urlStr, Map<String, String> dataMap) {
		HttpRequestCore requestCore = new HttpRequestCore();
		return requestCore.excuteGetRequest(urlStr,dataMap);
	}
	
	/**
	 * <p>Description: 发送post请求</p>
	 * @param urlStr 请求地址
	 * @return  请求的结果
	 * @date 2015年10月9日
	 */
	public static String post(String urlStr,Map<String, String> dataMap) {
		HttpResponse hr = postReuqest(urlStr, dataMap);
		if (hr != null) {
			return hr.getResult();
		}else{
			return "";
		}
	}
	
	public  static HttpResponse postReuqest(String urlStr,Map<String, String> dataMap) {
		HttpRequestCore requestCore = new HttpRequestCore();
		return requestCore.excutePostRequest(urlStr, dataMap);
	}
	
	
	public static String post(String urlStr,byte[] dataByte) {
		HttpRequestCore requestCore = new HttpRequestCore();
		return requestCore.postByteData(urlStr, dataByte);
	}
	
	
	public static HttpResponse postIn2Url(String preUrl,String spaUrl,Map<String, String> dataMap) {

		HttpResponse mHttpResponse = postReuqest(preUrl, dataMap);
		if (mHttpResponse != null && SStringUtil.isNotEmpty(mHttpResponse.getResult())) {
			return mHttpResponse;
		}
		if (SStringUtil.isNotEmpty(spaUrl)) {
			mHttpResponse = postReuqest(spaUrl, dataMap);
		}
		return mHttpResponse;

	}

	/**
	 * 上传文件
	 * @param params
	 *            传递的普通参数
	 * @param uploadFile
	 *            需要上传的文件名
	 * @param newFileName
	 *            上传的文件名称，不填写将为uploadFile的名称
	 * @param urlStr
	 *            上传的服务器的路径
	 */
	public static String uploadFile(Map<String, String> params, File uploadFile, String newFileName, String urlStr){
		return HttpFileUploadRequest.uploadFile(params, uploadFile, newFileName, urlStr);
	}


	public static String postIn2Url(String preUrl,String spaUrl,String mInterfaceName,Map<String, String> dataMap) {
		preUrl = SStringUtil.checkUrl(preUrl) + mInterfaceName;
		String result = post(preUrl, dataMap);

		if (TextUtils.isEmpty(result)) {//备用域名请求
			spaUrl = SStringUtil.checkUrl(spaUrl) + mInterfaceName;
			result = post(spaUrl, dataMap);
		}
		return result;
	}

	public static String postJsonObject(String urlStr,JSONObject jsonObject) {
		HttpRequestCore requestCore = new HttpRequestCore();
		requestCore.setSendData(jsonObject.toString());
		requestCore.setRequestUrl(urlStr);
		requestCore.setContentType("application/json");
		return requestCore.doPost().getResult();
	}


	public static boolean downLoadUrlFile(String downLoadFileUrl,String savePath) {
		HttpRequestCore requestCore = new HttpRequestCore();
		return requestCore.downLoadUrlFile(downLoadFileUrl, savePath);
	}

}
