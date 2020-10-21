package com.chanjx.utils;

import java.util.Iterator;

/**
 * 字符串操作工具
 *
 * @author 陈俊雄
 **/
public abstract class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * <p>Joins the elements of the provided varargs into a
     * single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * {@code null} elements and separator are treated as empty Strings ("").</p>
     *
     * <pre>
     * StringUtils.joinWithComma({"a", "b"})        = "a,b"
     * StringUtils.joinWithComma({"a", "b",""})     = "a,b,"
     * StringUtils.joinWithComma({"a", null, "b"})  = "a,,b"
     * </pre>
     *
     * @param objects the varargs providing the values to join together. {@code null} elements are treated as ""
     * @return the joined String.
     * @throws IllegalArgumentException if a null varargs is provided
     * @since 3.5
     */
    public static String joinWithComma(final Object... objects) {
        return joinWith(",", objects);
    }

    public static String joinWithComma(Iterable<?> iterable) {
        return join(iterable, ",");
    }

    public static String joinWithComma(Iterator<?> iterator) {
        return join(iterator, ",");
    }

}
