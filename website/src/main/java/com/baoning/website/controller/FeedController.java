package com.baoning.website.controller;

import com.baoning.website.model.EntityType;
import com.baoning.website.model.Feed;
import com.baoning.website.model.HostHolder;
import com.baoning.website.service.FeedService;
import com.baoning.website.service.FollowService;
import com.baoning.website.util.JedisAdapter;
import com.baoning.website.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * created by baoning on 18/04/12
 */
@Controller
public class FeedController {

    @Autowired
    FeedService feedService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;


    @RequestMapping(path = {"/pullfeeds"}, method = {RequestMethod.GET})
    private String getPullFeeds(Model model){
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if(localUserId != 0 ){
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }


    @RequestMapping(path = {"/pushfeeds"}, method = {RequestMethod.GET})
    private String getPushFeeds(Model model){
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10 );
        List<Feed> feeds = new ArrayList<Feed>();
        for(String feedId : feedIds ){
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if(feed == null ){
                continue;
            }
            feeds.add(feed);
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }



}
