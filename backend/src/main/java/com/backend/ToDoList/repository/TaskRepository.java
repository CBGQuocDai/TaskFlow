package com.backend.ToDoList.repository;

import com.backend.ToDoList.dto.response.StatisticResponse;
import com.backend.ToDoList.entity.Task;
import com.backend.ToDoList.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByUserAndCompletedAndDeleted(Pageable pageable, User user, boolean b, boolean b1);

    List<Task> findAllByUserAndDeleted(Pageable pageable, User user, boolean b);

    @Query("SELECT new com.backend.ToDoList.dto.response.StatisticResponse(" +
            "    COUNT(t) AS totalTasks, " +
            "    COUNT(CASE WHEN t.completed = TRUE  THEN 1L END) AS completedTasks, " +
            "    COUNT(CASE WHEN t.completed = FALSE AND t.dueDate < :currentDate THEN 1L END) AS overdueTasks, "+
            "    COUNT(CASE WHEN t.completed = FALSE AND t.dueDate >= :currentDate THEN 1L END) AS inProgressTasks, " +
            "    COUNT(CASE WHEN t.completed = FALSE AND t.dueDate = :currentDate THEN 1L END) AS dueSoonToday, " +
            "    COUNT(CASE WHEN t.completed = FALSE AND t.dueDate > :currentDate AND t.dueDate <= :sevenDaysLater THEN 1L END) AS dueSoonThisWeek, " +
            "    COUNT(CASE WHEN t.completed = FALSE AND t.dueDate > :sevenDaysLater AND t.dueDate <= :fourteenDaysLater THEN 1L END) AS dueSoonNextWeek, " +
            "    COUNT(CASE WHEN t.completed = FALSE AND t.dueDate > :fourteenDaysLater THEN 1L END) AS dueSoonAfterTwoWeeks " +
            ") FROM Task t \n" +
            "WHERE t.user.id = :user_id")
    StatisticResponse getStatisticResponse(@Param("user_id") int id,
                                           @Param("currentDate") Date currentDate,
                                           @Param("sevenDaysLater") Date sevenDaysLater,
                                           @Param("fourteenDaysLater") Date fourteenDaysLater);

    @Query("SELECT COUNT(t.id) FROM Task t " +
            "WHERE t.user.id = :id AND t.deleted = false")
    int getTotalTasksByUser(@Param("id")int  id);
    @Query("SELECT COUNT(t.id) FROM Task t " +
            "WHERE t.user.id = :id AND t.deleted = false AND t.completed = :completed")
    int getTotalTasksByUserAndCompleted(@Param("id") int id, @Param("completed") boolean b);

    @Query("SELECT t FROM Task t" +
            " WHERE t.user.id = :id AND t.deleted = false AND t.dueDate< :current and t.completed = false")
    List<Task> getAllTaskOverDue(@Param("id") int id, @Param("current") Date current);
    @Query("SELECT COUNT(t.id) FROM Task t" +
            " WHERE t.user.id = :id AND t.deleted = false AND t.dueDate< :current AND t.completed = false ")
    int getNumsOfTaskOverDue(@Param("id") int id, @Param("current") Date current);
}
