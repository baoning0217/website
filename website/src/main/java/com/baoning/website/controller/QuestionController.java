package com.baoning.website.controller;

import com.alibaba.fastjson.JSON;
import com.baoning.website.model.HostHolder;
import com.baoning.website.model.Question;
import com.baoning.website.service.QuestionService;
import com.baoning.website.service.UserService;
import com.baoning.website.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * created by baoning on 2018/9/9
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
                return JSONUtil.getJSONString(0);
            }
        }catch (Exception e){
            logger.error("增加问题失败" + e.getMessage());
        }
        return JSONUtil.getJSONString(1, "失败");
    }


    @RequestMapping(value = "/question/{qid}")
    public String questionDetail(Model model, @PathVariable("qid") int qid){
        Question question = questionService.selectByid(qid);
        model.addAttribute("question", question);
        model.addAttribute("user", userService.getUser(question.getUserId()));
        return "detail";
    }



}
