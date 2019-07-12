package com.core.base.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by gan on 2017/2/7.
 */

public class FragmentUtil {

    public static void replace(FragmentManager manager, int id, Fragment fragment){
        if (manager == null || fragment == null)
            return;
        manager.beginTransaction().replace(id, fragment).commit();
    }


    public static void replaceBackStack(FragmentManager manager, int id, Fragment fragment){
        if (manager == null || fragment == null)
            return;
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(id, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public static void popBackStack(FragmentManager manager){
        if (manager == null)
            return;
        manager.popBackStack();
    }
}
