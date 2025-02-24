package com.suxiaoshuai.util.httpclient;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpUtilsTest {

    @Test
    void get() {
        String s = HttpUtils.get("http://www.baidu.com");
        System.out.println(s);
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