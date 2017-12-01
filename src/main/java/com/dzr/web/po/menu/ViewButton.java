package com.dzr.web.po.menu;

/**
 * view类型的菜单
 *
 * @author wuhao
 * @date 2015-06-01
 */
public class ViewButton extends Button {

    private String type;
    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}