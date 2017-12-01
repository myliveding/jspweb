package com.dzr.web.util.wx;


import com.dzr.web.framework.config.WechatParams;
import com.dzr.web.po.menu.Menu;
import com.dzr.web.po.wx.AccessToken;
import com.dzr.web.po.wx.WechatUser;
import com.dzr.web.util.MyX509TrustManager;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.*;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyStore;

/**
 * @author dingzr
 * @Description 微信的工具类
 * @ClassName WechatUtil
 * @since 2017/7/27 14:24
 */


public class WechatUtil {

    @Autowired
    static WechatParams wechatParams;

    private static Logger logger = LoggerFactory.getLogger(WechatUtil.class);

    //调用网页授权接口URL 通过code换取网页授权access_token
    public static final String OAUTH2_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

    //调用access_token接口URL
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    //调用微信JS接口的临时票据
    public static final String TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

    //调用获取用于卡券接口签名的api_ticket
    public static final String API_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=wx_card";

    //调用获取用户信息接口URL
    public static final String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";

    //获取消息模板ID
    public static final String MESSAGE_TEMPLATE_ID_URL = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=ACCESS_TOKEN";

    //发送消息模板消息
    public static final String MESSAGE_TEMPLATE_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";

    //调用创建菜单接口URL
    public static final String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    //调用生成二维码接口URL
    public static final String QRCODE_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";

    /**
     * 网页授权,通过code换取网页授权access_token(只有openid是真实可用的)
     * 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     * @param appid     应用ID
     * @param appsecret 应用密钥
     * @param code      填写第一步获取的code参数
     * @return {"access_token":"ACCESS_TOKEN",
    "expires_in":7200,
    "refresh_token":"REFRESH_TOKEN",
    "openid":"OPENID",
    "scope":"SCOPE"
    }
    {"errcode":40029,"errmsg":"invalid code"}
     */
    public static String webAuthorization(String appid, String appsecret, String code) {
        String userUrl = OAUTH2_ACCESS_TOKEN_URL.replace("APPID", appid).replace("SECRET", appsecret).replace("CODE", code);
        String authorJson = httpRequest(userUrl, "GET", null);
        String openid = "";
        if (!"".equals(authorJson)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                AccessToken weixin = objectMapper.readValue(authorJson, AccessToken.class);
                if (null == weixin.getOpenid() || "".equals(weixin.getOpenid())) {
                    logger.info("网页授权失败：" + weixin.getErrmsg());
                } else {
                    openid = weixin.getOpenid();
                }
            } catch (JsonParseException e) {
                logger.error("网页授权解析失败" + e.getMessage());
            } catch (IOException e) {
                logger.error("网页授权出现异常" + e.getMessage());
            }
        } else {
            logger.info("去微信公众平台的网页授权结果为空");
        }
        return openid;
    }

    /**
     * 获取真实可用的ACCESS_TOKEN
     * @param appid     应用ID
     * @param appsecret 应用密钥
     * @return
    {"access_token":"ACCESS_TOKEN","expires_in":7200}
    {"errcode":40013,"errmsg":"invalid appid"}
     */
    public static String getAccessToken(String appid, String appsecret) {
        String accessToken = "-1";
        String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appid).replace("APPSECRET", appsecret);
        String accessTokenTemp = httpRequest(requestUrl, "GET", null);
        // 如果请求成功
        if (!"".equals(accessTokenTemp)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                AccessToken weixin = objectMapper.readValue(accessTokenTemp, AccessToken.class);
                if ((null == weixin.getAccess_token()) || "".equals(weixin.getAccess_token())) {
                    logger.info("获取access_token失败：" + weixin.getErrmsg());
                }else {
                    accessToken = weixin.getAccess_token();
                    logger.info("获取到微信平台的AccessToken：" + accessToken);
                }
            } catch (JsonParseException e) {
                logger.error("获取access_token解析失败" + e.getMessage());
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                logger.error("获取access_token出现异常" + e.getMessage());
            }
        } else {
            logger.info("去微信公众平台请求获取access_token的结果为空");
        }
        return accessToken;
    }

    /**
     * 获取微信JS接口的临时票据
     * @param accessToken 有效的access_token
     * @return
     * {
    "errcode":0,
    "errmsg":"ok",
    "ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA",
    "expires_in":7200
    }
     */
    public static String getTicket(String accessToken) {
        String url = TICKET_URL.replace("ACCESS_TOKEN", accessToken);
        String ticket = httpRequest(url, "GET", null);
        logger.info("获取微信JS接口的临时票据:" + ticket);
        String jsTicket = "";
        if (!"".equals(ticket)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                AccessToken weixin = objectMapper.readValue(ticket, AccessToken.class);
                if (null == weixin.getTicket() || "".equals(weixin.getTicket())) {
                    logger.info("获取ticket失败：" + weixin.getErrmsg());
                } else {
                    jsTicket = weixin.getTicket();
                }
            } catch (JsonParseException e) {
                logger.error("获取ticket解析失败" + e.getMessage());
            } catch (IOException e) {
                logger.error("获取ticket出现异常" + e.getMessage());
            }
        } else {
            logger.info("去微信公众平台请求获取js临时票据的结果为空");
        }
        return jsTicket;
    }

    /**
     * 创建菜单
     * @param menu        菜单实例
     * @param accessToken 有效的access_token
     * @return 0表示成功，其他值表示失败
     * {"errcode":0,"errmsg":"ok"}
     * {"errcode":40018,"errmsg":"invalid button name size"}
     */
    public static void createMenu(Menu menu, String accessToken) throws IOException {
        // 拼装创建菜单的url
        String url = MENU_CREATE_URL.replace("ACCESS_TOKEN", accessToken);
        // 将菜单对象转换成json字符串
        ObjectMapper mapper = new ObjectMapper();
        String jsonMenu = mapper.writeValueAsString(menu);
        // 调用接口创建菜单
        String res = httpRequest(url, "POST", jsonMenu);
        logger.info("创建菜单的返回值：" + res);
    }

    /**
     * 获取用户详细信息
     * @param accessToken 有效的access_token
     * @param openid      对应的openid
     * @return {
    "subscribe": 1,
    "openid": "o6_bmjrPTlm6_2sgVt7hMZOPfL2M",
    "nickname": "Band",
    "sex": 1,
    "language": "zh_CN",
    "city": "广州",
    "province": "广东",
    "country": "中国",
    "headimgurl":  "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4
    eMsv84eavHiaiceqxibJxCfHe/0",
    "subscribe_time": 1382694957,
    "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
    "remark": "",
    "groupid": 0,
    "tagid_list":[128,2] 用户被打上的标签ID列表
    }
     */
    public static WechatUser getUserInfo(String accessToken, String openid) {
        WechatUser wechatUser = new WechatUser();

        String userUrl = USER_INFO_URL.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
        String userJson = httpRequest(userUrl, "GET", null);
        if (!"".equals(userJson)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                wechatUser = objectMapper.readValue(userJson, WechatUser.class);
            } catch (JsonParseException e) {
                logger.error("用户信息json解析失败" + e.getMessage());
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                logger.error("获取用户信息出现异常" + e.getMessage());
            }
        } else {
            logger.error("获取用户信息为空");
        }
        return wechatUser;
    }

    /**
     * 发送模板消息
     * @param accessToken 有效的access_token
     * @param content     待发送的消息对象（json）
     * @return
    {
    "errcode":0,
    "errmsg":"ok",
    "msgid":200228332
    }
     */
    public static String sendTemplateMessage(String accessToken, String content) {
        String url = MESSAGE_TEMPLATE_SEND_URL.replace("ACCESS_TOKEN", accessToken);
        // 调用接口发送消息
        String json = httpRequest(url, "POST", content);
        logger.info("发送微信提醒模版，发送内容：" + content + "，结果：" + json);
        return json;
    }

    /**
     * http请求  handleRequest
     * @param requestUrl
     * @param requestMethod
     * @param outputStr
     * @return
     */
    public static String httpRequest(String requestUrl, String requestMethod, String outputStr) {
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
        } catch (ConnectException ce) {
            logger.error("Weixin server connection timed out.", ce);
        } catch (Exception e) {
            logger.error("https request error:{}", e);
        }
        return buffer.toString();
    }

    /**
     * https的请求（微信支付在https环境下需要加载微信api安全证书（下载自微信商户平台））
     * @param requestUrl
     * @param requestMethod
     * @param outputStr
     * @return
     */
    public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        String jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // 证书文件(微信商户平台-账户设置-API安全-API证书-下载证书)
            String keyStorePath = WechatUtil.class.getResource("/").getPath() + "/apiclient_cert.p12";
            // 证书密码（默认为商户ID）
            String password = wechatParams.getMchId();
            // 实例化密钥库
            KeyStore ks = KeyStore.getInstance("PKCS12");
            // 获得密钥库文件流
            FileInputStream fis = new FileInputStream(keyStorePath);
            // 加载密钥库
            ks.load(fis, password.toCharArray());
            // 关闭密钥库文件流
            fis.close();
            // 实例化密钥库
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            // 初始化密钥工厂
            kmf.init(ks, password.toCharArray());

            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(kmf.getKeyManagers(), null, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            jsonObject = buffer.toString();
        } catch (ConnectException ce) {
            logger.error("Weixin server connection timed out.", ce);
        } catch (Exception e) {
            logger.error("https request error:{}", e);
        }
        return jsonObject;
    }

}
