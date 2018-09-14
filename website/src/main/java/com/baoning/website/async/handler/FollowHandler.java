package com.baoning.website.async.handler;

import com.baoning.website.async.EventHandler;
import com.baoning.website.async.EventModel;
import com.baoning.website.async.EventType;
import com.baoning.website.model.EntityType;
import com.baoning.website.model.Message;
import com.baoning.website.model.User;
import com.baoning.website.service.MessageService;
import com.baoning.website.service.UserService;
import com.baoning.website.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * created by baoning on 18/04/11
 */
@Component
public class FollowHandler implements EventHandler {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;


    public void doHandler(EventModel model) {
        Message message = new Message();
        message.setFromId(JSONUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());
        if(model.getEntityType() == EntityType.ENTITY_QUESTION){
            message.setContent("用户:" + user.getName() + "关注了你的问题! ,http://localhost:8080/question/" + model.getEntityId() );
        }else {
            message.setContent("用户:" + user.getName() + "关注了你! ,http://localhost:8080/user/" + model.getActorId() );
        }
        messageService.addMessage(message);
    }


    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }


}
