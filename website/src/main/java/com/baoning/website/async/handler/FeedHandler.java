package com.baoning.website.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.baoning.website.async.EventHandler;
import com.baoning.website.async.EventModel;
import com.baoning.website.async.EventType;
import com.baoning.website.model.*;
import com.baoning.website.service.*;
import com.baoning.website.util.JSONUtil;
import com.baoning.website.util.JedisAdapter;
import com.baoning.website.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * created by baoning on 18/04/11
 */
@Component
public class FeedHandler implements EventHandler {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;


    public void doHandler(EventModel model) {
        Random random = new Random();
        model.setActorId(1+ random.nextInt());

        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setUserId(model.getActorId());
        feed.setType(model.getType().getValue());
        feed.setData(buildFeedData(model));
        if(feed.getData() == null){
            return;
        }
        feedService.addFeed(feed);

        //给事件的粉丝推
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
        followers.add(0);
        for(int follower : followers ){
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
        }


    }


    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
    }


    //获取Feed信息
    private String buildFeedData(EventModel model){
        Map<String, String> map = new HashMap<String, String>();
        User actor = userService.getUser(model.getActorId());
        if(actor == null){
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if( model.getType() == EventType.COMMENT ||
                (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION )){

            Question question = questionService.selectByid(model.getEntityId());
            if(question == null){
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }


}
