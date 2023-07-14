package com.chanjx.utils;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author chanjx
 * @since 2020/10/21
 **/
public abstract class FileUtils extends org.apache.commons.io.FileUtils {
    private static final Tika TIKA = new Tika();

    public static String getMimeType(File file) throws IOException {
        return TIKA.detect(file);
    }

    public static String getMimeType(URL url) throws IOException {
        return TIKA.detect(url);
    }

    public static String getMimeType(InputStream inputStream) throws IOException {
        return TIKA.detect(inputStream);
    }

    public static String getMimeType(byte[] bytes) throws IOException {
        return TIKA.detect(bytes);
    }

    public static String getMimeType(String fileName) throws IOException {
        return TIKA.detect(fileName);
    }
}
