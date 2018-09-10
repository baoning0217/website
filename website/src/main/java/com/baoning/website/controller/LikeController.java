package com.baoning.website.controller;

import com.baoning.website.model.EntityType;
import com.baoning.website.model.HostHolder;
import com.baoning.website.service.LikeService;
import com.baoning.website.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * created by baoning on 18/09/10
 */
@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;


    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser() == null ){
            return JSONUtil.getJSONString(999);
        }
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        System.out.println("hostHolder:" + hostHolder.getUser() +  " id：" + hostHolder.getUser().getId());
        return JSONUtil.getJSONString(0, String.valueOf(likeCount));
    }


    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser() == null ){
            return JSONUtil.getJSONString(999);
        }
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return JSONUtil.getJSONString(0, String.valueOf(likeCount));
    }


}
