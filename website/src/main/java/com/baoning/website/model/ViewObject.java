package com.baoning.website.model;

import java.util.HashMap;
import java.util.Map;

/**
 * created by baoning on 2018/9/8
 */
public class ViewObject {

    private Map<String, Object> objs = new HashMap<String, Object>();

    public void set(String key, Object value){
        objs.put(key, value);
    }
    public Object get(String key){
        return objs.get(key);
    }


}
