package com.chanjx.utils.entity.http;

import com.chanjx.utils.FileUtils;
import lombok.experimental.Accessors;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author 陈俊雄
 * @since 2020/10/21
 **/
@Accessors(chain = true)
public class BaseFile implements Serializable {

    protected File file;

    protected InputStream fileInputStream;

    protected byte[] fileBytes;

    protected String fileName;

    protected String mimeType;

    public BaseFile(File file) throws IOException {
        this.file = file;
        this.fileName = file.getName();
        this.mimeType = FileUtils.getMimeType(file);
    }

    public BaseFile(InputStream fileInputStream, String fileName) throws IOException {
        this.fileInputStream = fileInputStream;
        this.fileName = fileName;
        this.mimeType = FileUtils.getMimeType(fileInputStream);
    }

    public BaseFile(InputStream fileInputStream, String fileName, String mimeType) {
        this.fileInputStream = fileInputStream;
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

    public byte[] getFileBytes() throws IOException {
        if (this.fileBytes == null && this.file != null && this.file.exists()) {
            this.fileBytes = FileUtils.readFileToByteArray(this.file);
            return this.fileBytes;
        } else if (this.fileInputStream != null) {
            this.fileBytes = IOUtils.toByteArray(fileInputStream);
            return this.fileBytes;
        }
        return this.fileBytes;
    }

    public String getMimeType() {
        return mimeType;
    }


    public String getFileName() {
        return fileName;
    }

}
