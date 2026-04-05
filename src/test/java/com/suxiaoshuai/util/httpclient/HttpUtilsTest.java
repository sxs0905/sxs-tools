package com.suxiaoshuai.util.httpclient;

import com.suxiaoshuai.util.http.HttpResponse;
import com.suxiaoshuai.util.http.HttpUtils;
import com.suxiaoshuai.util.json.JsonUtil;
import org.junit.jupiter.api.Test;


class HttpUtilsTest {

    @Test
    void get() {
        HttpResponse httpResponse = HttpUtils.get("http://www.baidu.com");
        System.out.println(JsonUtil.toJson(httpResponse));
    }

    @Test
    void testGet() {
    }

    @Test
    void testGet1() {
    }

    @Test
    void post() {
    }

    @Test
    void testPost() {
    }

    @Test
    void postForm() {
    }

    @Test
    void testPostForm() {
    }
}