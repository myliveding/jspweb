package com.dzr.web.controller;

import com.dzr.web.po.User;
import com.dzr.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author dingzr
 * @Description
 * @ClassName UserController
 * @since 2017/11/30 15:48
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * @Title 获取用户数据信息
     * @param id
     * @return
     */
    @RequestMapping(value = "findUserInfo" ,method = RequestMethod.GET)
    public String findUserInfo(Model model, @RequestParam(value = "id") Integer id) {
        User user = userService.findUserInfo(id);
        model.addAttribute("user", user);
        return "index";
    }

}
