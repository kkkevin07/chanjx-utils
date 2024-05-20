package com.chanjx.utils.entity.http;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author chanjx
 * @since 2020/10/20
 **/
@Data
@Accessors(chain = true)
public class HttpFiles implements Serializable {

    private final String key;

    private final List<BaseFile> files;

    public HttpFiles(String key, List<BaseFile> files) {
        this.key = key;
        this.files = files;
    }
}
