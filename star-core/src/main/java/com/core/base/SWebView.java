package com.core.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.core.base.js.Native2JS;
import com.core.base.utils.PL;

import java.util.Map;

public class SWebView extends SBaseWebView {

	private static final String AndroidNativeJs = "AndroidNativeJs";

	private Native2JS native2js;

	/**
	 * @param jsObject the jsObject to set
	 */
	public void setJsObject(Native2JS jsObject) {
		this.native2js = jsObject;
		this.addJavascriptInterface(native2js, AndroidNativeJs);
	}

	public SWebView(Context context) {
		super(context);
		initJavaScript();
	}

	@SuppressLint("NewApi")
	public SWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initJavaScript();
	}

	public SWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initJavaScript();
	}

	public SWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initJavaScript();
	}


	public void executeJavascript(String scriptName){
		this.loadUrl("javascript:" + scriptName);
	}

	
	@SuppressLint("SetJavaScriptEnabled")
	private void initJavaScript(){
		native2js = new Native2JS(getContext());
	}


	public void jsCallBack(String msg){
		PL.i("jsCallBack:" + msg);
	}
	

	@Override
	public void loadUrl(String url) {
//		this.addJavascriptInterface(native2js, AndroidNativeJs);
		super.loadUrl(url);
	}
	
	@Override
	public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
//		this.addJavascriptInterface(native2js, AndroidNativeJs);
		super.loadUrl(url, additionalHttpHeaders);
	}
	
	@Override
	public void loadData(String data, String mimeType, String encoding) {
//		this.addJavascriptInterface(native2js, AndroidNativeJs);
		super.loadData(data, mimeType, encoding);
	}
}
