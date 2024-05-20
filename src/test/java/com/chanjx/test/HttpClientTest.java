package com.chanjx.test;

import com.chanjx.utils.HttpClientUtils;
import com.chanjx.utils.entity.http.HttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 *
 * </p>
 *
 * @author alan997s
 * @since 2023/7/14
 **/
public class HttpClientTest {

    @Test
    void name() throws URISyntaxException, IOException {
        final HttpResponse httpResponse = HttpClientUtils.doGet("https://www.baidu.com");
        final byte[] byteBody = httpResponse.getBody();
        System.out.println(new String(byteBody, StandardCharsets.UTF_8));
    }

    @Test
    void name1() {
        final boolean equals = ContentType.TEXT_HTML.toString().equals("text/html");
        System.out.println(equals);
        System.out.println(ContentType.TEXT_HTML);
        System.out.println("text/html");
    }
}
