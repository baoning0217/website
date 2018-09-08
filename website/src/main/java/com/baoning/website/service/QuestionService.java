package com.baoning.website.service;

import com.baoning.website.dao.QuestionDao;
import com.baoning.website.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by baoning on 2018/9/8
 */
@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    public List<Question> getLatestQuestions(int userId, int offset, int limit){
        return questionDao.selectLatestQuestions(userId, offset, limit);
    }


}
