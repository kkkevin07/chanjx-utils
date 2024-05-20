package com.chanjx.utils.entity.http;

import com.chanjx.utils.FileUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author chanjx
 * @since 2020/10/21
 **/
@Data
@Accessors(chain = true)
public class BaseFile implements Serializable {

    private final byte[] fileBytes;

    private final String fileName;

    private final String mimeType;

    public BaseFile(File file) throws IOException {
        this.fileBytes = FileUtils.readFileToByteArray(file);
        this.fileName = file.getName();
        this.mimeType = FileUtils.getMimeType(file);
    }

    public BaseFile(InputStream fileInputStream, String fileName) throws IOException {
        this.fileBytes = IOUtils.toByteArray(fileInputStream);
        this.fileName = fileName;
        this.mimeType = FileUtils.getMimeType(this.fileBytes);
    }

    public BaseFile(InputStream fileInputStream, String fileName, String mimeType) throws IOException {
        this.fileBytes = IOUtils.toByteArray(fileInputStream);
        this.fileName = fileName;
        this.mimeType = mimeType;
    }

    public BaseFile(byte[] fileBytes, String fileName) throws IOException {
        this.fileBytes = fileBytes;
        this.fileName = fileName;
        this.mimeType = FileUtils.getMimeType(fileBytes);
    }

    public BaseFile(byte[] fileBytes, String fileName, String mimeType) {
        this.fileBytes = fileBytes;
        this.fileName = fileName;
        this.mimeType = mimeType;
    }
}
