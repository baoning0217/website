package com.baoning.website.service;

import com.baoning.website.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * created by baoning on 18/04/13
 */
@Service
public class SearchService {

    private static final String SOLR_URL = "http://192.168.65.207:8080/solr/collection1";
    private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();

    private static  final String QUESTION_TITLE_FIELD = "question_title";
    private static  final String QUESTION_CONTENT_FIELD = "question_content";

    //查询
    public List<Question> searchQuestion(String keyword, int offset, int count,
                                         String hlPre, String hlPos) throws Exception{
        List<Question> questionList = new ArrayList<>();
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(count);
        query.setStart(offset);
        query.setHighlight(true);//设置高亮
        query.setHighlightSimplePre(hlPre);//设置前缀
        query.setHighlightSimplePost(hlPos);//设置后缀
        query.set("hl.fl", QUESTION_TITLE_FIELD + "," + QUESTION_CONTENT_FIELD );
        QueryResponse response = client.query(query);

        for (Map.Entry<String, Map<String, List<String>>> entry : response.getHighlighting().entrySet()){
            Question q = new Question();
            q.setId(Integer.parseInt(entry.getKey()));
            if(entry.getValue().containsKey(QUESTION_TITLE_FIELD)){
                List<String> titleList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if(titleList.size() > 0 ){
                    q.setContent(titleList.get(0));
                }
            }
            if(entry.getValue().containsKey(QUESTION_CONTENT_FIELD)){
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if(contentList.size() > 0 ){
                    q.setContent(contentList.get(0));
                }
            }
            questionList.add(q);
        }
        return questionList;
    }


    //添加索引
    public boolean indexQuestion(int qid, String title, String content ) throws Exception {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", qid);
        doc.setField(QUESTION_TITLE_FIELD, title);
        doc.setField(QUESTION_CONTENT_FIELD, content);
        UpdateResponse response = client.add(doc, 2000);
        return response != null && response.getStatus() == 0;
    }




}
