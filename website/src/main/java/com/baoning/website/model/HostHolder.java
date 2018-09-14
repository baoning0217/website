package com.baoning.website.model;

import org.springframework.stereotype.Component;

/**
 * created by baoning on 2018/4/9
 */
@Component
public class HostHolder {

    //当前线程，保存当前用户
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }


}
