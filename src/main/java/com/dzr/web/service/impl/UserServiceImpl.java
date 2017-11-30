package com.dzr.web.service.impl;

import com.dzr.web.service.UserService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.dzr.web.po.User;
import com.dzr.web.mapper.UserMapper;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    public int insert(User pojo){
        return userMapper.insert(pojo);
    }

    public int insertSelective(User pojo){
        return userMapper.insertSelective(pojo);
    }

    public int insertList(List<User> pojos){
        return userMapper.insertList(pojos);
    }

    public int update(User pojo){
        return userMapper.update(pojo);
    }

    public User findUserInfo(Integer id){
        return userMapper.findUserInfo(id);
    }
}
