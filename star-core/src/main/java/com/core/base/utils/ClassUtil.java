package com.core.base.utils;

/**
 * Created by gan on 2017/2/14.
 */

public class ClassUtil {

    public static boolean existClass(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
