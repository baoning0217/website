package com.baoning.website.controller;

import com.alibaba.fastjson.JSON;
import com.baoning.website.async.EventModel;
import com.baoning.website.async.EventProducer;
import com.baoning.website.async.EventType;
import com.baoning.website.model.*;
import com.baoning.website.service.CommentService;
import com.baoning.website.service.LikeService;
import com.baoning.website.service.QuestionService;
import com.baoning.website.service.UserService;
import com.baoning.website.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * created by baoning on 2018/4/9
 */
@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    EventProducer eventProducer;


    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if(hostHolder.getUser() == null ){
                //question.setUserId(JSONUtil.ANONYMOUS_USERID);
                return JSONUtil.getJSONString(999);
            }else{
                question.setUserId(hostHolder.getUser().getId());
            }
            if(questionService.addQuestion(question) > 0 ){
                //增加题目以后，发送事件Solr
                eventProducer.fireEvent(new EventModel(EventType.AND_QUESTION)
                        .setActorId(question.getUserId())
                        .setEntityId(question.getId())
                        .setExt("title", question.getTitle())
                        .setExt("content", question.getContent()));
                return JSONUtil.getJSONString(0);
            }
        }catch (Exception e){
            logger.error("增加问题失败" + e.getMessage());
        }
        return JSONUtil.getJSONString(1, "失败");
    }


    @RequestMapping(value = "/question/{qid}", method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("qid") int qid){
        Question question = questionService.selectByid(qid);
        model.addAttribute("question", question);

        List<Comment> commentList = commentService.getCommentByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> comments = new ArrayList<ViewObject>();
        for(Comment comment : commentList ){
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            if(hostHolder.getUser() == null ){
                vo.set("liked", 0);
            }else{
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }
            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vo.set("user", userService.getUser(comment.getUserId()));
            comments.add(vo);
        }
        model.addAttribute("comments", comments);
        return "detail";
    }



}
