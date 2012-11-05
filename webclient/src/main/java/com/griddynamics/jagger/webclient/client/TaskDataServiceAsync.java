package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public interface TaskDataServiceAsync {
    void getTaskDataForSessions(Set<String> sessionIds, AsyncCallback<List<TaskDataDto>> callback);

    void getTaskDataForSession(String sessionId, AsyncCallback<List<TaskDataDto>> callback);

    void getTaskDetails(Set<Long> taskIds, AsyncCallback<Map<String, TaskDetails>> callback);
}
