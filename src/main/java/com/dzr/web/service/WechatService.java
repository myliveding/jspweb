package com.dzr.web.service;

import com.dzr.web.po.wx.WechatUser;
import net.sf.json.JSONObject;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dingzr
 * @Description
 * @ClassName WechatService
 * @since 2017/12/1 10:37
 */
public interface WechatService {

    /**
     * 根据openid获取用户详细信息
     *
     * @param openid
     * @return
     */
    WechatUser getUserInfo(String openid);

    /**
     * 获取页面分享信息
     *
     * @param request
     * @return
     */
    void getWechatShare(Model model, HttpServletRequest request);

    String wechatPay(HttpServletRequest req, Model model);

    void wechatNotify(HttpServletRequest request, HttpServletResponse response);

    /**
     * 发送模板消息
     *
     * @param type 待发送的消息
     * @return
     */
    JSONObject sendTemplateMessageByType(String type, String firstStr, String keyword1Str, String keyword2Str,
                                         String keyword3Str, String keyword4Str, String keyword5Str, String openId, String remarkStr, String url);


}
