package com.baoning.website.service;

import com.baoning.website.dao.QuestionDao;
import com.baoning.website.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * created by baoning on 2018/9/8
 */
@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    SensitiveService sensitiveService;

    public int addQuestion(Question question){
        //HTML过滤
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        //敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return questionDao.addQuestion(question) > 0 ? question.getId() : 0 ;
    }

    public Question selectByid(int id){
        return questionDao.selectById(id);
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit){
        return questionDao.selectLatestQuestions(userId, offset, limit);
    }

    public int updateCommentCount(int id , int count){
        return questionDao.updateCommentCount(id, count);
    }




}
