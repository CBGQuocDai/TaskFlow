package com.backend.ToDoList.service.impl;

import com.backend.ToDoList.dto.request.TaskRequest;
import com.backend.ToDoList.dto.response.StatisticResponse;
import com.backend.ToDoList.dto.response.TaskResponse;
import com.backend.ToDoList.entity.Task;
import com.backend.ToDoList.entity.User;
import com.backend.ToDoList.errors.AppException;
import com.backend.ToDoList.enums.ErrorCode;
import com.backend.ToDoList.mapper.TaskMapper;
import com.backend.ToDoList.repository.TaskRepository;
import com.backend.ToDoList.service.TaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest req) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        User user = (User) auth.getPrincipal();
        Task task = taskMapper.toTask(req);
        task.setUser(user);
        task.setCompleted(false);
        return taskMapper.toTaskResponse(taskRepository.save(task));
    }
    @Override
    @Transactional
    public TaskResponse updateTask(int id, TaskRequest req) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        User user = (User) auth.getPrincipal();
        Task task = taskRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.TASK_NOT_EXIST));
        if(task.getUser().getId() == user.getId()) {
            task.setTitle(req.getTitle());
            task.setDescription(req.getDescription());
            task.setDueDate(req.getDueDate());
            return taskMapper.toTaskResponse(taskRepository.save(task));
        }
        else {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }
    @Override
    @Transactional
    public TaskResponse changeTaskCompleted(int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        Task t = taskRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.TASK_NOT_EXIST));
        if(t.getUser().getId() == user.getId()) {
            t.setCompleted(!t.isCompleted());
            return taskMapper.toTaskResponse(taskRepository.save(t));
        }
        else {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }
    @Override
    @Transactional
    public void deleteTask(int id) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        User user = (User) auth.getPrincipal();
        Task task = taskRepository.findById(id).orElse(null);
        if(task==null) return;
        if(task.getUser().getId() == user.getId()) {
            task.setDeleted(true);
            taskRepository.save(task);
        }
        else {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }
    @Override
    @Transactional
    public List<TaskResponse> getListTasks(
            int page, int limit, String orderBy, String complete, String sortOrder) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        User user = (User) auth.getPrincipal();

        Sort.Direction direction =  Sort.Direction.DESC;
        String by = "id";

        if(sortOrder.equals("asc")) {
            direction = Sort.Direction.ASC;
        }
        if(orderBy.equals("deadline")) {
            by = "dueDate";
        }
        Sort sort = Sort.by(direction, by);
        Pageable pageable = PageRequest.of(page-1, limit,sort);
        List<Task> tasks = null;
        if(complete.equals("done")) {
            tasks = taskRepository.findAllByUserAndCompletedAndDeleted(pageable,user, true,false);
        }
        else {
            if(complete.equals("process")) {
                tasks = taskRepository.findAllByUserAndCompletedAndDeleted(pageable,user, false,false);
            } else {
                if(complete.equals("over")) {
                    Instant now = Instant.now();
                    ZonedDateTime zoneTime = now.atZone(ZoneId.systemDefault()).toLocalDate()
                            .atStartOfDay(ZoneId.systemDefault());
                    Date today = new Date(zoneTime.toInstant().toEpochMilli());
                    tasks = taskRepository.getAllTaskOverDue(user.getId(), today);
                }else {
                    tasks = taskRepository.findAllByUserAndDeleted(pageable,user,false);
                }
            }
        }
        return tasks.stream().map(taskMapper::toTaskResponse).toList();
    }
    @Override
    @Transactional
    public int getTotalTasks(String orderBy, String complete, String sortOrder) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        int total = 0;
        if(complete.equals("done"))
        {
            total = taskRepository.getTotalTasksByUserAndCompleted(user.getId(),true);
        }else {
            if(complete.equals("process")){
                total = taskRepository.getTotalTasksByUserAndCompleted(user.getId(),false);
            }
            else {
                if (complete.equals("over")) {
                    Instant now = Instant.now();
                    ZonedDateTime zoneTime = now.atZone(ZoneId.systemDefault()).toLocalDate()
                            .atStartOfDay(ZoneId.systemDefault());
                    Date today = new Date(zoneTime.toInstant().toEpochMilli());

                    total = taskRepository.getNumsOfTaskOverDue(user.getId(),today);
                }else {
                    total = taskRepository.getTotalTasksByUser(user.getId());
                }
            }
        }
        return total;
    }
    @Override
    @Transactional
    public StatisticResponse getStatistic() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        User user = (User) auth.getPrincipal();
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedDateTime = now.atZone(zoneId);
        ZonedDateTime midnight = zonedDateTime.toLocalDate().atStartOfDay(zoneId);
        long midnightTomorrowEpochMilli = midnight.toInstant().toEpochMilli();
        Date deadline = new Date(midnightTomorrowEpochMilli);
        return taskRepository.getStatisticResponse(user.getId(),
                deadline,
                new Date(midnight.toInstant().plus(7,ChronoUnit.DAYS).toEpochMilli()),
                new Date(midnight.toInstant().plus(14,ChronoUnit.DAYS).toEpochMilli())
                );
    }
}
