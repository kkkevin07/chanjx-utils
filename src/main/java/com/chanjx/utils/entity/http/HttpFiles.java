package com.chanjx.utils.entity.http;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 陈俊雄
 * @since 2020/10/20
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HttpFiles implements Serializable {

    private String key;

    private List<BaseFile> files;

    public HttpFiles(String key, List<BaseFile> files) {
        this.key = key;
        this.files = files;
    }
}
