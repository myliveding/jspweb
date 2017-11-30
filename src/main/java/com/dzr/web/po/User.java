package com.dzr.web.po;

import lombok.Data;

/**
 * @author dingzr
 * @Description
 * @ClassName User
 * @since 2017/11/30 15:23
 */

@Data
public class User {

    private Integer id;

    private String name;

    private Integer phone;

    private String email;

    private String openid;

    private String neckname;

    private Integer sex;

    private String headimgurl;

    private Boolean subscribe;

    private Integer subscribeTime;

    private String remark;

    private Boolean isDelete;


}
