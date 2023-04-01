package com.dgut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.mapper.UserMapper;
import com.dgut.model.entity.User;
import com.dgut.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
