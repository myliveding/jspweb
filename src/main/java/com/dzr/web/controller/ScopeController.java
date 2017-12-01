package com.dzr.web.controller;

import com.dzr.web.framework.config.WechatParams;
import com.dzr.web.util.wx.WechatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Controller
@RequestMapping("/scope")
public class ScopeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WechatParams wechatParams;

    @RequestMapping("/openid")
    public ModelAndView getUserOpenIdOfScope(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String next = request.getParameter("next");
        logger.info("ScopeController：" + code + ",cn：" + next);
        // /scope/openid.do?next=rest/receiveCard.do appid cardId=111
        if (!"".equals(code) && code != null && !"".equals(next) && next != null) {
            String nextPage = next.substring(0, next.indexOf(".do"));
            String appid = next.substring(next.indexOf(".do") + 3, next.length());
            String pram = "";
            if (appid.length() > 18) {
                pram = appid.substring(18, appid.length());
                appid = appid.substring(0, 18);
            }
            logger.info("pram：" + pram + ",appid：" + appid);
            String openid = WechatUtil.webAuthorization(appid, wechatParams.getAppSecret(), code);
            logger.info("redirect:" + wechatParams.getDomain() + "/" + nextPage + "?openid=" + openid + "&appid=" + appid + "#" + pram);
            HttpSession session = request.getSession();
            session.setAttribute("openid", openid);
            session.setAttribute("appid", appid);
            logger.info("把openid：" + openid + "，appid：" + appid + "加到sesion");

            if (!"".equals(pram)) {
                logger.info(nextPage + "?" + pram);
                return new ModelAndView("redirect:" + wechatParams.getDomain() + "/" + nextPage + "?" + pram);

            } else {
                return new ModelAndView("redirect:" + wechatParams.getDomain() + "/" + nextPage);
            }

        } else {
            logger.error("未将对象引入到实例");
            request.setAttribute("error", "未将对象引入到实例");
            return new ModelAndView("redirect:error.do");
        }
    }

}
