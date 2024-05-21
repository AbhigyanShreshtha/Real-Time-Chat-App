package com.chatapp.source.databases.mongo;

import com.chatapp.source.models.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MongoProfile extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByUsername(String username);
    Optional<UserProfile> findByUuid(String uuid);
    Optional<UserProfile> findByEmailId(String emailid);
}