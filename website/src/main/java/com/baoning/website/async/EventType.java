package com.baoning.website.async;

import org.omg.CORBA.UNKNOWN;

/**
 * created by baoning on 18/04/10
 */
public enum EventType {

    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5),
    AND_QUESTION(6);


    private int value;
    EventType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }


}
