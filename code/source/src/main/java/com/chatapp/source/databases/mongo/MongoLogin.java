package com.chatapp.source.databases.mongo;

import com.chatapp.source.models.Login;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MongoLogin extends MongoRepository<Login, String> {
    Optional<Login> findByUsername(String username);
}