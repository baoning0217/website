package com.baoning.website.service;

import com.baoning.website.dao.LoginTicketDao;
import com.baoning.website.dao.UserDao;
import com.baoning.website.model.LoginTicket;
import com.baoning.website.model.User;
import com.baoning.website.util.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by baoning on 2018/9/8
 */
@Service
public class UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    LoginTicketDao loginTicketDao;


    public Map<String, String> register(String username, String password){
        Map<String, String> map = new HashMap<String, String>();

        //验证注册用户名必须是邮箱格式或者手机号格式
        String phone_pattern = "0?(13|14|15|17|18|19)[0-9]{9}";//手机号正则
        String email_pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";//邮箱正则

        boolean phone_check = Pattern.compile(phone_pattern).matcher(username).matches();
        boolean email_check = Pattern.compile(email_pattern).matcher(username).matches();

        if(StringUtils.isBlank(username)){
            map.put("msg", "用户名不能为空!");
            return map;
        }
        if(!email_check) {
            map.put("msg", "用户格式不正确，请使用邮箱注册");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空!");
            return map;
        }
        User user = userDao.selectByName(username);
        if(user != null){
            map.put("msg","用户名已经注册!");
            return map;
        }
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://114.255.95.20/oa/images/test.jpg"));
        user.setPassword(Md5Util.MD5(password + user.getSalt()));

        userDao.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }


    public Map<String, String> login(String username, String password){
        Map<String, String> map = new HashMap<String, String>();
        if(StringUtils.isBlank(username)){
            map.put("msg", "用户名不能为空!");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空!");
            return map;
        }
        User user = userDao.selectByName(username);
        if(user == null){
            map.put("msg","用户名不存在!");
            return map;
        }
        if(!Md5Util.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msg", "密码或用户名错误!");
            return map;
        }
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }


    public void logout(String ticket){
        loginTicketDao.updateStatus(ticket, 1);
    }


    public User getUser(int id) {
        return userDao.selectById(id);
    }


    /*
    *下发ticket
     */
    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(3600*24*7 + date.getTime());
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDao.addTicket(loginTicket);
        return loginTicket.getTicket();
    }


}
