package com.dzr.web.po.wx;

import lombok.Data;

/**
 * @author dingzr
 * @Description
 * @ClassName WechatToken
 * @since 2017/12/1 15:59
 */

@Data
public class AccessToken {

    private String ticket; //js调用的临时凭据
    private String access_token; //accesstoken
    private String expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private Integer errcode; //错误code
    private String errmsg; //错误信息
}
