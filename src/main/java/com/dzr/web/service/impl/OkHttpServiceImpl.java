package com.dzr.web.service.impl;

import com.dzr.web.service.OkHttpService;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author sggb
 * @since 2017/8/22
 */
@Service
public class OkHttpServiceImpl implements OkHttpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpServiceImpl.class);

    private static final String ERROR_RESULT = "{\"status\":1,\"msg\":\"出错了\"}";

    @Override
    public String post(String url, Map<String, String> paramMap, String contentType) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        StringBuilder paramStr = new StringBuilder();
        for (Map.Entry<String, String> param : paramMap.entrySet()) {
            builder.add(param.getKey(), param.getValue());
            paramStr.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }
        LOGGER.info(url + "?" + paramStr.toString());
        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", contentType)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String result=response.body().string();
            LOGGER.info(result);
            return result;
        } catch (IOException e) {
            LOGGER.error("获取数据出错:" + e.getMessage(), e);
        }

        return ERROR_RESULT;
    }

    @Override
    public String post(String url, String contentType) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();

        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", contentType)
                .build();
        LOGGER.info(url);
        try {
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            LOGGER.error("获取数据出错:" + e.getMessage(), e);
        }

        return ERROR_RESULT;
    }

    @Override
    public <T> String post(String url, T t) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            @Override
            public boolean apply(Object o, String s, Object value) {
                if (value == null)
                    return true;
                return false;
            }
        });
        String json = JSONObject.fromObject(t, jsonConfig).toString();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        LOGGER.info(url + "?" + json);
        try {
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            LOGGER.error("获取数据出错:" + e.getMessage(), e);
        }

        return ERROR_RESULT;
    }

    /**
     * POST请求
     *
     * @param url         地址
     * @param paramMap    参数
     * @param contentType
     * @return
     */
    @Override
    public Response postForResponse(String url, Map<String, String> paramMap, String contentType) throws IOException {

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry param : paramMap.entrySet()) {
            builder.add((String) param.getKey(), (String) param.getValue());
        }

        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", contentType)
                .build();
        return client.newCall(request).execute();
    }
}
