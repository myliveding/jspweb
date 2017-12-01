package com.dzr.web.controller;

import com.dzr.web.service.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/wechatPay")
@Controller
public class WechatPayController {

    @Autowired
    WechatService wechatService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String wechatPay(HttpServletRequest req, Model model) {
        return wechatService.wechatPay(req, model);
    }


    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public void wechatNotify(HttpServletRequest request, HttpServletResponse response) {
        wechatService.wechatNotify(request, response);
    }

}
