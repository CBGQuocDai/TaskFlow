package com.backend.ToDoList.controller;

import com.backend.ToDoList.ToDoListApplication;
import com.backend.ToDoList.dto.response.TaskResponse;
import com.backend.ToDoList.entity.User;
import com.backend.ToDoList.service.impl.TaskServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {ToDoListApplication.class}
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.yaml")
public class TaskControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskServiceImpl taskServiceImpl;
    private List<TaskResponse> tasks;
    @BeforeEach
    public void setup() {
        tasks = new ArrayList<>();
        for (int i=1; i<=5; i++) {
            TaskResponse task = TaskResponse.builder()
                    .id(i)
                    .title("title" + i)
                    .description("description for title " + i).build();
            tasks.add(task);
        }
    }

//    @Test
//    void testGetAllTasks_Success() throws Exception {
//        int page =1;
//        int limit = 10;
//        String orderBy = "deadline";
//        String direction = "asc";
//        String complete = "true";
//        UserDetails u= User.builder().email("dai@gmail.com").name("dai").build();
//
//        when(taskServiceImpl.getListTasks(page,limit,orderBy,complete,direction)).thenReturn(tasks);
//        mvc.perform(MockMvcRequestBuilders.get("/todos")
//                .param("page", String.valueOf(page))
//                .param("limit", String.valueOf(limit))
//                .with(user(u)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("code")
//                        .value("1000"))
//                .andExpect(MockMvcResultMatchers.jsonPath("message")
//                        .value("success"))
//                .andExpect(MockMvcResultMatchers.jsonPath("data")
//                        .isArray())
//                .andExpect(MockMvcResultMatchers.jsonPath("page").value(page))
//                .andExpect(MockMvcResultMatchers.jsonPath("limit").value(limit))
//                .andExpect(MockMvcResultMatchers.jsonPath("total").value(tasks.size()));
//    }

    @Test
    void testGetAllTasks_Unauthorized() throws Exception {
        int page =1;
        int limit = 10;
        String orderBy = "deadline";
        String direction = "asc";
        String complete = "true";
        UserDetails u= User.builder().email("dai@gmail.com").name("dai").build();

        when(taskServiceImpl.getListTasks(page,limit,orderBy,complete,direction)).thenReturn(tasks);

        mvc.perform(MockMvcRequestBuilders.get("/todos")
                        .param("page", String.valueOf(page))
                        .param("limit", String.valueOf(limit)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("1001"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Unauthorized"));
    }

}
