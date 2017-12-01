package com.dzr.web.service.impl;

import com.dzr.web.framework.config.Constant;
import com.dzr.web.framework.config.TemplateConfig;
import com.dzr.web.framework.config.WechatParams;
import com.dzr.web.framework.exception.ApiException;
import com.dzr.web.mapper.WechatMapper;
import com.dzr.web.po.Wechat;
import com.dzr.web.po.message.Template;
import com.dzr.web.po.message.TemplateData;
import com.dzr.web.po.wx.*;
import com.dzr.web.service.OkHttpService;
import com.dzr.web.service.WechatService;
import com.dzr.web.util.*;
import com.dzr.web.util.wx.MessageUtil;
import com.dzr.web.util.wx.WechatUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import net.sf.json.JSONObject;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("wechatService")
@Transactional
public class WechatServiceImpl implements WechatService{

    @Resource
    OkHttpService okHttpService;
    @Resource
    private WechatMapper wechatMapper;
    @Resource
    WechatParams wechatParams;
    @Resource
    TemplateConfig templateConfig;

    private Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);

    /**
     * 获取微信公众号的access_token
     *
     * @param appId appid
     */
    private String getAccessToken(String appId) {

        Integer nowTime = DateUtils.getNowTime();
        String accessToken;
        Map<String, String> map = new HashMap<>();
        map.put("appId", appId);
        map.put("type", "0");
        Wechat wechatToken = wechatMapper.selectTokenByAppId(map);
        if (wechatToken != null) {
            boolean isUpdate = false;
            int timeDB = wechatToken.getUpdateTime();

            if (wechatToken.getToken() != null && !"".equals(wechatToken.getToken())) {
                if ((nowTime - timeDB) < 6000) {
                    accessToken = wechatToken.getToken();
                    logger.info("当前使用的时间差小于6000s，取值数据库的AccessToken：" + accessToken);
                } else {
                    accessToken = WechatUtil.getAccessToken(appId, wechatParams.getAppSecret());
                    if (!"-1".equals(accessToken)) {
                        isUpdate = true;
                    } else {
                        logger.info("微信平台获取的accessToken的结果为空");
                    }
                }
            } else {
                accessToken = WechatUtil.getAccessToken(appId, wechatParams.getAppSecret());
                if (!"-1".equals(accessToken)) {
                    logger.info("数据库中有对象但是没有token的值，获取服务器AccessToken：" + accessToken);
                    isUpdate = true;
                } else {
                    logger.info("微信平台获取的accessToken的结果为空");
                }
            }
            //更新token和时间
            if (isUpdate) {
                updateWechat(wechatToken.getId(), accessToken, nowTime);
            }
        } else {
            //appid对应的微信公众平台为空
            accessToken = WechatUtil.getAccessToken(wechatParams.getAppId(), wechatParams.getAppSecret());
            if (!"-1".equals(accessToken)) {
                logger.info("数据库中没有初始值,此公众号第一次获取AccessToken: " + accessToken);
                wechatToken.setAppId(wechatParams.getAppId());
                wechatToken.setAppSecret(wechatParams.getAppSecret());
                wechatToken.setName("XX");
                wechatToken.setType(0);
                wechatToken.setToken(accessToken);
                wechatToken.setRemark("初始化AccessToken");
                wechatToken.setCreateTime(nowTime);
                wechatToken.setUpdateTime(nowTime);
                wechatToken.setIsDelete(false);
                wechatMapper.insertSelective(wechatToken);
            } else {
                logger.error("微信平台获取的AccessToken的结果为空");
            }
        }
        return accessToken;
    }

    /**
     * 获取用于调用微信JS接口的临时票据
     * @param appId 微信公众编号
     */
    private String getJsTicket(String appId) {

        String ticket;
        Integer nowTime = DateUtils.getNowTime();
        String accessToken = this.getAccessToken(appId);
        Map<String, String> map = new HashMap<>();
        map.put("appId", appId);
        map.put("type", "1");
        Wechat jsapiTicket = wechatMapper.selectTokenByAppId(map);
        if (jsapiTicket != null) {
            Boolean isUpdate = false;
            if (jsapiTicket.getToken() != null && !"".equals(jsapiTicket.getToken())) {
                int timeDB = jsapiTicket.getUpdateTime();
                if ((nowTime - timeDB) > 6000) {
                    ticket = WechatUtil.getTicket(accessToken);
                    if (!"".equals(ticket)) {
                        isUpdate = true;
                    }
                } else {
                    ticket = jsapiTicket.getToken();
                }
            } else {
                //第一次获取
                ticket = WechatUtil.getTicket(accessToken);
                if (!"".equals(ticket)) {
                    isUpdate = true;
                }
            }
            //更新token和时间
            if (isUpdate) {
                updateWechat(jsapiTicket.getId(), ticket, nowTime);
            }
        } else {
            //appid对应的微信公众平台为空
            ticket = WechatUtil.getTicket(accessToken);
            if (!"".equals(ticket)) {
                logger.info("数据库中没有初始值,此公众号第一次获取ticket: " + ticket);
                jsapiTicket.setAppId(wechatParams.getAppId());
                jsapiTicket.setAppSecret(wechatParams.getAppSecret());
                jsapiTicket.setName("臻品");
                jsapiTicket.setType(1);
                jsapiTicket.setToken(ticket);
                jsapiTicket.setRemark("初始化jsapiTicket");
                jsapiTicket.setCreateTime(nowTime);
                jsapiTicket.setUpdateTime(nowTime);
                jsapiTicket.setIsDelete(false);
                wechatMapper.insertSelective(jsapiTicket);
            } else {
                logger.info("微信平台获取的jsapiTicket结果为空");
            }
        }
        return ticket;
    }

    /**
     * 更新值
     *
     * @param id
     * @param token
     * @param nowTime
     */
    private void updateWechat(Integer id, String token, Integer nowTime) {
        Wechat newWechat = new Wechat();
        newWechat.setId(id);
        newWechat.setToken(token);
        newWechat.setUpdateTime(nowTime);
        wechatMapper.updateByPrimaryKeySelective(newWechat);
    }

    /**
     * 根据openid获取用户详细信息
     * @param openid
     * @return
     */
    public WechatUser getUserInfo(String openid) {
        //获取微信服务用户数据
        String accessToken = this.getAccessToken(wechatParams.getAppId());
        logger.info("根据openid获取用户详细信息前,获取的accessToken：" + accessToken);

        WechatUser userInfo = WechatUtil.getUserInfo(accessToken, openid);
        if (null != userInfo.getErrmsg() && userInfo.getErrmsg().contains("access_token is invalid")) {
            accessToken = this.getAccessTokenForError(wechatParams.getAppId());
            logger.info("根据openid获取用户详细信息前,立即获取的accessToken：" + accessToken);
            userInfo = WechatUtil.getUserInfo(accessToken, openid);
        }
        logger.info("根据openid获取用户详细信息返回值：" + userInfo.getSubscribe());
        return userInfo;
    }

    /**
     * 根据appid和openid判断当前用户是否关注公众号
     * @param openid
     * @return true为已关注  false未关注
     */
    public boolean isSubscribe(String openid) {
        logger.info("根据appid和openid判断当前用户是否关注公众号,openid为：" + openid);
        if (!"".equals(openid)) {
            WechatUser wechatUser = this.getUserInfo(openid);
            if (null != wechatUser.getSubscribe()) {
                String subscribe = wechatUser.getSubscribe();
                logger.info("根据appid和openid判断当前用户是否关注公众号,结果为(0为未关注,1为已关注)：" + subscribe);
                if ("1".equalsIgnoreCase(subscribe)) { //已关注
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * AccessToken错误重新获取
     *
     * @param appId appid
     * @return 返回新的accesstoken
     */
    private String getAccessTokenForError(String appId) {
        Integer nowTime = DateUtils.getNowTime();
        //appid对应的微信公众平台为空
        String accessToken = WechatUtil.getAccessToken(wechatParams.getAppId(), wechatParams.getAppSecret());
        if (!"-1".equals(accessToken)) {
            logger.info("数据库中值有误重新获取AccessToken：" + accessToken);
            updateByAppId(accessToken, nowTime);
        }
        return accessToken;
    }

    /**
     * 把获取的最新Access_token存入数据库
     * @param token AccessToken
     * @param nowTime 当前时间
     */
    private void updateByAppId(String token, Integer nowTime) {
        Wechat wechatToken = new Wechat();
        wechatToken.setAppId(wechatParams.getAppId());
        wechatToken.setType(0);
        wechatToken.setToken(token);
        wechatToken.setUpdateTime(nowTime);
        wechatToken.setRemark("数据库中值有误重新获取AccessToken");
        wechatMapper.updateByAppId(wechatToken);
    }

    /**
     * 获取页面分享信息
     *
     * @param request 请求
     */
    public void getWechatShare(Model model, HttpServletRequest request) {

        String url = wechatParams.getDomain()
                + request.getContextPath() // 项目名称
                + request.getServletPath(); // 请求页面或其他地址
        if (StringUtils.isNotEmpty(request.getQueryString())) {
            url = url + "?" + (request.getQueryString()); //url后面的参数
        }
        logger.info("JS调用时的确切路径，需要在加密时使用：" + url); // 当前网页的URL，不包含#及其后面部分
        model.addAttribute("url", url);
        try {
            String jsTicket = getJsTicket(wechatParams.getAppId());
            logger.info("jsapi_ticket:" + jsTicket);
            model.addAttribute("ticket", jsTicket);
            String signature = SignUtil.getSignature(jsTicket,
                    wechatParams.getTimeStamp(), wechatParams.getNoncestr(), url);
            logger.info("signature:" + signature);
            model.addAttribute("signature", signature);
        } catch (Exception e) {
            logger.error("调用去获取分享相关信息出错..." + e.getMessage());
            e.printStackTrace();
        }
        model.addAttribute("timestamp", wechatParams.getTimeStamp());
        model.addAttribute("noncestr", wechatParams.getNoncestr());
        model.addAttribute("appid", wechatParams.getAppId());
    }


    /**
     * 微信预支付调用方法
     *
     * @param request 请求
     * @param model 模型
     * @return 返回页面
     */
    public String wechatPay(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Object openidObj = session.getAttribute("openid");
        String openid = openidObj.toString();
        logger.info("支付调用时获取的openid为：" + openid);
        if (null != openid && !"".equals(openid)) {

            String productId = request.getParameter("productId");
            if (StringUtils.isEmpty(productId)) {
                request.setAttribute("error", "商品不能为空");
                return "error";
            }

            //商品价格
            double orderAmt = Double.valueOf(request.getParameter("orderAmt"));
            String payAmt = new java.text.DecimalFormat("#0.00").format(orderAmt);
            logger.info("payAmt（元）:" + payAmt);
            //这个时候还没有把金额转换成分
            model.addAttribute("payAmt", payAmt);

            //把支付金额进行转换成分
            if (StringUtils.isEmpty(payAmt)) {
                payAmt = "0";
            } else {
                int index = payAmt.indexOf(".");
                int length = payAmt.length();
                Long amLong;
                if (index == -1) {
                    amLong = Long.valueOf(payAmt + "00");
                } else if (length - index >= 3) {
                    amLong = Long.valueOf((payAmt.substring(0, index + 3)).replace(".", ""));
                } else if (length - index == 2) {
                    amLong = Long.valueOf((payAmt.substring(0, index + 2)).replace(".", "") + 0);
                } else {
                    amLong = Long.valueOf((payAmt.substring(0, index + 1)).replace(".", "") + "00");
                }
                payAmt = amLong + "";
                logger.info("order_money（分）:" + payAmt);
            }

            //调用微信支付
            try {
                String nonceStr = RandomStringUtils.random(30, "123456789qwertyuioplkjhgfdsazxcvbnm"); // 8位随机数
                //项目系统里面不去生成订单号，但是支付需要，这里生成个随机得订单号
                String orderNo = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
                //定义订单名称
                String productName = "XXX支付订单";

                ReportReqData reportReqData = new ReportReqData(productId, productName, openid, orderNo, payAmt, nonceStr);
                XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
                Annotations.configureAliases(xStreamForRequestPostData, ReportReqData.class);
                String postDataXML = xStreamForRequestPostData.toXML(reportReqData);
                logger.info("wechat send postDataXML：" + postDataXML);
                String jsonObject = WechatUtil.httpRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", postDataXML);
                logger.info("wechat return jsonObject：" + jsonObject);
                Map<String, String> map = ParseXmlUtil.parseXmlText(jsonObject);
                String prepayId = map.get("prepay_id");
                Map<String, String> paymap = new HashMap<>();
                String paytimeStamp = new Date().getTime() + "";
                String paynonceStr = RandomStringUtils.random(30, "123456789qwertyuioplkjhgfdsazxcvbnm");
                paymap.put("appId", wechatParams.getAppId());
                paymap.put("timeStamp", paytimeStamp);
                paymap.put("nonceStr", paynonceStr);
                paymap.put("package", "prepay_id=" + prepayId);
                paymap.put("signType", "MD5");
                String pay2sign = SignUtil.getSign(paymap, wechatParams.getKey());
                model.addAttribute("appid", wechatParams.getAppId());
                model.addAttribute("timeStamp", paytimeStamp);
                model.addAttribute("nonceStr", paynonceStr);
                model.addAttribute("_package", "prepay_id=" + prepayId);
                model.addAttribute("paySign", pay2sign);
                logger.info("下一步返回页面..." + prepayId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return "pay";
    }

    /**
     * 微信支付的异步通知
     * @param request request
     * @param response response
     */
    public void wechatNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/xml");
            Map<String, String> requestMap = MessageUtil.parseXml(request);
            String returnCode = requestMap.get("return_code");
            String returnMsg = requestMap.get("return_msg");
            logger.info("return_code:" + returnCode);
            logger.info("return_msg:" + returnMsg);
            String serialNo = ""; // 交易流水号
            String orderNo = ""; // 订单号
            String openid = "";
            String productId = "";
            String thirdPayAmt = "0";//订单金额
            if ("SUCCESS".equals(returnCode)) {
                for (String key : requestMap.keySet()) {
                    logger.info("key=------- " + key + " and value=---- " + requestMap.get(key));
                    if ("transaction_id".equals(key)) {
                        serialNo = requestMap.get(key);
                    } else if ("out_trade_no".equals(key)) {
                        orderNo = requestMap.get(key);
                    } else if ("openid".equals(key)) {
                        openid = requestMap.get(key);
                    } else if ("attach".equals(key)) {
                        productId = requestMap.get(key);
                    } else if ("total_fee".equals(key)) {
                        thirdPayAmt = calculatePayAmt(requestMap.get(key));
                    }
                }
                logger.info("交易流水号serialNo:" + serialNo);
                logger.info("商品ID:" + productId);
                logger.info("订单号orderNo:" + orderNo);
                logger.info("订单金额payAmt:" + thirdPayAmt);
                logger.info("支付回调用时获取的openid为：" + openid);

                if(StringUtils.isEmpty(orderNo)){
                    logger.info("异步通知，获取到得订单号为空");
                    return;
                }

                if(StringUtils.isEmpty(openid)){
                    logger.info("异步通知，获取到得openid为空");
                    return;
                }
                //调用后台支付成功异步接口
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("openid", openid);
                paramMap.put("serial_no", serialNo);
                JSONObject resultStr = JSONObject.fromObject(okHttpService.post(Constant.TSET, paramMap, MediaType.APPLICATION_FORM_URLENCODED_VALUE));
                if (resultStr.containsKey("error_code") && 0 == resultStr.getInt("error_code")) {
                    logger.info("微信支付确认订单完成:" + orderNo);
                    String resXml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml> ";
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
                    bufferedOutputStream.write(resXml.getBytes());
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                } else {
                    response.getWriter().println("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[支付发生异常，请联系无忧保客服400-111-8900]]></return_msg></xml>");//支付成功，确认清单失败的处理
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 支付成功处理微信返回得支付金额
     * @param totalFee 待处理金额
     * @return 返回计算结果
     */
    private String calculatePayAmt(String totalFee){
        try {
            StringBuilder result = new StringBuilder();
            if (totalFee.length() == 1) {
                result.append("0.0").append(totalFee);
            } else if (totalFee.length() == 2) {
                result.append("0.").append(totalFee);
            } else {
                String intString = totalFee.substring(0, totalFee.length() - 2);
                for (int i = 1; i <= intString.length(); i++) {
                    if ((i - 1) % 3 == 0 && i != 1) {
                        result.append(",");
                    }
                    result.append(intString.substring(intString.length() - i, intString.length() - i + 1));
                }
                result.reverse().append(".").append(totalFee.substring(totalFee.length() - 2));
            }
            totalFee = result.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return totalFee;
    }

    /**
     * 发送模板消息
     *
     * @param type 待发送的消息 0账户余额通知 1会员积分消费提醒 2返利到帐提醒 3生日提醒
     * @param firstStr  第一个属性
     * @param keyword1Str   1
     * @param keyword2Str   2
     * @param keyword3Str   3
     * @param keyword4Str   4
     * @param keyword5Str   4
     * @param openId    openid
     * @param remarkStr 备注
     * @param url   非必填
     * @return json
     */
    public JSONObject sendTemplateMessageByType(String type, String firstStr, String keyword1Str,
                                                String keyword2Str, String keyword3Str, String keyword4Str,
                                                String keyword5Str, String openId, String remarkStr, String url) {

        if (StringUtils.isEmpty(type)) {
            throw new ApiException(10007, "传入参数消息类型");
        } else {
            if (type.equals("1")) {
                if (StringUtils.isEmpty(keyword3Str)) {
                    throw new ApiException(10007, "传入参数keyword3");
                }
            }
        }
        if (StringUtils.isEmpty(openId)) {
            throw new ApiException(10007, "传入参数openId");
        }
        if (StringUtils.isEmpty(firstStr)) {
            throw new ApiException(10007, "传入参数first");
        }
        if (StringUtils.isEmpty(keyword1Str)) {
            throw new ApiException(10007, "传入参数keyword1");
        }
        if (StringUtils.isEmpty(keyword2Str)) {
            throw new ApiException(10007, "传入参数keyword2");
        }

        logger.info("发送用户openid：" + openId + "的类型(0账户余额通知 1会员积分消费提醒 2返利到帐提醒 3生日提醒)type："
                + type + "的微信提醒，相关参数如下："
                + "first-" + firstStr + "，keyword1-" + keyword1Str + "，keyword2-" + keyword2Str
                + "，keyword3-" + keyword3Str + "，keyword4-" + keyword4Str + "，keyword5-" + keyword5Str
                + "，remark-" + remarkStr + "，url-" + url);

        //发送的信息对象
        Template template = new Template();
        template.setUrl(url);  //模板跳转链接，非必填
        template.setTouser(openId);

        Map<String, TemplateData> map = new HashMap<>();
        TemplateData first = new TemplateData();
        first.setValue(firstStr + "\n");
        map.put("first", first);

        TemplateData keyword1 = new TemplateData();
        keyword1.setValue(keyword1Str);

        TemplateData keyword2 = new TemplateData();
        keyword2.setValue(keyword2Str);

        TemplateData keyword3 = new TemplateData();
        keyword3.setValue(keyword3Str);

        TemplateData keyword4 = new TemplateData();
        keyword4.setValue(keyword4Str);

        TemplateData keyword5 = new TemplateData();
        keyword5.setValue(keyword5Str);

        TemplateData remark = new TemplateData();
        remark.setValue(remarkStr);

        switch (type) {
            //账户余额通知
            case "0":
                template.setTemplate_id(templateConfig.getAccountBalance());
                map.put("keyword1", keyword1);//变动金额
                map.put("keyword2", keyword2);//账户余额
                break;
            case "1":
                //会员积分消费提醒
                template.setTemplate_id(templateConfig.getIntegralConsumption());
                map.put("XM", keyword1);//姓名
                map.put("KH", keyword2);//会员卡号
                map.put("CONTENTS", keyword3);//内容
                break;
            case "2":
                //返利到帐提醒
                template.setTemplate_id(templateConfig.getAccountRebate());
                map.put("keyword1", keyword1);//金额
                map.put("keyword2", keyword2);//时间
                break;
            case "3":
                //生日提醒
                template.setTemplate_id(templateConfig.getBirthdayReminder());
                map.put("keyword1", keyword1);
                map.put("keyword2", keyword2);
                break;
            default:
                break;
        }
        map.put("remark", remark);
        template.setData(map);

        String content = JSONObject.fromObject(template).toString();
        logger.info("发送消息" + content);
        String accessToken = getAccessToken(wechatParams.getAppId());
        JSONObject jsonObject = JSONObject.fromObject(WechatUtil.sendTemplateMessage(accessToken, content));
        if (null != jsonObject) {
            logger.info("发送消息结果  " + jsonObject.toString());
        } else {
            logger.info("发送消息结果 null");
        }
        return jsonObject;
    }


}
