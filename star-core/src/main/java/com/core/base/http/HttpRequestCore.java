package com.core.base.http;

import android.text.TextUtils;
import android.util.Log;

import com.core.base.utils.FileUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestCore{
	
	private static final String TAG = "HttpRequestCore";
	private String requestUrl;
	private int connectTimeout = 30 * 1000;
	private String requestMethod = HttpReuqestMethod.POST;
	private String contentType = "application/x-www-form-urlencoded";
	private String sendData;
	private HttpResponse httpResponse = new HttpResponse();

	public Map<String, String> getHeaderMap() {
		return headerMap;
	}

	private Map<String,String> headerMap = new HashMap<>();

	private byte[] postByteData;

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * @return the httpResponse
	 */
	public HttpResponse getHttpResponse() {
		return httpResponse;
	}

	/**
	 * @param requestUrl the requestUrl to set
	 */
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	/**
	 * @param connectTimeout the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @param requestMethod the requestMethod to set
	 */
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	/**
	 * @param sendData the sendData to set
	 */
	public void setSendData(String sendData) {
		this.sendData = sendData;
	}

	
	public HttpResponse excuteGetRequest(String urlStr,Map<String, String> dataMap) {
		if (TextUtils.isEmpty(urlStr)) {
			return null;
		}

		if (dataMap == null || dataMap.isEmpty()) {
			return excuteGetRequest(urlStr);
		}
		String getData = SStringUtil.map2strData(dataMap);
		if (TextUtils.isEmpty(getData)) {
			return excuteGetRequest(urlStr);
		}
		String getUrl = "";
		if (urlStr.endsWith("?")) {
			getUrl = urlStr + getData;
		}else{
			getUrl = urlStr + "?" + getData;
		}
		PL.d("http request:" + getUrl);
		return excuteGetRequest(getUrl);
		
	}
	
	public HttpResponse excuteGetRequest(String urlStr) {
		this.requestUrl = urlStr;
		doGet();
		httpResponse.setRequestCompleteUrl(urlStr);
		return httpResponse;
	}
	

	public HttpResponse excutePostRequest(String urlStr,Map<String, String> dataMap) {
		
		String postData = SStringUtil.map2strData(dataMap);
		return excutePostRequest(urlStr, postData);
	
	}
	
	public HttpResponse excutePostRequest(String urlStr, String postData) {
		if (TextUtils.isEmpty(urlStr)) {
			return httpResponse;
		}
		this.requestUrl = urlStr;
		this.sendData = postData;
		PL.i("http request:" + urlStr + "?" + postData);
		doPost();
		httpResponse.setRequestCompleteUrl(urlStr + "?" + postData);
		return httpResponse;
	}
	
	
	public String postByteData(String urlStr,byte[] postByteData) {
		if (TextUtils.isEmpty(urlStr)) {
			return "";
		}
		this.requestUrl = urlStr;
		this.postByteData = postByteData;
		return doPost().getResult();
	}
	
	
	public HttpResponse doPost(){
		this.requestMethod =  HttpReuqestMethod.POST;
		return doReuqest();
	}
	
	public HttpResponse doGet(){
		this.requestMethod =  HttpReuqestMethod.GET;
		return doReuqest();
	}

//	TrustManager tm = new X509TrustManager() {
//		public void checkClientTrusted(X509Certificate[] chain, String authType)
//				throws CertificateException {
//			//do nothing，接受任意客户端证书
//		}
//
//		public void checkServerTrusted(X509Certificate[] chain, String authType)
//				throws CertificateException {
//			//do nothing，接受任意服务端证书
//		}
//
//		public X509Certificate[] getAcceptedIssuers() {
//			return null;
//		}
//	};


	public HttpResponse doReuqest(){

		if (TextUtils.isEmpty(requestUrl)) {
			return httpResponse;
		}
		checkHttpsUrl(requestUrl);
		HttpURLConnection urlConnection = null;
		String result = "";
		try {
			URL url = new URL(requestUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
//			if (urlConnection instanceof HttpsURLConnection){
//				HttpsURLConnection httpsURLConnection = (HttpsURLConnection)urlConnection;
//
//				// Create an SSLContext that uses our TrustManager
//				SSLContext context = SSLContext.getInstance("TLS");
//				context.init(null, new TrustManager[] { tm }, null);
//
//				httpsURLConnection.setSSLSocketFactory(context.getSocketFactory());
//
//				httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
//					@Override
//					public boolean verify(String hostname, SSLSession session) {
//						return true;
//					}
//				});
//			}

			if (!TextUtils.isEmpty(sendData) && !HttpReuqestMethod.GET .equals(requestMethod)) {
				urlConnection.setDoOutput(true);
			}
			//urlConnection.setChunkedStreamingMode(0);
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(connectTimeout);
			urlConnection.setRequestMethod(requestMethod);
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("accept", "*/*");
			urlConnection.setRequestProperty("Content-Type",contentType);

			if (!headerMap.isEmpty()){

				for (Map.Entry<String, String> entry : headerMap.entrySet()) {
					String mapValue = entry.getValue();
					if (SStringUtil.isNotEmpty(mapValue)) {
						urlConnection.setRequestProperty(entry.getKey(),mapValue);
					}
				}

			}
			urlConnection.connect();
			
			if (!TextUtils.isEmpty(sendData) && !HttpReuqestMethod.GET.equals(requestMethod)) {//post请求需要写文件流
				writeStream(urlConnection.getOutputStream(), sendData);
			}else if(postByteData != null && !HttpReuqestMethod.GET.equals(requestMethod)){
				writeStream(urlConnection.getOutputStream(), postByteData);
			}
			
			int code = urlConnection.getResponseCode();
			httpResponse.setHttpResponseCode(code);
			String message = urlConnection.getResponseMessage();
			PL.i("request code:" + code + ",code message:" + message);
			if (code == HttpURLConnection.HTTP_OK) {
				result = readStream(urlConnection.getInputStream());
			}
			//SLogUtil.logD(result);
			if (httpResponse != null) {
				httpResponse.setResult(result);
				httpResponse.setServerTime(urlConnection.getHeaderField("Date"));
				httpResponse.setHeads(urlConnection.getHeaderFields());
				PL.i("request url:" + requestUrl + " --> result:" + httpResponse.getResult());
			}
			
		}catch (IOException e) {
			Log.d(TAG, "e:" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
				Log.d(TAG, "urlConnection.disconnect()");
			}
			urlConnection= null;
		}
		return httpResponse;
	}
	

	/**
	 * <p>Description: </p>
	 * @param data  格式必须为（key=value&key1=value1）
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @date 2015年10月9日
	 */
	private void writeStream(OutputStream outputStream, String data) throws UnsupportedEncodingException, IOException {
		if (outputStream == null || TextUtils.isEmpty(data)) {
			return;
		}
		writeStream(outputStream, data.getBytes("UTF-8"));
	}

	private void writeStream(OutputStream outputStream, byte[] byteData) throws UnsupportedEncodingException, IOException {
		if (outputStream == null || byteData == null) {
			return;
		}
		OutputStream os = new BufferedOutputStream(outputStream);
		os.write(byteData);
		os.flush();
		os.close();
	}
	
/*	public String readStream1(InputStream inputStream) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		
		StringBuilder data = new StringBuilder();
		int offset = 0;
		byte[] buffer = new byte[2048];
		while ((offset = bis.read(buffer, 0, buffer.length)) != -1) {
			String string = new String(buffer, 0, offset,"UTF-8");
			data.append(string);
			offset = 0;
		}
		bis.close();
		
		return data.toString();
	}*/

	
/*	readStream1 发现可能是由于我采用字节流从网络读取数据，且每次读取2048个字节，读取完成后能后强制转化为字符串，又因为使用编码为UTF-8，
	UTF-8是一种变长码（英文1个字节，中文两个字节），
	所以2048可能会造成刚好截取了某个汉字的一半（前一个字节），然后转化为字符串时造成乱码。
	于是把读取数据的代码从字节流改成字符流，修改后的代码为readStream:
	
	一个char 可以保持一个汉字。 
	对于字符还可以使用：
	BufferedReader buff = new BufferedReader(new InputStreamReader(is));
	String temp = null;
	StringBuffer sb = new StringBuffer();
	while( ( temp = buff.readLine()) != null ){
	sb.append(temp);
	}
	*/
	private String readStream(InputStream inputStream) throws IOException {
		InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
		StringBuilder data = new StringBuilder();
		int offset = 0;
		char[] buffer = new char[2048];
		while ((offset = isr.read(buffer, 0, buffer.length)) != -1) {
			data.append(new String(buffer, 0, offset));
			offset = 0;
		}
		isr.close();
		
		return data.toString();
	}

	public byte[] getPostByteData() {
		return postByteData;
	}

	public void setPostByteData(byte[] postByteData) {
		this.postByteData = postByteData;
	}

	
	public boolean downLoadUrlFile(String downLoadFileUrl,String savePath) {
		HttpURLConnection conn = null;
		FileOutputStream fileOutputStream = null;
		InputStream is = null;
		try {
			PL.i("start down load...");
			checkHttpsUrl(downLoadFileUrl);
			PL.i("http request:" + downLoadFileUrl);
			URL url = new URL(downLoadFileUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Referer", downLoadFileUrl); 
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();
			int code = conn.getResponseCode();
			PL.i("域名文件 http response code:" + code);
			if (200 == code) {
				File saveFile = new File(savePath);
				FileUtil.deleteFile(saveFile);
				if (!saveFile.exists()) {
					if(saveFile.getParentFile()!= null){
						saveFile.getParentFile().mkdirs();
					}
					saveFile.createNewFile();
				}
				
				fileOutputStream = new FileOutputStream(saveFile);
				is = conn.getInputStream();
				int offset = 0;
				byte[] buffer = new byte[2048];
				while ((offset = is.read(buffer, 0, buffer.length)) != -1) {
					fileOutputStream.write(buffer,0,offset);
					offset = 0;
				}
				fileOutputStream.flush();
				return true;
			}
		} catch (IOException e) {
			PL.i("exception:" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return false;
	}
	
	/**
	 * <p>Description: 添加非https 请求地址的判断（之后需要全部改为https）</p>
	 * @param url
	 * @date 2016年10月26日
	 */
	public static void checkHttpsUrl(String url) {
		if (!TextUtils.isEmpty(url) && !url.startsWith("https")) {
			//Log.e(TAG, "sdk warming: " + url + " is not https url, please check that is it correct ??");
		}
	}
}
