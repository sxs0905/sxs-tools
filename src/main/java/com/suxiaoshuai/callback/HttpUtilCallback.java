package com.suxiaoshuai.callback;

import okhttp3.Call;

/**
 * HTTP 工具类的回调接口
 */
public interface HttpUtilCallback {

    /**
     * HTTP 请求成功的回调方法
     *
     * @param call HTTP 请求的 Call 对象
     * @param data 响应数据字符串
     */
    void success(Call call, String data);

    /**
     * HTTP 请求失败的回调方法
     *
     * @param call     HTTP 请求的 Call 对象
     * @param errorMsg 错误信息
     */
    void fail(Call call, String errorMsg);
}
