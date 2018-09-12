package com.baoning.website.service;

import com.baoning.website.dao.FeedDao;
import com.baoning.website.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by baoning on 18/09/12
 */
@Service
public class FeedService {

    @Autowired
    FeedDao feedDao;


    public boolean addFeed(Feed feed){
        feedDao.addFeed(feed);
        return feed.getId() > 0;
    }

    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count){
        return feedDao.selectUserFeeds(maxId, userIds, count);
    }

    public Feed getById(int id){
        return feedDao.getFeedById(id);
    }


}
