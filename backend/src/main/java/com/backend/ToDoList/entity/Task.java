package com.backend.ToDoList.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private boolean completed = false;
    private Date dueDate = new Date();
    private boolean deleted =false;
    @ManyToOne
    @JoinColumn (name = "user_id")
    private User user;
}

