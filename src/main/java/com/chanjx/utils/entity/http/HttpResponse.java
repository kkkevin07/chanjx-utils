package com.chanjx.utils.entity.http;

import com.chanjx.utils.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chanjx
 * @since 2020/11/9
 **/
@Getter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class HttpResponse implements Serializable {

    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6838#section-4.2.1">若未指定字符集应使用UTF-8</a>
     */
    private final static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * Http status
     */
    private final Integer status;

    /**
     * Response headers
     */
    private final List<Header> headers;

    /**
     * Response body
     */
    private final byte[] body;

    /**
     * Body content type
     */
    private final ContentType contentType;

    /**
     * Body mime type
     */
    private final String mimeType;

    /**
     * Body charset
     */
    private final Charset charset;

    /**
     * Body to string
     */
    private String strBody;

    public HttpResponse(Integer status, Header[] headers, byte[] body, ContentType contentType) {
        this.status = status;
        this.headers = Arrays.asList(headers);
        this.body = body;
        this.contentType = contentType;
        this.mimeType =
                this.contentType != null
                        ? this.contentType.getMimeType()
                        : null;
        this.charset =
                this.contentType != null && this.contentType.getCharset() != null
                        ? this.contentType.getCharset()
                        : DEFAULT_CHARSET;
    }

    public List<Header> getHeaders(String name) {
        return this.headers.stream()
                .filter(header -> header.getName().equals(name))
                .collect(Collectors.toList());
    }

    public String getStrBody() {
        if (StringUtils.isBlank(this.strBody)) {
            if (this.getCharset() != null) {
                this.strBody = new String(this.body, this.getCharset());
            } else {
                this.strBody = new String(this.body, DEFAULT_CHARSET);
            }
        }
        return this.strBody;
    }
}
