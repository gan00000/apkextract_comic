package com.core.base.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by gan on 2016/12/23.
 */

public class BaseResponseModel implements Serializable {

    public static final String SUCCESS_CODE = "1000";

    private String rawResponse;
    private String code = "";

    private String message = "";

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setRawResponse(String rawResponse){
        this.rawResponse = rawResponse;

        try {
            JSONObject jsonObject = new JSONObject(rawResponse);
            code = jsonObject.optString("code","");
            message = jsonObject.optString("message","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isRequestSuccess(){
        return SUCCESS_CODE.equals(code);
    }

    public String getRawResponse() {
        return rawResponse;
    }

}
