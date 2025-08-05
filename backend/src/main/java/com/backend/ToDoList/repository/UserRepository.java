package com.backend.ToDoList.repository;

import com.backend.ToDoList.entity.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


    User getByEmail(String email);


    User findByEmailAndActive(String email, boolean b);

    boolean existsByEmail(String email);

    boolean existsByEmailAndActive(String newEmail, boolean b);

    User findByEmail(@Email(message = "EMAIL_INVALID") String email);
}
