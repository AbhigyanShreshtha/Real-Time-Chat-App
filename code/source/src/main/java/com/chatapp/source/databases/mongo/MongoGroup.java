package com.chatapp.source.databases.mongo;

import com.chatapp.source.models.Groups;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoGroup extends MongoRepository<Groups, String> {
    Optional<Groups> findByGroupId(String groupId);
}