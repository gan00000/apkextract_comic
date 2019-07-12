package com.core.base.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by gan on 2017/4/1.
 */

public class MarketUtil {


    /*
        Uri.parse("market://search?q=pub:Author Name"); //跳转到商店搜索界面，并搜索开发者姓名
        Uri.parse("market://search?q=Keyword"); //跳转到商店搜索界面，并搜索关键词

        优先通过商店打开，打不开就通过浏览器打开
    */
    public static boolean openMarket(Context context) {

        //这里开始执行一个应用市场跳转逻辑，默认this为Context上下文对象
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + context.getPackageName())); //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);

        //这里开始执行一个应用市场跳转逻辑，默认this为Context上下文对象
        Intent intent2 = new Intent(Intent.ACTION_VIEW);
        intent2.setData(Uri.parse("market://details?id=" + context.getPackageName())); //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
        intent2.setPackage("com.android.vending");   //设置市场商店为Google商店
        intent2.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);

        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        if (intent2.resolveActivity(context.getPackageManager()) != null){

            context.startActivity(intent2);

            //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        }else  if (intent.resolveActivity(context.getPackageManager()) != null) { //可以接收

            context.startActivity(intent);

        } else { //没有应用市场，我们通过浏览器跳转到Google Play
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
            //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
            if (intent.resolveActivity(context.getPackageManager()) != null) { //有浏览器
                context.startActivity(intent);
            } else { //天哪，这还是智能手机吗？
                PL.i("天啊，您没安装应用市场，连浏览器也没有，您买个手机干啥？");
                return false;
            }
        }
        return true;
    }
}
