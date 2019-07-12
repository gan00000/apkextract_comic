package com.core.base.request;

import android.app.Dialog;
import android.text.TextUtils;

import com.core.base.callback.ISReqCallBack;
import com.core.base.http.HttpRequest;
import com.core.base.http.HttpResponse;
import com.core.base.bean.BaseReqeustBean;
import com.core.base.bean.BaseResponseModel;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;


public abstract class AbsHttpRequest implements ISRqeust {

    private HttpResponse coreHttpResponse;

    private Dialog loadDialog;

    private ISReqCallBack reqCallBack;

    public void setReqCallBack(ISReqCallBack reqCallBack) {
        this.reqCallBack = reqCallBack;
    }


    public <T> void excute(final Type mTypeOfT) {

        SRequestAsyncTask asyncTask = new SRequestAsyncTask() {

            T responseModule = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadDialog != null && !loadDialog.isShowing()){
                    loadDialog.show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                BaseReqeustBean baseReqeustBean = createRequestBean();
                if (baseReqeustBean == null) {
                    return "";
                }
                String rawResponse = doRequest(baseReqeustBean);

                //解析json数据
                if (!TextUtils.isEmpty(rawResponse) && mTypeOfT != null && JsonUtil.isJson(rawResponse)) {
                    Gson gson = new Gson();
                    responseModule = gson.fromJson(rawResponse, mTypeOfT);
                    if (responseModule != null && (responseModule instanceof BaseResponseModel)) {
                        ((BaseResponseModel) responseModule).setRawResponse(rawResponse);
                    }

                }
                return rawResponse;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (loadDialog != null && loadDialog.isShowing()){
                    loadDialog.dismiss();
                }

                if (coreHttpResponse != null) {
                    if (coreHttpResponse.getHttpResponseCode() != HttpURLConnection.HTTP_OK) {
                        onTimeout(coreHttpResponse.getHttpResponseCode() + "");
                        if (reqCallBack != null){
                            reqCallBack.timeout(coreHttpResponse.getHttpResponseCode() + "");
                        }
                    } else if (TextUtils.isEmpty(result)) {
                        onNoData(coreHttpResponse.getRequestCompleteUrl());
                        if (reqCallBack != null){
                            reqCallBack.noData();
                        }
                    } else {
                        onHttpSucceess(responseModule);
                        if (reqCallBack != null){
                            reqCallBack.success(responseModule,result);
                        }
                    }
                }

            }
        };

        asyncTask.asyncExcute();
    }

    @Override
    public void excute() {
        excute(BaseResponseModel.class);
    }

    @Override
    public <T> void excute(final Class<T> classOfT) {

        SRequestAsyncTask asyncTask = new SRequestAsyncTask() {

            T responseModule = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadDialog != null && !loadDialog.isShowing()){
                    loadDialog.show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                BaseReqeustBean baseReqeustBean = createRequestBean();
                if (baseReqeustBean == null) {
                    return "";
                }
                String rawResponse = doRequest(baseReqeustBean);

                //解析json数据
                if (!TextUtils.isEmpty(rawResponse) && classOfT != null && JsonUtil.isJson(rawResponse)) {
                    try {
                        Gson gson = new Gson();
                        responseModule = gson.fromJson(rawResponse, classOfT);
                        if (responseModule != null && (responseModule instanceof BaseResponseModel)) {
                            ((BaseResponseModel) responseModule).setRawResponse(rawResponse);
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        PL.e("json解析出错，responseModule = gson.fromJson(rawResponse, classOfT)");
                    }

                }
                return rawResponse;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (loadDialog != null && loadDialog.isShowing()){
                    loadDialog.dismiss();
                }

                if (coreHttpResponse != null) {
                    if (coreHttpResponse.getHttpResponseCode() != HttpURLConnection.HTTP_OK) {
                        onTimeout(coreHttpResponse.getHttpResponseCode() + "");
                        if (reqCallBack != null){
                            reqCallBack.timeout(coreHttpResponse.getHttpResponseCode() + "");
                        }
                    } else if (TextUtils.isEmpty(result)) {
                        onNoData(coreHttpResponse.getRequestCompleteUrl());
                        if (reqCallBack != null){
                            reqCallBack.noData();
                        }
                    } else {
                        onHttpSucceess(responseModule);
                        if (reqCallBack != null){
                            reqCallBack.success(responseModule,result);
                        }
                    }
                }

            }
        };

        asyncTask.asyncExcute();
    }

    /**
     * <p>Title: doRequest</p> <p>Description: 实际网络请求</p>
     */
    public String doRequest(BaseReqeustBean baseReqeustBean) {
        if (SStringUtil.isNotEmpty(baseReqeustBean.getCompleteUrl())) {

            if (isGetMethod) {
                if (isNeedGetParams){

                    coreHttpResponse = HttpRequest.getReuqest(baseReqeustBean.getCompleteUrl(),baseReqeustBean.fieldValueToMap());
                }else{

                    coreHttpResponse = HttpRequest.getReuqestIn2Url(baseReqeustBean.getCompleteUrl(),baseReqeustBean.getCompleteSpaUrl());
                }

            }else{
                coreHttpResponse = HttpRequest.postIn2Url(baseReqeustBean.getCompleteUrl(), baseReqeustBean.getCompleteSpaUrl(), baseReqeustBean.fieldValueToMap());
            }
            if (coreHttpResponse != null) {
                return coreHttpResponse.getResult();
            }
        }
        return "";
    }

    public void setLoadDialog(Dialog loadDialog) {
        this.loadDialog = loadDialog;
    }

    private boolean isGetMethod = false;
    private boolean isNeedGetParams = false;


    public void setGetMethod(boolean getMethod, boolean isNeedGetParams) {
        isGetMethod = getMethod;
        this.isNeedGetParams = isNeedGetParams;
    }

    @Override
    public <T> void onHttpSucceess(T responseModel) {

    }

    @Override
    public void onTimeout(String result) {

    }

    @Override
    public void onNoData(String result) {

    }
}
