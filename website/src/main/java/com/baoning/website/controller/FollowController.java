package com.baoning.website.controller;

import com.baoning.website.async.EventModel;
import com.baoning.website.async.EventProducer;
import com.baoning.website.async.EventType;
import com.baoning.website.model.*;
import com.baoning.website.service.FollowService;
import com.baoning.website.service.QuestionService;
import com.baoning.website.service.UserService;
import com.baoning.website.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by baoning on 18/09/11
 */
@Controller
public class FollowController {

    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    QuestionService questionService;


    //关注用户
    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String follow(@RequestParam("userId") int userId){
        if(hostHolder.getUser() == null ){
            return JSONUtil.getJSONString(999);
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityOwnerId(userId));
        return JSONUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }


    //取消关注用户
    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollow(@RequestParam("userId") int userId){
        if(hostHolder.getUser() == null ){
            return JSONUtil.getJSONString(999);
        }
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityOwnerId(userId));
        return JSONUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }


    //关注问题
    @RequestMapping(path = {"/followQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser() == null ){
            return JSONUtil.getJSONString(999);
        }
        Question question = questionService.selectByid(questionId);
        if(question == null ){
            return JSONUtil.getJSONString(1, "问题不存在");
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(question.getUserId())
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(question.getUserId()));
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFolloweeCount(EntityType.ENTITY_QUESTION, questionId));
        return JSONUtil.getJSONString(ret ? 0 : 1, info);
    }


    //取消关注问题
    @RequestMapping(path = {"/unfollowQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser() == null ){
            return JSONUtil.getJSONString(999);
        }
        Question question = questionService.selectByid(questionId);
        if(question == null ){
            return JSONUtil.getJSONString(1, "问题不存在");
        }
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(question.getUserId())
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityOwnerId(question.getUserId()));
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFolloweeCount(EntityType.ENTITY_QUESTION, questionId));
        return JSONUtil.getJSONString(ret ? 0 : 1, info);
    }


    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId){
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);
        if(hostHolder.getUser() != null){
            model.addAttribute("followees", getUserInfo(hostHolder.getUser().getId(),followeeIds));
        }else {
            model.addAttribute("followees", getUserInfo(0, followeeIds));
        }
        return "followees";
    }


    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model,@PathVariable("uid") int userId){
        List<Integer> followerIds = followService.getFollowers(userId, EntityType.ENTITY_USER, 0, 10);
        if(hostHolder.getUser() != null){
            model.addAttribute("followers", getUserInfo(hostHolder.getUser().getId(),followerIds));
        }else {
            model.addAttribute("followers", getUserInfo(0, followerIds));
        }
        return "followers";
    }


    //取数据
    private List<ViewObject> getUserInfo(int localUserId, List<Integer> userIds ){
        List<ViewObject> userInfos = new ArrayList<>();
        for(Integer uid : userIds){
            User user = userService.getUser(uid);
            if(user == null){
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, uid));
            if(localUserId != 0 ){
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            }else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }


}
