package com.chanjx.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chanjx
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

    public static HttpMethod resolve(String method) {
        return (method != null ? mappings.get(method) : null);
    }

    public boolean matches(String method) {
        return (this == resolve(method));
    }
}
