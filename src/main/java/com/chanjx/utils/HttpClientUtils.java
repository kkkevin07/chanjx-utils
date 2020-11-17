package com.chanjx.utils;

import com.chanjx.utils.entity.http.BaseFile;
import com.chanjx.utils.entity.http.HttpFile;
import com.chanjx.utils.entity.http.HttpFiles;
import com.chanjx.utils.entity.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.http.Consts.UTF_8;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * @author 陈俊雄
 * @since 2020/5/14
 **/
@Slf4j
public abstract class HttpClientUtils {

    private static final int TIMEOUT = 60;

    /**
     * 获取HttpClient
     *
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }

    public static CloseableHttpClient getHttpClient(CredentialsProvider provide) {
        return HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provide)
                .build();
    }

    /**
     * 执行Post请求
     *
     * @param uri    uri
     * @param params 请求参数
     * @return 请求结果
     */
    public static HttpResponse doPostForm(String uri, Map<String, String> params) {
        return doPostForm(uri, params, null);
    }

    /**
     * 执行Post请求
     *
     * @param uri     uri
     * @param params  请求参数
     * @param headers 请求头信息
     * @return 请求结果
     */
    public static HttpResponse doPostForm(String uri, Map<String, String> params, Map<String, String> headers) {
        final HttpPost httpPost = new HttpPost(uri);
        setParams(params, httpPost);
        return send(getHttpClient(), httpPost, headers);
    }

    public static HttpResponse doPostJson(String uri, String jsonString) {
        return doPostJson(uri, jsonString, null);
    }

    public static HttpResponse doPostJson(String uri, String jsonString, Map<String, String> headers) {
        HttpPost httpPost = new HttpPost(uri);
        setJsonBody(jsonString, httpPost);
        return send(getHttpClient(), httpPost, headers);
    }

    public static HttpResponse doGet(String uri) throws URISyntaxException {
        return doGet(uri, null, null);
    }

    public static HttpResponse doGet(String uri, Map<String, String> query) throws URISyntaxException {
        return doGet(uri, query, null);
    }

    public static HttpResponse doGet(String uri, Map<String, String> query, Map<String, String> headers) throws URISyntaxException {
        final HttpGet httpGet = new HttpGet(setQuery(uri, query));
        return send(getHttpClient(), httpGet, headers);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFile httpFile, Map<String, String> params) throws IOException {
        return doPostMultipartForm(uri, httpFile, params, null);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFile httpFile, Map<String, String> params, Map<String, String> headers) throws IOException {
        final ContentType contentType = ContentType.create(httpFile.getMimeType());
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody(httpFile.getKey(), httpFile.getFileBytes(), contentType, httpFile.getFileName());
        final HttpPost httpPost = setParams(uri, params, builder);
        return send(getHttpClient(), httpPost, headers);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFiles httpFiles, Map<String, String> params) throws IOException {
        return doPostMultipartForm(uri, httpFiles, params, null);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFiles httpFiles, Map<String, String> params, Map<String, String> headers) throws IOException {

        final MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (BaseFile baseFile : httpFiles.getFiles()) {
            final ContentType contentType = ContentType.create(baseFile.getMimeType());
            builder.addBinaryBody(httpFiles.getKey(), baseFile.getFileBytes(), contentType, baseFile.getFileName());
        }

        final HttpPost httpPost = setParams(uri, params, builder);
        return send(getHttpClient(), httpPost, headers);
    }

    public static HttpResponse doPostMultipartForm(String uri, List<HttpFile> httpFiles, Map<String, String> params) throws IOException {
        return doPostMultipartForm(uri, httpFiles, params, null);
    }

    public static HttpResponse doPostMultipartForm(String uri, List<HttpFile> httpFiles, Map<String, String> params, Map<String, String> headers) throws IOException {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (HttpFile httpFile : httpFiles) {
            final ContentType contentType = ContentType.create(httpFile.getMimeType());
            builder.addBinaryBody(httpFile.getKey(), httpFile.getFileBytes(), contentType, httpFile.getFileName());
        }

        final HttpPost httpPost = setParams(uri, params, builder);
        return send(getHttpClient(), httpPost, headers);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFiles httpFiles, List<HttpFile> httpFileList, Map<String, String> params) throws IOException {
        return doPostMultipartForm(uri, httpFiles, httpFileList, params, null);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFiles httpFiles, List<HttpFile> httpFileList, Map<String, String> params, Map<String, String> headers) throws IOException {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (BaseFile baseFile : httpFiles.getFiles()) {
            final ContentType contentType = ContentType.create(baseFile.getMimeType());
            builder.addBinaryBody(httpFiles.getKey(), baseFile.getFileBytes(), contentType, baseFile.getFileName());
        }
        for (HttpFile httpFile : httpFileList) {
            final ContentType contentType = ContentType.create(httpFile.getMimeType());
            builder.addBinaryBody(httpFile.getKey(), httpFile.getFileBytes(), contentType, httpFile.getFileName());
        }
        final HttpPost httpPost = setParams(uri, params, builder);
        return send(getHttpClient(), httpPost, headers);
    }

    public static HttpResponse doPut(String uri, Map<String, String> params) {
        return doPut(uri, params, null);
    }

    public static HttpResponse doPut(String uri, Map<String, String> params, Map<String, String> headers) {
        final HttpPut httpPut = new HttpPut(uri);
        setParams(params, httpPut);
        return send(getHttpClient(), httpPut, headers);
    }

    public static HttpResponse doPutJson(String uri, String jsonString) {
        return doPutJson(uri, jsonString, null);
    }

    public static HttpResponse doPutJson(String uri, String jsonString, Map<String, String> headers) {
        final HttpPut httpPut = new HttpPut(uri);
        setJsonBody(jsonString, httpPut);
        return send(getHttpClient(), httpPut, headers);
    }

    public static HttpResponse doDelete(String uri) throws URISyntaxException {
        return doDelete(uri, null, null);
    }

    public static HttpResponse doDelete(String uri, Map<String, String> query) throws URISyntaxException {
        return doDelete(uri, query, null);
    }

    public static HttpResponse doDelete(String uri, Map<String, String> query, Map<String, String> headers) throws URISyntaxException {
        final HttpDelete httpDelete = new HttpDelete(setQuery(uri, query));
        return send(getHttpClient(), httpDelete, headers);
    }

    private static HttpPost setParams(String uri, Map<String, String> params, MultipartEntityBuilder builder) {
        params.forEach(builder::addTextBody);
        final HttpPost httpPost = new HttpPost(uri);
        final HttpEntity httpEntity = builder.build();
        httpPost.setEntity(httpEntity);
        return httpPost;
    }

    /**
     * 设置请求参数
     *
     * @param params 请求参数
     * @param method Http请求
     */
    private static void setParams(Map<String, String> params, HttpEntityEnclosingRequestBase method) {
        // 设置请求参数
        if (params != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            params.forEach((k, v) -> nameValuePairList.add(new BasicNameValuePair(k, v)));
            method.setEntity(new UrlEncodedFormEntity(nameValuePairList, UTF_8));
        }
    }

    private static URI setQuery(String uri, Map<String, String> query) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(new URI(uri));
        uriBuilder.setCharset(StandardCharsets.UTF_8);
        if (query != null && query.size() > 0) {
            query.forEach(uriBuilder::addParameter);
        }
        return uriBuilder.build();
    }

    private static void setJsonBody(String jsonString, HttpEntityEnclosingRequestBase method) {
        final StringEntity stringEntity = new StringEntity(jsonString, UTF_8);
        stringEntity.setContentType(APPLICATION_JSON.getMimeType());
        stringEntity.setContentEncoding(UTF_8.name());
        method.setEntity(stringEntity);
    }

    /**
     * 发送请求
     *
     * @param httpClient httpClient
     * @param request    request
     * @param headers    请求头
     * @return 请求字符串结果
     */
    public static HttpResponse send(CloseableHttpClient httpClient, HttpRequestBase request, Map<String, String> headers) {
        setHeaders(request, headers);
        setConfig(request);

        // 获取请求返回消息
        HttpResponse result = null;
        try {
            // 执行请求
            CloseableHttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() > HttpStatus.SC_MULTI_STATUS || response.getStatusLine().getStatusCode() < HttpStatus.SC_OK) {
                log.warn("Response status code:" + response.getStatusLine().getStatusCode());
            }
            // 获取请求返回消息
            final HttpEntity entity = response.getEntity();
            final ContentType contentType = ContentType.get(entity);
            result = new HttpResponse(
                    response.getStatusLine().getStatusCode(),
                    response.getAllHeaders(),
                    EntityUtils.toByteArray(entity),
                    contentType);
            response.close();
            httpClient.close();
        } catch (IOException e) {
            log.error("Http client error:" + e.getMessage());
        }
        // 返回请求参数
        return result;
    }

    private static void setHeaders(HttpRequestBase request, Map<String, String> headers) {
        // 设置请求头
        if (headers != null) {
            headers.forEach(request::setHeader);
        }
    }

    private static void setConfig(HttpRequestBase request) {
        // 设置超时时间
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT * 1000)
                .setConnectionRequestTimeout(TIMEOUT * 1000)
                .setSocketTimeout(TIMEOUT * 1000).build();
        request.setConfig(config);
    }
}
