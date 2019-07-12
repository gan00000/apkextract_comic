package com.core.base.utils;

import android.util.Log;

/**
 * Created by gan on 2016/11/28.
 */

public class PL {

    public final static String PL_LOG = "PL_LOG";

    public static void d(String msg){
        d(PL_LOG,msg + "");
    }

    public static void d(String tag, String msg){
        Log.d(tag,msg + "");
    }

    public static void i(String msg){
        i(PL_LOG,msg + "");
    }

    public static void i(String tag, String msg){
        Log.i(tag,msg + "");
    }

    public static void w(String msg){
        Log.w(PL_LOG,msg + "");
    }

    public static void e(String tag, String msg){
        Log.e(tag,msg + "");
    }

    public static void e(String msg){
        Log.e(PL_LOG,msg + "");
    }
}
