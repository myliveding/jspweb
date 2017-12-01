package com.dzr.web.po;

import lombok.Data;

/**
 * @author dingzr
 * @Description
 * @ClassName Wechat
 * @since 2017/12/1 10:31
 */

@Data
public class Wechat {

    private Integer id;

    private String appId;

    private String appSecret;

    private String name;

    private Integer type;

    private String token;

    private String remark;

    private Integer createTime;

    private Integer updateTime;

    private Boolean isDelete;

}
