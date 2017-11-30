package com.dzr.web.service;

import com.dzr.web.po.User;

import java.util.List;

/**
 * @author dingzr
 * @Description
 * @ClassName UserService
 * @since 2017/11/30 15:42
 */
public interface UserService {

    int insert(User pojo);

    int insertSelective(User pojo);

    int insertList(List<User> pojos);

    int update(User pojo);

    User findUserInfo(Integer id);

}
