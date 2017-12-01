package com.dzr.web.util.wx;

import com.dzr.web.po.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author dingzr
 * @Description
 * @ClassName WechatCore
 * @since 2017/12/1 11:43
 */
public class WechatResponse {

    private static Logger logger = LoggerFactory.getLogger(WechatResponse.class);

    /**
     * 处理微信发来的请求
     *
     * @param request
     * @return
     */
    public static String processRequest(HttpServletRequest request) {
        String respMessage = null;
        try {
            // 默认返回的文本消息内容
            String respContent = "HI，欢迎关注我！！！";

            // xml请求解析
            Map<String, String> requestMap = MessageUtil.parseXml(request);

            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            //消息内容
            String countent = requestMap.get("Content");
            List<Article> articles = new ArrayList<>();
            Image image = new Image();

            //对收到的消息进行解析
            // 文本消息
            switch (msgType) {
                case MessageUtil.REQ_MESSAGE_TYPE_TEXT:
                    respContent = "您发送的是文本消息！";
                    break;
                // 图片消息
                case MessageUtil.REQ_MESSAGE_TYPE_IMAGE:
                    respContent = "您发送的是图片消息！";
                    break;
                // 地理位置消息
                case MessageUtil.REQ_MESSAGE_TYPE_LOCATION:
                    respContent = "您发送的是地理位置消息！";
                    break;
                // 链接消息
                case MessageUtil.REQ_MESSAGE_TYPE_LINK:
                    respContent = "您发送的是链接消息！";
                    break;
                // 音频消息
                case MessageUtil.REQ_MESSAGE_TYPE_VOICE:
                    respContent = "您发送的是音频消息！";
                    break;
                // 事件推送
                default:
                    if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                        // 事件类型
                        String eventType = requestMap.get("Event");
                        // 订阅
                        switch (eventType) {
                            case MessageUtil.EVENT_TYPE_SUBSCRIBE: {
                                logger.info("用户关注，openid=" + fromUserName);
                                respContent = "HI！欢迎关注我的小窝...";
                                String eventKey = requestMap.get("EventKey");
                                break;
                            }
                            // 取消订阅
                            case MessageUtil.EVENT_TYPE_UNSUBSCRIBE:
                                // 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                                break;
                            // 自定义菜单点击事件
                            case MessageUtil.EVENT_TYPE_CLICK: {
                                String eventKey = requestMap.get("EventKey");
                                logger.info("自定义菜单点击事件-----" + eventKey);
                                break;
                            }
                        }
                    }
                    break;
            }

            //进行消息的应答处理
            switch (msgType) {
                case MessageUtil.RESP_MESSAGE_TYPE_TEXT:
                    // 回复文本消息
                    TextMessage textMessage = new TextMessage();
                    textMessage.setToUserName(fromUserName);
                    textMessage.setFromUserName(toUserName);
                    textMessage.setCreateTime(new Date().getTime());
                    textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    textMessage.setFuncFlag(0);
                    textMessage.setContent(respContent);
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                    logger.info("回复文本消息 " + fromUserName + " 内容 " + respContent);
                    break;
                case MessageUtil.RESP_MESSAGE_TYPE_NEWS:
                    //回复图文消息
                    NewsMessage newsMessage = new NewsMessage();
                    newsMessage.setToUserName(fromUserName);
                    newsMessage.setFromUserName(toUserName);
                    newsMessage.setCreateTime(new Date().getTime());
                    newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
                    newsMessage.setArticleCount(articles.size());
                    newsMessage.setArticles(articles);
                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
                    for (Article article : articles) {
                        logger.info("回复图文消息 " + fromUserName + " 数量 " + articles.size() + " 标题 " + article.getTitle());
                    }
                    break;
                case MessageUtil.RESP_MESSAGE_TYPE_IMAGE:
                    ImageMessage imageMessage = new ImageMessage();
                    imageMessage.setToUserName(fromUserName);
                    imageMessage.setFromUserName(toUserName);
                    imageMessage.setCreateTime(new Date().getTime());
                    imageMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_IMAGE);
                    imageMessage.setImage(image);
                    respMessage = MessageUtil.imageMessageToXml(imageMessage);
                    logger.info("回复图片消息 " + fromUserName + " 内容 " + image.getMediaId());
                    break;
            }
        } catch (Exception e) {
            logger.error("消息接收处理失败 " + e.getMessage(), e);
        }
        return respMessage;
    }
}
