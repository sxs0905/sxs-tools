package com.suxiaoshuai.util.http;

import com.suxiaoshuai.util.string.StringUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    public static final int REQUEST_TIME_OUT_CODE = -1;
    public static final int REQUEST_EXCEPTION_CODE = -9;

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
     */
    public static HttpResponse get(String url) {
        return get(url, null, null);
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static HttpResponse get(String url, String param) {
        String urlNameString = StringUtil.isNotBlank(param) ? url + "?" + param : url;
        return get(urlNameString);
    }

    /**
     * 发起get请求
     */
    public static HttpResponse get(String url, Map<String, String> headerMap) {
        return get(url, null, headerMap);
    }

    /**
     * 发起get 请求
     */
    public static HttpResponse get(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
        logger.info("okhttp get url:{}, paramMap:{}, headerMap:{}", url, paramMap, headerMap);
        HttpResponse result = new HttpResponse();
        try {
            Request.Builder request = new Request.Builder().get();
            addGetHeader(request, headerMap);
            String finalUrl = getUrl(url, paramMap);
            logger.info("okhttp get url:{}, add param final url:{}", url, finalUrl);
            request.url(finalUrl);
            result = doExecute(request, finalUrl);
        } catch (Exception e) {
            logger.error("okHttpUtils get url:{}, error", url, e);
            result.setStatusCode(REQUEST_EXCEPTION_CODE);
        }
        logger.info("okhttp get url:{},result:{}", url, result);
        return result;
    }

    /**
     * 发起post请求
     */
    public static HttpResponse post(String url, String json) {
        return post(url, json, null);
    }

    /**
     * 发起post请求，支持自定义请求头
     */
    public static HttpResponse post(String url, String json, Map<String, String> headerMap) {
        logger.info("okhttp post json url:{}, body:{}, headerMap:{}", url, json, headerMap);
        HttpResponse result = new HttpResponse();
        try {
            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
            Request.Builder request = new Request.Builder().post(requestBody).url(url);
            addHeader(request, headerMap);
            result = doExecute(request, url);
        } catch (Exception e) {
            logger.error("okHttpUtils post json url:{}, error", url, e);
            result.setStatusCode(REQUEST_EXCEPTION_CODE);
        }
        logger.info("okhttp post json url:{},result:{}", url, result);
        return result;
    }

    /**
     * 发起post form请求
     */
    public static HttpResponse postForm(String url, Map<String, String> paramsMap) {
        return postForm(url, paramsMap, null);
    }

    /**
     * 发起post form请求，支持自定义请求头
     */
    public static HttpResponse postForm(String url, Map<String, String> paramsMap, Map<String, String> headerMap) {
        logger.info("okhttp post form url:{}, body:{}, headerMap:{}", url, paramsMap, headerMap);
        HttpResponse result = new HttpResponse();
        try {
            FormBody.Builder formBody = new FormBody.Builder();
            if (paramsMap != null && !paramsMap.isEmpty()) {
                paramsMap.forEach(formBody::add);
            }
            RequestBody requestBody = formBody.build();
            Request.Builder request = new Request.Builder().post(requestBody).url(url);
            addHeader(request, headerMap);
            result = doExecute(request, url);
        } catch (Exception e) {
            logger.error("okHttpUtils post form url:{}, error", url, e);
            result.setStatusCode(REQUEST_EXCEPTION_CODE);
            result.setMessage(e.getMessage());
        }
        logger.info("okhttp post form url:{},result:{}", url, result);
        return result;
    }


    private static HttpResponse doExecute(Request.Builder request, String url) {
        try (Response response = okHttpClient.newCall(request.build()).execute()) {
            int statusCode = response.code();
            Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            for (Map.Entry<String, List<String>> entry : response.headers().toMultimap().entrySet()) {
                headers.put(entry.getKey(), entry.getValue().get(0));
            }
            String body = null;
            if (response.body() != null) {
                body = response.body().string();
            }
            return new HttpResponse(statusCode, headers, body, url);
        } catch (SocketTimeoutException e) {
            logger.error("okHttpUtils socket time out ", e);
            Map<String, String> errorHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            return new HttpResponse(REQUEST_TIME_OUT_CODE, errorHeaders, null, url);
        } catch (Exception e) {
            logger.error("okHttpUtils error", e);
            Map<String, String> errorHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            return new HttpResponse(REQUEST_EXCEPTION_CODE, errorHeaders, null, url);
        }
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
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            String key = entry.getKey();
            if (StringUtil.isBlank(key)) {
                continue;
            }
            request.addHeader(key, entry.getValue());
        }
    }


    /**
     * 生成安全套接字工厂，用于 HTTPS 请求的证书跳过
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
