package com.baoning.website.controller;

import com.baoning.website.model.Comment;
import com.baoning.website.model.EntityType;
import com.baoning.website.model.HostHolder;
import com.baoning.website.service.CommentService;
import com.baoning.website.service.QuestionService;
import com.baoning.website.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * created by baoning on 2018/9/9
 */
@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;


    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try{
            Comment comment = new Comment();
            comment.setContent(content);
            if(hostHolder.getUser() != null ){
                comment.setUserId(hostHolder.getUser().getId());
            }else{
                comment.setUserId(JSONUtil.ANONYMOUS_USERID);
                //return "redirect:/reglogin";
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            commentService.addComment(comment);

            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), count);

        }catch (Exception e){
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }




}
