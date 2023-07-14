package com.chanjx.utils.entity.http;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chanjx
 * @since 2020/10/20
 **/
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class HttpFile extends BaseFile {

    private String key;

    public HttpFile(File file, String key) throws IOException {
        super(file);
        this.key = key;
    }

    public HttpFile(InputStream fileInputStream, String fileName, String key) throws IOException {
        super(fileInputStream, fileName);
        this.key = key;
    }

    public HttpFile(InputStream fileInputStream, String fileName, String mimeType, String key) throws IOException {
        super(fileInputStream, fileName, mimeType);
        this.key = key;
    }

    public HttpFile(byte[] fileBytes, String fileName, String key) throws IOException {
        super(fileBytes, fileName);
        this.key = key;
    }

    public HttpFile(byte[] fileBytes, String fileName, String mimeType, String key) {
        super(fileBytes, fileName, mimeType);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
