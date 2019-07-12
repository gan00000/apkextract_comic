package com.core.base.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by gan on 2016/11/24.
 */

public abstract class AbsReqeustBean implements Serializable{

    private String requestUrl = "";
    private String requestSpaUrl = "";
    private String requestMethod = "";
    private String completeUrl = "";

    private String completeSpaUrl = "";

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }



    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getCompleteUrl() {
        if (TextUtils.isEmpty(completeUrl) && !TextUtils.isEmpty(requestUrl) && !TextUtils.isEmpty(requestMethod)){
            completeUrl = requestUrl + requestMethod;
        }
        return completeUrl;
    }

    public void setCompleteUrl(String completeUrl) {
        this.completeUrl = completeUrl;
    }

    public String getCompleteSpaUrl() {

        if (TextUtils.isEmpty(completeSpaUrl) && !TextUtils.isEmpty(requestSpaUrl) && !TextUtils.isEmpty(requestMethod)){
            completeSpaUrl = requestSpaUrl + requestMethod;
        }
        return completeSpaUrl;
    }

    public void setCompleteSpaUrl(String completeSpaUrl) {
        this.completeSpaUrl = completeSpaUrl;
    }

    public String getRequestSpaUrl() {
        return requestSpaUrl;
    }

    public void setRequestSpaUrl(String requestSpaUrl) {
        this.requestSpaUrl = requestSpaUrl;
    }


}
