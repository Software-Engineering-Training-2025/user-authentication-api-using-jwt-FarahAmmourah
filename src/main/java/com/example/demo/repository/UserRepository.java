package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query("SELECT * FROM users WHERE email = :email")
    User findByEmail(String email);
}
