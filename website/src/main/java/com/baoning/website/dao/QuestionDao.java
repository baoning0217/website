package com.baoning.website.dao;

import com.baoning.website.model.Question;
import com.baoning.website.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * created by baoning on 2018/9/8
 */
@Mapper
public interface QuestionDao {

    String TABLE_NAME= " question ";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({" insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values ( #{title}, #{content}, #{createdDate}, #{userId}, #{commentCount} )"})
    int addQuestion(Question question);

    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);





}
