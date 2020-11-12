package com.chanjx.utils;

import com.sun.istack.internal.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈俊雄
 * @since 2020/11/12
 **/
public enum HttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    private static final Map<String, HttpMethod> mappings = new HashMap<>(8);

    static {
        for (HttpMethod httpMethod : values()) {
            mappings.put(httpMethod.name(), httpMethod);
        }
    }

    public static HttpMethod resolve(@Nullable String method) {
        return (method != null ? mappings.get(method) : null);
    }

    public boolean matches(String method) {
        return (this == resolve(method));
    }
}
