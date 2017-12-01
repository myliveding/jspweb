package com.dzr.web.util.wx;

import com.dzr.web.po.menu.Button;
import com.dzr.web.po.menu.Menu;
import com.dzr.web.po.menu.ViewButton;

import java.io.IOException;

public class MenuManager {

    /**
     * 组装菜单数据
     * @return
     */
    private static Menu getMenu(String appid, String url) {

        ViewButton btn11 = new ViewButton();
        btn11.setName("臻品展示");
        btn11.setType("view");
        btn11.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url + "/scope/openid.do?next=login/productCates.do" + appid + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");

        ViewButton btn21 = new ViewButton();
        btn21.setName("活动");
        btn21.setType("view");
        btn21.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url + "/scope/openid.do?next=login/activitys.do" + appid + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");

//        ViewButton btn22 = new ViewButton();
//        btn22.setName("在线超市");
//        btn22.setType("view");
//        btn22.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url + "/scope/openid.do?next=member/gotoShop.do" + appid + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
//
//        ViewButton btn23 = new ViewButton();
//        btn23.setName("优惠活动");
//        btn23.setType("view");
//        btn23.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url + "/scope/openid.do?next=member/gotoDiscount.do" + appid + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");

        ViewButton btn31 = new ViewButton();
        btn31.setName("臻品会员");
        btn31.setType("view");
        btn31.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url + "/scope/openid.do?next=login/index.do" + appid + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
//        ViewButton btn32 = new ViewButton();
//        btn32.setName("我的预定");
//        btn32.setType("view");
//        btn32.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url + "/scope/openid.do?next=member/gotoMyOrder.do" + appid + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
//        ViewButton btn33 = new ViewButton();
//        btn33.setName("个人中心");
//        btn33.setType("view");
//        btn33.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url + "/scope/openid.do?next=member/gotoUserCenter.do" + appid + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");

        //主菜单
//        ComplexButton mainBtn1 = new ComplexButton();
//        mainBtn1.setName("在线预定");
//        mainBtn1.setSub_button(new Button[]{btn11});

//        ComplexButton mainBtn2 = new ComplexButton();
//        mainBtn2.setName("走进惠机");
//        mainBtn2.setSub_button(new Button[]{btn21, btn22, btn23});

//        ComplexButton mainBtn3 = new ComplexButton();
//        mainBtn3.setName("我的惠机");
//        mainBtn3.setSub_button(new Button[]{btn31, btn32, btn33});

        Menu menu = new Menu();
        menu.setButton(new Button[]{btn11, btn21, btn31});
//        menu.setButton(new Button[]{btn11, mainBtn2, mainBtn3});
        return menu;
    }

    public static void main(String[] args) throws IOException {
//        System.err.println(WeixinUtil.getAccessTokenForWXService("wxbb336e8a40b636d6","aa389c3d29c333ebdad2d50b95160d64").getToken());

        String accessToken = "T0D--YnOe3SbQ-ChJcD6hpGzK3V5B1Xgo1CqotssUrJGktmIwlWQWqUgEAgb361r_iVOxtcaD7TYj4BoeYUUM7ZWYdhLHSiw0Bsf2bV5Xp6JFkWG5nCBuJYQrYfHhXL5GEGgAFAOMO";
        String url = "http://www.zxbmm.cn";
        String appId = "wxfb086a201e1de746";

        System.out.println(url + " = " + appId);
        // 调用接口创建菜单
        WechatUtil.createMenu(getMenu(appId, url), accessToken);
    }

}