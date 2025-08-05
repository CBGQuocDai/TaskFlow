package com.backend.ToDoList.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StatisticResponse {
    private  long totalTasks;
    private long completedTasks;
    private long overdueTasks;
    private long inProgressTasks;
    private long dueSoonToday;
    private long dueSoonThisWeek;
    private long dueSoonNextWeek;
    private long dueSoonAfterTwoWeeks;
}
