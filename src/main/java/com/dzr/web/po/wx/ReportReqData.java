package com.dzr.web.po.wx;


import com.dzr.web.framework.config.Constant;
import com.dzr.web.util.Signature;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Date: 2014/11/12
 * Time: 17:05
 */
@XStreamAlias("xml")
@Data
public class ReportReqData {

    //每个字段具体的意思请查看API文档
    private String appid = Constant.APP_ID;
    private String attach; //附加信息可作为自定义参数使用
    private String body;
    private String mch_id = Constant.MCH_ID;
    private String nonce_str;
    private String notify_url = Constant.NOTIFY_URL;
    private String openid;
    private String out_trade_no;
    private String spbill_create_ip;
    private String total_fee;
    private String trade_type = "JSAPI";
    private String sign;


    public ReportReqData(String goodId, String goodname, String openid,
                         String orderid, String totalfee, String nonceStr) {
        setAttach(goodId);
        setBody(goodname);
        setOpenid(openid);
        setNonce_str(nonceStr);
        setOut_trade_no(orderid);
        setTotal_fee(totalfee);
        String sign = Signature.getSign(toMap());
        setSign(sign);//把签名数据设置到Sign这个属性中
    }


    private Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);
                if (obj != null) {
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
