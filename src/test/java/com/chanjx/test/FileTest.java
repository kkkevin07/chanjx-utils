package com.chanjx.test;

import com.chanjx.utils.HttpClientUtils;
import com.chanjx.utils.JsonUtils;
import com.chanjx.utils.entity.http.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * @author 陈俊雄
 * @since 2020/10/21
 **/
@Slf4j
public class FileTest {
    @Test
    public void FileNameTest() {
        final ContentType contentType = ContentType.create("image/bmp");
        System.out.println(contentType);
    }

    @Test
    public void wsaAndDomain() throws JsonProcessingException {
        final String uri = "https://open.chinanetcenter.com/api/domain";
        final String username = "wecloud";
        final String apiKey = "GxPB4FTgdrPDkLeq";

        final HashMap<String, String> params = new HashMap<>();
        // 设置版本 1.0.0
        params.put("version", "1.0.0");
        // 加速域名的服务区域 apac （亚太）
        params.put("service-areas", "apac");
        // 设置备注信息
        params.put("comment", "创建加速域名：www.chanjx.com");
        // 设置加速域名的服务类型
        params.put("service-type", "web-https");
        // 设置回源地址
        params.put("origin-ips", "104.243.16.13");
        // 设置标识域名是否是纯海外加速 （是，未备案）
        params.put("accelerate-no-china", "true");
        // 需要接入CDN的域名
        params.put("domain-name", "www.chanjx.com");
        // 获取 GMT 时间
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        // 获取 RFC_1123 协议格式时间
        final String format = DateTimeFormatter.RFC_1123_DATE_TIME.format(now);
        // 对 format 进行 hmac sha1 加密
        final byte[] hmacHex = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, apiKey).hmac(format);
        // 对 hmacHex 进行 base64 编码
        final String password = Base64.encodeBase64String(hmacHex);
        // 获取 Basic auth 参数
        final String auth = Base64.encodeBase64String((username + ":" + password).getBytes());
        // 设置请求头
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, "Basic " + auth);
        headers.put(HttpHeaders.ACCEPT, "application/json");
        headers.put(HttpHeaders.DATE, format);
        final HttpResponse result = HttpClientUtils.doPostJson(uri, JsonUtils.obj2Json(params), headers);
        log.info("返回消息体：" + result.getBody());

    }
}
