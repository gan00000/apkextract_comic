package com.core.base.bean;

import android.content.Context;

import androidx.annotation.NonNull;

import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.SStringUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用反射进行拼接post请求，不能进行代码混淆
 * Created by gan on 2016/11/24.
 */

public class BaseReqeustBean extends AbsReqeustBean {

    private String androidId = "";
    private String imei = "";
    private String systemVersion = "";
    private String deviceType = "";
    private String mac = "";
    private String osLanguage = "";//系统语言


    private String packageName = "";
    private String versionCode = "";
    private String versionName = "";

    public BaseReqeustBean(Context context) {

        systemVersion = ApkInfoUtil.getOsVersion();
        deviceType = ApkInfoUtil.getDeviceType();
        androidId = ApkInfoUtil.getAndroidId(context);
        osLanguage = ApkInfoUtil.getOsLanguage();

        if (context != null) {
            imei = ApkInfoUtil.getImeiAddress(context);
            mac = ApkInfoUtil.getMacAddress(context);

            packageName = context.getPackageName();
            versionCode = ApkInfoUtil.getVersionCode(context);
            versionName = ApkInfoUtil.getVersionName(context);
        }
    }


    public Map<String, String> fieldValueToMap() {

        Map<String, String> postParams = new HashMap<String, String>();
        Class c = this.getClass();
        while (c != null && c != BaseReqeustBean.class.getSuperclass()) {

            Field[] fields = c.getDeclaredFields();
            Field.setAccessible(fields, true);

            for (int i = 0; i < fields.length; i++) {
                try {
                    Object value = null;
                    if (fields[i].get(this) != null) {
                        value = fields[i].get(this);
                        if (fields[i].getType() == String.class) {
                            postParams.put(fields[i].getName(), (String) value);
                        } else if (fields[i].getType() == int.class) {
                            postParams.put(fields[i].getName(), String.valueOf((int) value));
                        }

                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            c = c.getSuperclass();
        }
        return postParams;
    }

    public String fieldValueToString() {
        return SStringUtil.map2strData(fieldValueToMap());
    }


    public String createPreRequestUrl(){
        String s = getCompleteUrl();
        return getStyeUrl(s);
    }

    @NonNull
    private String getStyeUrl(String s) {
        if (SStringUtil.isNotEmpty(s)){
            String fieldParams = fieldValueToString();
            if (SStringUtil.isEmpty(fieldParams)){
                return s;
            }
            if (s.endsWith("?")){
                return s + fieldParams;
            }
            if (s.contains("?")){
                return s + "&" + fieldParams;
            }
            return s + "?" + fieldParams;
        }
        return "";
    }

    public String createSpaRequestUrl(){
        String s = getCompleteSpaUrl();
        return getStyeUrl(s);
    }

    public String[] createRequestUrls(){
        String[] m = new String[2];
        m[0] = createPreRequestUrl();
        m[1] = createSpaRequestUrl();
        return m;
    }

}
