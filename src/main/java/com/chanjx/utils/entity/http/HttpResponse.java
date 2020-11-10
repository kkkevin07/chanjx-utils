package com.chanjx.utils.entity.http;

import com.chanjx.utils.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.http.Header;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * @author 陈俊雄
 * @since 2020/11/9
 **/
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HttpResponse implements Serializable {

    /**
     * Http status
     */
    private final Integer status;

    /**
     * Response headers
     */
    private final Header[] headers;

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
    private String mimeType;

    /**
     * Body charset
     */
    private Charset charset;

    /**
     * Body to string
     */
    private String strBody;

    public HttpResponse(Integer status, Header[] headers, byte[] body, ContentType contentType) {
        this.status = status;
        this.headers = headers;
        this.body = body;
        this.contentType = contentType;
    }

    public Integer getStatus() {
        return status;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public byte[] getByteBody() {
        return body;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Charset getCharset() {
        if (charset == null && contentType != null) {
            charset = contentType.getCharset();
            if (charset == null) {
                final ContentType defaultContentType = ContentType.getByMimeType(contentType.getMimeType());
                charset = defaultContentType != null ? defaultContentType.getCharset() : null;
            }
        } else {
            charset = HTTP.DEF_CONTENT_CHARSET;
        }
        return charset;
    }

    public String getMimeType() {
        if (StringUtils.isBlank(mimeType) && contentType != null) {
            mimeType = contentType.getMimeType();
        }
        return mimeType;
    }

    public String getBody() {
        if (StringUtils.isBlank(strBody)) {
            if (charset == null) {
                getCharset();
            }
            strBody = new String(body, charset);
        }

        return strBody;
    }
}
