package com.baoning.website.async.handler;

import com.baoning.website.async.EventHandler;
import com.baoning.website.async.EventModel;
import com.baoning.website.async.EventType;
import com.baoning.website.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by baoning on 18/09/11
 */
@Component
public class LoginExceptionHandler implements EventHandler {

    @Autowired
    MailSender mailSender;


    public void doHandler(EventModel model) {
        //发现登录异常
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"),"登录异常", "/mails/login_exception.html", map);

    }


    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }


}
