package com.chanjx.test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.entity.ContentType;
import org.junit.Test;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 陈俊雄
 * @since 2020/10/21
 **/
@Slf4j
public class FileTest {
    @Test
    public void FileNameTest() {
        final ContentType contentType = ContentType.create("image/bmp");
        System.out.println(contentType);
    }

    @Test
    public void test() {
        final String s = RandomStringUtils.randomAlphanumeric(16);
        System.out.println(s);
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public static class Hello implements Serializable {

        private String name;

        private Boolean enable;

        private LocalDateTime createTime;

    }
}
