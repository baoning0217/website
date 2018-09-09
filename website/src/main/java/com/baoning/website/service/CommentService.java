package com.baoning.website.service;

import com.baoning.website.dao.CommentDao;
import com.baoning.website.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * created by baoning on 2018/9/9
 */
@Service
public class CommentService {

    @Autowired
    CommentDao commentDao;

    @Autowired
    SensitiveService sensitiveService;


    public List<Comment> getCommentByEntity(int entityId, int entityType){
        return commentDao.selectCommentByEntity(entityId, entityType);
    }

    public int addComment(Comment comment){
        //评论敏感词过滤以及html标签过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment);
    }

    public int getCommentCount(int entityId, int entityType){
        return commentDao.getCommentCount(entityId, entityType);
    }

    public boolean deleteComment(int commentId){
        return commentDao.updateStatus(commentId, 1) > 0 ;
    }


}
