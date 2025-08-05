package com.backend.ToDoList.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
public class TaskResponse {
    private int id;
    private String title;
    private String description;
    private Date dueDate;
    private boolean completed;
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting TaskResponse to JSON: " + e.getMessage());
            return "{"
                    + "\"id\": " + id + ","
                    + "\"title\": \"" + title + "\","
                    + "\"description\": \"" + description + "\""
                    + "} (Error converting to JSON)";
        }
    }
}
