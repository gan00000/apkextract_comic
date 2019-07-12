package com.ssract.one.utils;

import android.app.Activity;
import android.app.ProgressDialog;

public class DialogUtil {

    public static ProgressDialog createDialog(Activity activity,CharSequence msg) {

        ProgressDialog loadingDialog = new ProgressDialog(activity);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setMessage(msg);

        return loadingDialog;
    }
}
