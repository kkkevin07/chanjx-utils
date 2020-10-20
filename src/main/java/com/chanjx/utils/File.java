package com.chanjx.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 陈俊雄
 * @since 2020/10/20
 **/
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@AllArgsConstructor
public class File implements Serializable {

    private String key;

    private java.io.File file;
}
