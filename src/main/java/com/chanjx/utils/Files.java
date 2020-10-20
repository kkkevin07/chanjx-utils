package com.chanjx.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * @author 陈俊雄
 * @since 2020/10/20
 **/
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@AllArgsConstructor
public class Files implements Serializable {

    private String key;

    private List<File> files;
}
