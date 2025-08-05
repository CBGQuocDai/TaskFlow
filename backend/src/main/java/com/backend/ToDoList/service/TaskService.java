package com.backend.ToDoList.service;

import com.backend.ToDoList.dto.request.TaskRequest;
import com.backend.ToDoList.dto.response.StatisticResponse;
import com.backend.ToDoList.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest req);

    TaskResponse updateTask(int id, TaskRequest req);

    TaskResponse changeTaskCompleted(int id);

    void deleteTask(int id);

    List<TaskResponse> getListTasks(
            int page, int limit, String orderBy, String complete, String sortOrder);

    int getTotalTasks(String orderBy, String complete, String sortOrder);

    StatisticResponse getStatistic();
}
