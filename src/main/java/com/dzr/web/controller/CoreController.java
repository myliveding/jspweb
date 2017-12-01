package com.dzr.web.controller;

import com.dzr.web.framework.config.WechatParams;
import com.dzr.web.util.SignUtil;
import com.dzr.web.util.wx.WechatResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理微信服务器消息转发加服务器地址验证
 * 微信服务器地址配置 http://域名/wechat/
 */
@RestController
@RequestMapping("/wechat")
public class CoreController {

    private Logger logger = Logger.getLogger(CoreController.class);

    @Autowired
    WechatParams wechatParams;

    /**
     * 确认请求来自微信服务器
     */
    @GetMapping("/")
    public String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 微信加密签名  
        String signature = request.getParameter("signature");
        // 时间戳  
        String timestamp = request.getParameter("timestamp");
        // 随机数  
        String nonce = request.getParameter("nonce");
        // 随机字符串  
        String echostr = request.getParameter("echostr");
        logger.info("signature--" + signature + "--timestamp--" + timestamp
                + "--nonce--" + nonce + "--echostr--" + echostr
                + "--wechatParams.getAppToken--" + wechatParams.getAppToken());

        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (SignUtil.checkSignature(wechatParams.getAppToken(), signature, timestamp, nonce)) {
            return echostr;
        }
        return "";
    }

    /**
     * 处理微信服务器发来的消息
     */
    @PostMapping("/")
    public String doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）  
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 调用核心业务类接收消息、处理消息  
        return WechatResponse.processRequest(request);
    }

}  