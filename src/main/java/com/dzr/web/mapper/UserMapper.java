package com.dzr.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import com.dzr.web.po.User;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {
    int insert(@Param("pojo") User pojo);

    int insertSelective(@Param("pojo") User pojo);

    int insertList(@Param("pojos") List<User> pojo);

    int update(@Param("pojo") User pojo);

    User findUserInfo(@Param("id") Integer id);

}
