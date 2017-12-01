package com.dzr.web.po.message;


import lombok.Data;

/**
 * 微信消息模板数据对象
 */
@Data
public class TemplateData {
    /**
     * 数据值
     */
    private String value;

    /**
     * 数据内容颜色
     */
    private String color;

}
