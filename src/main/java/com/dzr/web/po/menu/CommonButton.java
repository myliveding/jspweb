package com.dzr.web.po.menu;

/**
 * 普通按钮（子按钮）
 * @author wuhao
 * @date 2015-06-01
 */
public class CommonButton extends Button {

    private String type;
    private String key;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}