package com.chanjx.utils;

import com.chanjx.utils.entity.http.BaseFile;
import com.chanjx.utils.entity.http.HttpFile;
import com.chanjx.utils.entity.http.HttpFiles;
import com.chanjx.utils.entity.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.apache.hc.core5.http.ContentType.APPLICATION_XML;

/**
 * @author chanjx
 * @since 2020/5/14
 **/
@Slf4j
public class HttpClientUtils {

    private HttpClientUtils() {
    }

    private static final Timeout REQUEST_TIMEOUT = Timeout.ofMilliseconds(30L);
    private static final Timeout RESPONSE_TIMEOUT = Timeout.ofMilliseconds(15L);

    private static final CloseableHttpClient client;

    static {
        // 连接池配置，路由及路由线路配置
        final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(256);
        connManager.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(connManager).build();
    }

    public static HttpResponse doGet(String uri) throws URISyntaxException, IOException {
        return doGet(uri, null, null);
    }

    public static HttpResponse doGet(String uri, Map<String, String> query) throws URISyntaxException, IOException {
        return doGet(uri, query, null);
    }

    public static HttpResponse doGet(String uri, Map<String, String> query, Map<String, String> headers) throws URISyntaxException, IOException {
        final HttpGet httpGet = new HttpGet(setQuery(uri, query));
        return send(httpGet, headers);
    }

    /**
     * 执行Post请求
     *
     * @param uri    uri
     * @param params 请求参数
     * @return 请求结果
     */
    public static HttpResponse doPostForm(String uri, Map<String, String> params) throws IOException {
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
    public static HttpResponse doPostForm(String uri, Map<String, String> params, Map<String, String> headers) throws IOException {
        final HttpPost httpPost = new HttpPost(uri);
        return sendForm(params, headers, httpPost);
    }

    public static HttpResponse doPostJson(String uri, String jsonStr) throws IOException {
        return doPostJson(uri, jsonStr, null);
    }

    public static HttpResponse doPostJson(String uri, String jsonStr, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(uri);
        setJsonBody(jsonStr, httpPost);
        return send(httpPost, headers);
    }

    public static HttpResponse doPostXml(String uri, String xmlStr) throws IOException {
        return doPostXml(uri, xmlStr, null);
    }

    public static HttpResponse doPostXml(String uri, String xmlStr, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(uri);
        setXmlBody(xmlStr, httpPost);
        return send(httpPost, headers);
    }

    public static HttpResponse doPostRow(String uri, String rowStr, ContentType contentType) throws IOException {
        return doPostRow(uri, rowStr, null, contentType);
    }

    public static HttpResponse doPostRow(String uri, String rowStr, Map<String, String> headers, ContentType contentType) throws IOException {
        HttpPost httpPost = new HttpPost(uri);
        setRowBody(rowStr, httpPost, contentType);
        return send(httpPost, headers);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFile httpFile, Map<String, String> params) throws IOException {
        return doPostMultipartForm(uri, httpFile, params, null);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFile httpFile, Map<String, String> params, Map<String, String> headers) throws IOException {
        final ContentType contentType = ContentType.create(httpFile.getMimeType());
        final MultipartEntityBuilder builder =
                MultipartEntityBuilder
                        .create()
                        .addBinaryBody(
                                httpFile.getKey(),
                                httpFile.getFileBytes(),
                                contentType,
                                httpFile.getFileName());
        final HttpPost httpPost = setParams(uri, params, builder);
        return send(httpPost, headers);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFiles httpFiles, Map<String, String> params) throws IOException {
        return doPostMultipartForm(uri, httpFiles, params, null);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFiles httpFiles, Map<String, String> params, Map<String, String> headers) throws IOException {

        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (BaseFile baseFile : httpFiles.getFiles()) {
            final ContentType contentType = ContentType.create(baseFile.getMimeType());
            builder.addBinaryBody(httpFiles.getKey(), baseFile.getFileBytes(), contentType, baseFile.getFileName());
        }

        final HttpPost httpPost = setParams(uri, params, builder);
        return send(httpPost, headers);
    }

    public static HttpResponse doPostMultipartForm(String uri, List<HttpFile> httpFiles, Map<String, String> params) throws IOException {
        return doPostMultipartForm(uri, httpFiles, params, null);
    }

    public static HttpResponse doPostMultipartForm(String uri, List<HttpFile> httpFiles, Map<String, String> params, Map<String, String> headers) throws IOException {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        httpFiles.forEach(httpFile -> addBinaryBody(builder, httpFile));
        final HttpPost httpPost = setParams(uri, params, builder);
        return send(httpPost, headers);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFiles httpFiles, List<HttpFile> httpFileList, Map<String, String> params) throws IOException {
        return doPostMultipartForm(uri, httpFiles, httpFileList, params, null);
    }

    public static HttpResponse doPostMultipartForm(String uri, HttpFiles httpFiles, List<HttpFile> httpFileList, Map<String, String> params, Map<String, String> headers) throws IOException {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        httpFiles.getFiles().forEach(baseFile -> {
            final ContentType contentType = ContentType.create(baseFile.getMimeType());
            builder.addBinaryBody(
                    httpFiles.getKey(),
                    baseFile.getFileBytes(),
                    contentType,
                    baseFile.getFileName());
        });
        httpFileList.forEach(httpFile -> addBinaryBody(builder, httpFile));
        final HttpPost httpPost = setParams(uri, params, builder);
        return send(httpPost, headers);
    }

    public static HttpResponse doPutForm(String uri, Map<String, String> params) throws IOException {
        return doPutForm(uri, params, null);
    }

    public static HttpResponse doPutForm(String uri, Map<String, String> params, Map<String, String> headers) throws IOException {
        params.values().removeIf(Objects::isNull);
        final HttpPut httpPut = new HttpPut(uri);
        return sendForm(params, headers, httpPut);
    }

    private static HttpResponse sendForm(Map<String, String> params, Map<String, String> headers, HttpUriRequestBase method) throws IOException {
        final ContentType contentType;
        if (MapUtils.isEmpty(headers)) {
            contentType = ContentType.APPLICATION_FORM_URLENCODED;
            headers = new HashMap<String, String>() {{
                put(HttpHeaders.CONTENT_TYPE, contentType.toString());
            }};
        } else {
            final String mimeType = headers.get(HttpHeaders.CONTENT_TYPE);
            contentType =
                    StringUtils.isNotBlank(mimeType)
                            ? ContentType.parse(mimeType)
                            : ContentType.APPLICATION_FORM_URLENCODED;
            headers.put(HttpHeaders.CONTENT_TYPE, contentType.toString());
        }
        setParams(params, method, contentType.getCharset());
        return send(method, headers);
    }

    public static HttpResponse doPutJson(String uri, String jsonStr) throws IOException {
        return doPutJson(uri, jsonStr, null);
    }

    public static HttpResponse doPutJson(String uri, String jsonStr, Map<String, String> headers) throws IOException {
        final HttpPut httpPut = new HttpPut(uri);
        setJsonBody(jsonStr, httpPut);
        return send(httpPut, headers);
    }

    public static HttpResponse doPutXml(String uri, String xmlStr) throws IOException {
        return doPutXml(uri, xmlStr, null);
    }

    public static HttpResponse doPutXml(String uri, String xmlStr, Map<String, String> headers) throws IOException {
        final HttpPut httpPut = new HttpPut(uri);
        setXmlBody(xmlStr, httpPut);
        return send(httpPut, headers);
    }

    public static HttpResponse doDelete(String uri) throws URISyntaxException, IOException {
        return doDelete(uri, null, null);
    }

    public static HttpResponse doDelete(String uri, Map<String, String> query) throws URISyntaxException, IOException {
        return doDelete(uri, query, null);
    }

    public static HttpResponse doDelete(String uri, Map<String, String> query, Map<String, String> headers) throws URISyntaxException, IOException {
        final HttpDelete httpDelete = new HttpDelete(setQuery(uri, query));
        return send(httpDelete, headers);
    }

    public static HttpPost setParams(String uri, Map<String, String> params, MultipartEntityBuilder builder) {
        params.values().removeIf(Objects::isNull);
        params.forEach(builder::addTextBody);
        final HttpPost httpPost = new HttpPost(uri);
        final HttpEntity httpEntity = builder.build();
        httpPost.setEntity(httpEntity);
        return httpPost;
    }

    /**
     * 设置请求参数
     *
     * @param params  请求参数
     * @param method  Http请求
     * @param charset 字符集
     */
    public static void setParams(Map<String, String> params, HttpUriRequestBase method, Charset charset) {
        // 设置请求参数
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        params.values().removeIf(Objects::isNull);
        params.forEach((k, v) -> nameValuePairList.add(new BasicNameValuePair(k, v)));
        method.setEntity(
                new UrlEncodedFormEntity(nameValuePairList,
                        charset == null ? StandardCharsets.UTF_8 : charset));
    }

    private static URI setQuery(String uri, Map<String, String> query) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(new URI(uri));
        uriBuilder.setCharset(StandardCharsets.UTF_8);
        if (MapUtils.isNotEmpty(query)) {
            query.values().removeIf(Objects::isNull);
            query.forEach(uriBuilder::addParameter);
        }
        return uriBuilder.build();
    }

    private static void setJsonBody(String jsonStr, HttpUriRequestBase method) {
        setRowBody(jsonStr, method, APPLICATION_JSON);
    }

    private static void setXmlBody(String xmlStr, HttpUriRequestBase method) {
        setRowBody(xmlStr, method, APPLICATION_XML);
    }

    private static void setRowBody(String rowStr, HttpUriRequestBase method, ContentType contentType) {
        final StringEntity stringEntity = new StringEntity(rowStr, contentType);
        method.setEntity(stringEntity);
    }

    /**
     * 发送请求
     *
     * @param request request
     * @param headers 请求头
     * @return 请求字符串结果
     */
    private static HttpResponse send(HttpUriRequestBase request, Map<String, String> headers) throws IOException {
        setHeaders(request, headers);
        setConfig(request);
        return client.execute(request, response -> {
            // 获取请求返回消息
            final HttpEntity entity = response.getEntity();
            final Header header = response.getHeader(HttpHeaders.CONTENT_TYPE);
            return new HttpResponse(
                    response.getCode(),
                    response.getHeaders(),
                    EntityUtils.toByteArray(entity),
                    header == null ? null : ContentType.parse(header.getValue()));
        });
    }

    private static void setHeaders(HttpUriRequestBase request, Map<String, String> headers) {
        // 设置请求头
        if (headers != null) {
            headers.forEach(request::setHeader);
        }
    }

    private static void setConfig(HttpUriRequestBase request) {
        // 设置超时时间
        RequestConfig requestConfig =
                RequestConfig
                        .custom()
                        .setConnectionKeepAlive(TimeValue.ofSeconds(5L))
                        .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                        .setResponseTimeout(RESPONSE_TIMEOUT)
                        .build();
        request.setConfig(requestConfig);
    }

    private static void addBinaryBody(MultipartEntityBuilder builder, HttpFile httpFile) {
        final ContentType contentType = ContentType.create(httpFile.getMimeType());
        builder.addBinaryBody(
                httpFile.getKey(),
                httpFile.getFileBytes(),
                contentType,
                httpFile.getFileName());
    }
}
