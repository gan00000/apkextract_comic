package com.core.base.http;

import java.util.List;
import java.util.Map;

public class HttpResponse {
	
	/**
	 *  当前get或者post请求时服务器时间
	 */
	private String serverTime;
	
	/**
	 *  当前get或者post请求的头信息
	 */
	private Map<String, List<String>> heads;
	
	private String result = "";
	
	private String requestCompleteUrl = "";
	
	private int httpResponseCode = 0;
	
	/**
	 * @return the serverTime
	 */
	public String getServerTime() {
		return serverTime;
	}

	/**
	 * @return the heads
	 */
	public Map<String, List<String>> getHeads() {
		return heads;
	}

	/**
	 * @param serverTime the serverTime to set
	 */
	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}

	/**
	 * @param heads the heads to set
	 */
	public void setHeads(Map<String, List<String>> heads) {
		this.heads = heads;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getRequestCompleteUrl() {
		return requestCompleteUrl;
	}

	public void setRequestCompleteUrl(String requestCompleteUrl) {
		this.requestCompleteUrl = requestCompleteUrl;
	}

	public int getHttpResponseCode() {
		return httpResponseCode;
	}

	public void setHttpResponseCode(int httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}


}
