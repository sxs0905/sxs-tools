package com.suxiaoshuai.callback;

import okhttp3.Call;

public interface HttpUtilCallback {

    void success(Call call, String data);

    void fail(Call call, String errorMsg);
}
