package com.baoning.website.service;

import com.baoning.website.dao.UserDao;
import com.baoning.website.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * created by baoning on 2018/9/8
 */
@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getUser(int id) {
        return userDao.selectById(id);
    }

}
