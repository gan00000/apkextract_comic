package com.ccsky.util;

import android.app.Activity;
import android.text.TextUtils;

import com.ccsky.sfish.SkyConstant;
import com.ccsky.sfish.SkyPayActivity;

public class PayUtil {

    public static boolean isCanView(Activity activity){

        String sku = SPUtil.getSimpleString(activity, SkyConstant.SP_FILE_NAME, SkyConstant.SP_PAY_SKU_KEY);
        String payTime = SPUtil.getSimpleString(activity, SkyConstant.SP_FILE_NAME, SkyConstant.SP_PAY_DATE_KEY);

        if (TextUtils.isEmpty(sku) || TextUtils.isEmpty(payTime)){
            return false;
        }
        if (sku.equals(SkyPayActivity.sevenDay)){

           if (System.currentTimeMillis() - Long.parseLong(payTime) <= 7 * 24 * 60 * 60 * 1000){
               return true;
           }

        }else if (sku.equals(SkyPayActivity.fifteenDay)){

            if (System.currentTimeMillis() - Long.parseLong(payTime) <= 15 * 24 * 60 * 60 * 1000){
                return true;
            }

        }else if (sku.equals(SkyPayActivity.monthDay)){
            if (System.currentTimeMillis() - Long.parseLong(payTime) <= 30 * 24 * 60 * 60 * 1000){
                return true;
            }
        }else if (sku.equals(SkyPayActivity.oneDay)){
            if (System.currentTimeMillis() - Long.parseLong(payTime) <= 1 * 24 * 60 * 60 * 1000){
                return true;
            }
        }

        return false;
    }
}
