package com.baoning.website.dao;

import com.baoning.website.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * created by baoning on 18/04/12
 */
@Mapper
public interface FeedDao {

    String TABLE_NAME= " feed ";
    String INSERT_FIELDS = " type, user_id, created_date, data ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    @Insert({" insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values ( #{type}, #{userId}, #{createdDate}, #{data}) "})
    int addFeed(Feed feed);

    @Select({ " select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id} "})
    Feed getFeedById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId, @Param("userIds") List<Integer> userIds, @Param("count") int count);






}
