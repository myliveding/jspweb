package com.dzr.web.po.message;

import lombok.Data;

import java.util.Map;

/**
 * 微信消息模板主题
 */
@Data
public class Template {

    /**
     * 模板id
     */
    private String template_id;

    /**
     * 发送对象openid
     */
    private String touser;

    /**
     * 模板跳转链接（非必传）
     */
    private String url;

    /**
     * 模板内容字体颜色，不填默认为黑色
     */
    private String color;

    private Map<String, TemplateData> data;

}
