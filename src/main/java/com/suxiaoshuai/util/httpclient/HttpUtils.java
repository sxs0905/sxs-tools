package com.suxiaoshuai.util.httpclient;


import com.suxiaoshuai.util.string.StringUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP 请求工具类，基于 OkHttp 实现
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static volatile OkHttpClient okHttpClient = null;
    /**
     * HTTP 请求超时时间，单位：秒
     */
    public static final int TIME_OUT = 60;

    static {
        if (okHttpClient == null) {
            synchronized (HttpUtils.class) {
                if (okHttpClient == null) {
                    TrustManager[] trustManagers = buildTrustManagers();
                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .sslSocketFactory(createSSLSocketFactory(trustManagers), (X509TrustManager) trustManagers[0])
                            .hostnameVerifier((hostName, session) -> true)
                            .retryOnConnectionFailure(true)
                            .connectionPool(new ConnectionPool(10, 10, TimeUnit.SECONDS))
                            .build();
                }
            }
        }
    }

    /**
     * 发起get请求
     *
     * @param url 请求地址
     * @return 请求结果
     */
    public static String get(String url) {
        return get(url, null, null);
    }

    /**
     * 发起get请求
     *
     * @param url       请求地址
     * @param headerMap 请求头
     * @return 请求结果
     */
    public static String get(String url, Map<String, String> headerMap) {
        return get(url, null, headerMap);
    }

    /**
     * 发起get 请求
     *
     * @param url       请求地址
     * @param paramMap  请求参数
     * @param headerMap 请求头
     * @return 请求结果
     */
    public static String get(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
        logger.info("okhttp get url:{}, paramMap:{}, headerMap:{}", url, paramMap, headerMap);
        String result = null;
        try {
            Request.Builder request = new Request.Builder().get();
            addGetHeader(request, headerMap);
            String finalUrl = getUrl(url, paramMap);
            logger.info("okhttp get url:{}, add param final url:{}", url, finalUrl);
            request.url(finalUrl);
            result = doExecute(request);
        } catch (Exception e) {
            logger.error("okHttpUtils get url:{}, error", url, e);
        }
        logger.info("okhttp get url:{},result:{}", url, result);
        return result;
    }

    /**
     * 发起post请求
     *
     * @param url  请求地址
     * @param json 请求参数
     * @return 请求结果
     */
    public static String post(String url, String json) {
        return post(url, json, null);
    }

    /**
     * 发起post请求，支持自定义请求头
     *
     * @param url       请求地址
     * @param json      请求参数
     * @param headerMap 请求头
     * @return 请求结果
     */
    public static String post(String url, String json, Map<String, String> headerMap) {
        logger.info("okhttp post json url:{}, body:{}, headerMap:{}", url, json, headerMap);
        String result = null;
        try {
            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
            Request.Builder request = new Request.Builder().post(requestBody).url(url);
            addHeader(request, headerMap);
            result = doExecute(request);
        } catch (Exception e) {
            logger.error("okHttpUtils post json url:{}, error", url, e);
        }
        logger.info("okhttp post json url:{},result:{}", url, result);
        return result;
    }

    /**
     * 发起post form请求
     *
     * @param url       请求地址
     * @param paramsMap 请求参数
     * @return 请求结果
     */
    public static String postForm(String url, Map<String, String> paramsMap) {
        return postForm(url, paramsMap, null);
    }

    /**
     * 发起post form请求，支持自定义请求头
     *
     * @param url       请求地址
     * @param paramsMap 参数
     * @param headerMap 请求头
     * @return 请求结果
     */
    public static String postForm(String url, Map<String, String> paramsMap, Map<String, String> headerMap) {
        logger.info("okhttp post form url:{}, body:{}, headerMap:{}", url, paramsMap, headerMap);
        String result = null;
        try {
            FormBody.Builder formBody = new FormBody.Builder();
            if (paramsMap != null && !paramsMap.isEmpty()) {
                paramsMap.forEach(formBody::add);
            }
            RequestBody requestBody = formBody.build();
            Request.Builder request = new Request.Builder().post(requestBody).url(url);
            addHeader(request, headerMap);
            result = doExecute(request);
        } catch (Exception e) {
            logger.error("okHttpUtils post form url:{}, error", url, e);
        }
        logger.info("okhttp post form url:{},result:{}", url, result);
        return result;
    }


    private static String doExecute(Request.Builder request) {
        String result = null;
        try (Response response = okHttpClient.newCall(request.build()).execute()) {
            if (response.body() != null) {
                result = response.body().string();
            }
        } catch (Exception e) {
            logger.error("okHttpUtils error", e);
        }
        return result;
    }

    private static String getUrl(String url, Map<String, String> paramMap) {
        if (StringUtil.isBlank(url) || paramMap == null || paramMap.isEmpty()) {
            return url;
        }
        StringBuilder urlBuilder = new StringBuilder(url);
        if (!url.contains("?")) {
            urlBuilder.append("?");
        } else {
            urlBuilder.append("&");
        }
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)).
                    append("=").
                    append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)).
                    append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        return urlBuilder.toString();
    }

    private static void addGetHeader(Request.Builder request, Map<String, String> headerMap) {
        request.addHeader("Connection", "Keep-Alive");
        addHeader(request, headerMap);
    }

    private static void addHeader(Request.Builder request, Map<String, String> headerMap) {
        if (request == null) {
            return;
        }
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");
        request.addHeader("Accept", "*/*");
        if (headerMap == null || headerMap.isEmpty()) {
            return;
        }
        headerMap.forEach(request::addHeader);
    }


    /**
     * 生成安全套接字工厂，用于 HTTPS 请求的证书跳过
     *
     * @param trustAllCerts 信任管理器数组
     * @return SSL 套接字工厂
     */
    private static SSLSocketFactory createSSLSocketFactory(TrustManager[] trustAllCerts) {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            logger.warn("create SSL Socket Factory exception", e);
        }
        return ssfFactory;
    }

    /**
     * 构建信任所有证书的信任管理器
     *
     * @return 信任管理器数组
     */
    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
    }
}
