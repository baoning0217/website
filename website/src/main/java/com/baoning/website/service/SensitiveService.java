package com.baoning.website.service;

import com.baoning.website.controller.HomeController;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * created by baoning on 2018/4/9
 */
@Service
public class SensitiveService implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    public void afterPropertiesSet() throws Exception {
        try{
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while((lineTxt = bufferedReader.readLine()) != null ){
                addWord(lineTxt.trim());
            }
            read.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    //增加关键词
    private void addWord(String lineTxt){
        TrieNode tempNode = rootNode;
        for(int i =0 ; i < lineTxt.length(); i++ ){
            Character c = lineTxt.charAt(i);
            if(isSymbol(c)){
                continue;
            }

            TrieNode node = tempNode.getSubNode(c);
            if(node == null ){
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }
            tempNode = node;
            if(i == lineTxt.length() - 1 ){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    private class TrieNode{

        //是不是关键词的结尾
        private boolean end = false;

        //当前节点下所有的子节点
        private Map<Character, TrieNode> subNodes = new HashMap<Character, TrieNode>();

        public void addSubNode(Character key, TrieNode node){
            subNodes.put(key, node);
        }

        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeywordEnd() {
            return end;
        }

        void setKeywordEnd(boolean end){
            this.end = end;
        }

    }

    private TrieNode rootNode = new TrieNode();

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }
        StringBuilder result = new StringBuilder();
        String replacement = "***";
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while(position < text.length()){
            char c = text.charAt(position);

            if(isSymbol(c)){
                if(tempNode == rootNode){
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            if(tempNode == null ){
                result.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                //发现敏感词
                result.append(replacement);
                position = position + 1 ;
                begin = position;
                tempNode = rootNode;
            }else {
                ++position;
            }
        }
        result.append(text.substring(begin));
        return result.toString();
    }

    //非法词
    private boolean isSymbol(char c){
        int ic = (int) c;
        //东亚文字 0x2E80-ox9FFF
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }


}
