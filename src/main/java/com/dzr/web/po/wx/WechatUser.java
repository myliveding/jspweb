package com.dzr.web.po.wx;

import lombok.Data;

/**
 * @author dingzr
 * @Description
 * @ClassName WechatUser
 * @since 2017/12/1 16:01
 */

@Data
public class WechatUser {

    private String subscribe;
    private String openid;
    private String nickname;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String unionid;
    private String remark;
    private Integer groupid;

    private Integer errcode; //错误code
    private String errmsg; //错误信息
}
