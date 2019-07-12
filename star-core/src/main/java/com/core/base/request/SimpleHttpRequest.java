package com.core.base.request;

import com.core.base.bean.BaseReqeustBean;

/**
 * Created by gan on 2017/8/29.
 */

public class SimpleHttpRequest extends AbsHttpRequest {

    private BaseReqeustBean baseReqeustBean;

    public BaseReqeustBean getBaseReqeustBean() {
        return baseReqeustBean;
    }

    public void setBaseReqeustBean(BaseReqeustBean baseReqeustBean) {
        this.baseReqeustBean = baseReqeustBean;
    }


    @Override
    public BaseReqeustBean createRequestBean() {
        return baseReqeustBean;
    }
}
