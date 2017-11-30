package com.dzr.web.controller;

import com.dzr.web.po.User;
import com.dzr.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dingzr
 * @Description 返回json信息
 * @ClassName UserRestController
 * @since 2017/11/30 15:42
 */

@RestController
@RequestMapping("/restUser")
public class UserRestController {

    @Autowired
    private UserService userService;

    /**
     * @Title 获取用户数据信息
     * @param id
     * @return
     */
    @RequestMapping(value = "findUserInfo" ,method = RequestMethod.GET)
    public User findUserInfo(@RequestParam(value = "id") Integer id) {
        return userService.findUserInfo(id);
    }

}
