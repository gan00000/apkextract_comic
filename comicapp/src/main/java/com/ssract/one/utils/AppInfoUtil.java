package com.ssract.one.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

public class AppInfoUtil {


    public static boolean isSystemApp(PackageInfo pInfo) {
        return (((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) && ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0));
    }
}
