package com.baoning.website.async.handler;

import com.baoning.website.async.EventHandler;
import com.baoning.website.async.EventModel;
import com.baoning.website.async.EventType;
import com.baoning.website.controller.CommentController;
import com.baoning.website.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * created by baoning on 18/04/14
 */
@Component
public class AddQuestionHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);

    @Autowired
    SearchService searchService;


    public void doHandler(EventModel model) {

        try{
            searchService.indexQuestion(model.getEntityId(), model.getExt("title"), model.getExt("content"));
        }catch (Exception e){
            logger.error("增加题目索引失败:" + e.getMessage());
        }
    }


    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.AND_QUESTION);
    }

}
