package com.example.demo.repository;

import com.example.demo.model.RefreshToken;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    @Query("SELECT * FROM refresh_tokens WHERE token = :token")
    RefreshToken findByToken(String token);
}
