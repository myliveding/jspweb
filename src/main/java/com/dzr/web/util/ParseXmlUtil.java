/*
 * @(#)ParseXmlUtil.java 2014-2-25下午03:49:43
 * Copyright 2013 sinovatech, Inc. All rights reserved.
 */
package com.dzr.web.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析xml工具类
 * <ul>
 * <li>
 * <b>修改历史：</b><br/>
 * <p>
 * [2014-2-25下午03:49:43]gaozhanglei<br/>
 * </p>
 * </li>
 * </ul>
 */
@SuppressWarnings("unchecked")
public class ParseXmlUtil {
    private static Logger logger = LoggerFactory.getLogger(ParseXmlUtil.class);

    /**
     * 得到epg info 信息
     *
     * @param xmlStr
     * @param key
     * @param defVal
     * @return
     */
    public static String getInfoContentforXml(String xmlStr, String key, String defVal) {
        logger.info("-ParseXmlUtil-getInfoContentforXml-xmlStr:" + xmlStr);
        String regex = "<" + key + ">([^<]+)</" + key + ">";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(xmlStr);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return defVal;
    }

    /**
     * 解析xml文本
     * 用于解析简单XML报文，无重复元素
     *
     * @param xmlText xml文本
     * @return 参数Map集合
     * @throws Exception
     * @author sunju
     * @creationDate. 2014-2-19 下午04:14:18
     */
    public static Map<String, String> parseXmlText(String xmlText) throws DocumentException {
        Document document = DocumentHelper.parseText(xmlText);
        return parseXmlDocument(document);
    }

    /**
     * 解析xml文档
     * 用于解析简单XML报文，无重复元素
     *
     * @param document xml文档
     * @return 参数Map集合
     * @throws Exception
     * @author sunju
     * @creationDate. 2014-2-25 下午01:01:44
     */
    public static Map<String, String> parseXmlDocument(Document document) {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<>();
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
        // 遍历所有子节点
        for (Element e : elementList) {
            if (e.elements().size() == 0) {
                map.put(e.getName(), e.getText());
            } else {
                putElements(map, e.elements());
            }
        }
        return map;
    }

    /**
     * 递归设置元素集合
     *
     * @param map      map集合
     * @param elements 元素集合
     * @throws Exception
     * @author sunju
     * @creationDate. 2014-2-26 上午11:35:13
     */
    private static void putElements(Map<String, String> map, List<Element> elements) {
        for (Element e : elements) {
            map.put(e.getName(), e.getText());
            if (e.elements().size() > 0) {
                putElements(map, e.elements());
            }
        }
    }


}
