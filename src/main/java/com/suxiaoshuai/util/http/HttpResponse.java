package com.suxiaoshuai.util.http;

import java.util.Map;
import java.util.TreeMap;

/**
 * HTTP响应对象，包含状态码、响应头和响应体
 */
public class HttpResponse {
    private int statusCode;
    private Map<String, String> headers;
    private String body;
    private String url;
    private String message;

    public HttpResponse() {
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * 构造函数
     * @param statusCode HTTP状态码
     * @param headers 响应头信息
     * @param body 响应体内容
     */
    public HttpResponse(int statusCode, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        // 转换为大小写不敏感的TreeMap
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (headers != null) {
            this.headers.putAll(headers);
        }
        this.body = body;
    }

    /**
     * 构造函数
     * @param statusCode HTTP状态码
     * @param headers 响应头信息
     * @param body 响应体内容
     * @param url 请求URL
     */
    public HttpResponse(int statusCode, Map<String, String> headers, String body, String url) {
        this.statusCode = statusCode;
        // 转换为大小写不敏感的TreeMap
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (headers != null) {
            this.headers.putAll(headers);
        }
        this.body = body;
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}