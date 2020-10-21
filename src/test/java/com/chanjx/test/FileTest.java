package com.chanjx.test;

import org.apache.http.entity.ContentType;
import org.junit.Test;

/**
 * @author 陈俊雄
 * @since 2020/10/21
 **/
public class FileTest {
    @Test
    public void FileNameTest() {
        final ContentType contentType = ContentType.create("image/bmp");
        System.out.println(contentType);
    }
}
