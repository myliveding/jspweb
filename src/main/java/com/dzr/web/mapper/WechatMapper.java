package com.dzr.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import com.dzr.web.po.Wechat;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface WechatMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Wechat record);

    int insertSelective(Wechat record);

    Wechat selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Wechat record);

    int updateByPrimaryKey(Wechat record);

    Wechat selectTokenByAppId(Map<String, String> map);

    int updateByAppId(Wechat record);
}
