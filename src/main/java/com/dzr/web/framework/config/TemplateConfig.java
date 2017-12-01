package com.dzr.web.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author dingzr
 * @Description
 * @ClassName TemplateConfig
 * @since 2017/9/14 10:07
 */

@Data
@Component
@ConfigurationProperties(prefix = "template")
public class TemplateConfig {

    //账户余额通知
    private String accountBalance;
    //会员积分消费提醒
    private String integralConsumption;
    //返利到帐提醒
    private String accountRebate;
    //生日提醒
    private String birthdayReminder;

}
