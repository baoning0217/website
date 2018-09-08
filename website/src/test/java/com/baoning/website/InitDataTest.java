package com.baoning.website;

import com.baoning.website.dao.QuestionDao;
import com.baoning.website.dao.UserDao;
import com.baoning.website.model.Question;
import com.baoning.website.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * created by baoning on 2018/9/8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebsiteApplication.class)
@Sql("/init-schema.sql")
public class InitDataTest {

    @Autowired
    UserDao userDao;

    @Autowired
    QuestionDao questionDao;

    @Test
    public void initData(){

        Random random = new Random();

        for(int i =0; i< 5; ++i){
            User user = new User();
            user.setHeadUrl(String.format("http://114.255.95.20/oa/images/test.jpg"));
            user.setName(String.format("User%d",i));
            user.setPassword("");
            user.setSalt("");
            userDao.addUser(user);

            user.setPassword("123456");
            userDao.updatePassword(user);

            Question question = new Question();
            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000*3600*i);
            question.setCreatedDate(date);
            question.setUserId(i+1);
            question.setTitle(String.format("Title{%d}",i));
            question.setContent(String.format("hello{%d}",i));

            questionDao.addQuestion(question);

        }

        Assert.assertEquals("123456", userDao.selectById(1).getPassword());
        userDao.deleteById(1);
        Assert.assertNull(userDao.selectById(1));

        List<Question> questions = questionDao.selectLatestQuestions(0, 0, 10);
        System.out.println(questions);

    }


}
