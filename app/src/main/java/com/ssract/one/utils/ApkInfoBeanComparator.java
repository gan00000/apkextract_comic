package com.ssract.one.utils;

import com.ssract.one.bean.ApkInfoBean;

import java.util.Comparator;

public class ApkInfoBeanComparator implements Comparator<ApkInfoBean> {


//    这里o1表示位于前面的对象，o2表示后面的对象
//
//    返回-1（或负数），表示不需要交换01和02的位置，o1排在o2前面，asc
//    返回1（或正数），表示需要交换01和02的位置，o1排在o2后面，desc


    @Override
    public int compare(ApkInfoBean apkInfoBean, ApkInfoBean apkInfoBean2) {

        int m = 0;
        if (apkInfoBean.isSystemApp()){
            m = 1;
        }

        int p = 0;
        if (apkInfoBean2.isSystemApp()){
            p = 1;
        }


        return m - p;
    }
}
