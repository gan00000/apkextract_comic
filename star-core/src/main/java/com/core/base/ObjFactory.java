package com.core.base;

import com.core.base.utils.PL;

/**
 * Created by gan on 2017/4/14.
 */

public class ObjFactory {

    public static <T> T create(Class<T> t){
        try {
            return t.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        PL.w("ObjFactory create error!!!");
        return null;
    }

}
