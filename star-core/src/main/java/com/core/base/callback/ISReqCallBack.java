package com.core.base.callback;

/**
 * Created by gan on 2017/2/11.
 */
public interface ISReqCallBack<T> extends ISCallBack<T> {

    void success(T t, String rawResult);

    void timeout(String code);

    void noData();


}
