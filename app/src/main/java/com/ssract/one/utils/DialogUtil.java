package com.ssract.one.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {

    public static ProgressDialog createDialog(Activity activity,CharSequence msg) {

        ProgressDialog loadingDialog = new ProgressDialog(activity);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setMessage(msg);

        return loadingDialog;
    }

    public static void alert(Activity activity,String msg, int textId, DialogInterface.OnClickListener onClickListener){

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(msg)
                .setPositiveButton(textId,onClickListener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();

        alertDialog.show();
    }

    public static void alertOk(Activity activity,String msg, DialogInterface.OnClickListener onClickListener){

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(msg)
                .setPositiveButton("OK",onClickListener)
                .create();

        alertDialog.show();
    }
}
