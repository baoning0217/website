package com.baoning.website.async;

import java.util.List;

/**
 * created by baoning on 18/09/10
 */
public interface EventHandler {

    void doHandler(EventModel model);

    List<EventType> getSupportEventTypes();

}
