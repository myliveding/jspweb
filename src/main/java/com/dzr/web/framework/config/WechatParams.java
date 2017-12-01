package com.dzr.web.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author dingzr
 * @Description
 * @ClassName WechatParams
 * @since 2017/7/31 11:45
 */

@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatParams {

    private String domain;
    private String appId;
    private String appSecret;
    private String appToken;
    private String notifyUrl;
    private String apiAddress;
    private String mchId;
    private String timeStamp;
    private String noncestr;
    private String key;
    private String sdkVersion;

}
