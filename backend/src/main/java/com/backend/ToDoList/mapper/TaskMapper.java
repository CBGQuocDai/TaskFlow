package com.backend.ToDoList.mapper;

import com.backend.ToDoList.dto.request.TaskRequest;
import com.backend.ToDoList.dto.response.TaskResponse;
import com.backend.ToDoList.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .completed(task.isCompleted())
                .build();
    }
    public Task toTask(TaskRequest taskRequest) {
        return Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .dueDate(taskRequest.getDueDate())
                .build();
    }
}
