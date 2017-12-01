package com.dzr.web.service;

import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * @author sggb
 * @since 2017/8/22
 */
public interface OkHttpService {

    /**
     * POST请求
     *
     * @param url         地址
     * @param paramMap    参数
     * @param contentType
     * @return
     */
    String post(String url, Map<String, String> paramMap, String contentType);

    String post(String url, String contentType);

    <T> String post(String url, T t);

    Response postForResponse(String url, Map<String, String> paramMap, String contentType) throws IOException;
}
