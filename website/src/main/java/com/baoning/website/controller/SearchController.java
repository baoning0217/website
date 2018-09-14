package com.baoning.website.controller;

import com.baoning.website.model.EntityType;
import com.baoning.website.model.Question;
import com.baoning.website.model.ViewObject;
import com.baoning.website.service.FollowService;
import com.baoning.website.service.QuestionService;
import com.baoning.website.service.SearchService;
import com.baoning.website.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * created by baoning on 18/04/13
 */
@Controller
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    SearchService searchService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;


    @RequestMapping(path = {"/search"}, method = {RequestMethod.GET})
    public String search(@RequestParam("q") String keyword, Model model,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count ){

        try{
            List<Question> questionList = searchService.searchQuestion(keyword, offset, count, "<em>", "</em>");
            List<ViewObject> vos = new ArrayList<>();
            for(Question question : questionList){
                Question q = questionService.selectByid(question.getId());
                ViewObject vo = new ViewObject();
                if(question.getContent() != null ){
                    q.setContent(question.getContent());
                }
                if(question.getTitle() != null ){
                    q.setTitle(question.getTitle());
                }
                vo.set("question", q);
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                vo.set("user", userService.getUser(q.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
            model.addAttribute("keyword", keyword);
        }catch (Exception e){
            logger.error("搜索评论失败:" + e.getMessage() );
        }
        return "result";


    }



}
