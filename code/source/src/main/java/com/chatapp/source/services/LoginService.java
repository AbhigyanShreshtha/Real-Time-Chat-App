package com.chatapp.source.services;

import com.chatapp.source.helpers.jwt.JwtHelper;
import com.chatapp.source.helpers.hash.PasswordHelper;
import com.chatapp.source.models.Login;
import com.chatapp.source.databases.mongo.MongoLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class LoginService {

    private final MongoLogin mongo;
    private final PasswordHelper passwordHelper;
    private final JwtHelper jwtHelper;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public LoginService(MongoLogin mongo, PasswordHelper passwordHelper, JwtHelper jwtHelper, RedisTemplate<String, String> redisTemplate) {
        this.mongo = mongo;
        this.passwordHelper = passwordHelper;
        this.jwtHelper = jwtHelper;
        this.redisTemplate = redisTemplate;
    }

    public Login signup(String username, String password) {
        if (mongo.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        String hashedPassword = passwordHelper.hashPassword(password);
        Login user = new Login();
        user.setUsername(username);
        user.setHashedPassword(hashedPassword);

        return mongo.save(user);
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        Optional<Login> optionalUser = mongo.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }

        Login user = optionalUser.get();
        if (!passwordHelper.matchPassword(currentPassword, user.getHashedPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setHashedPassword(passwordHelper.hashPassword(newPassword));
        mongo.save(user);
    }

    public String login(String username, String password) {
        Optional<Login> optionalUser = mongo.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }

        Login user = optionalUser.get();
        if (!passwordHelper.matchPassword(password, user.getHashedPassword())) {
            throw new RuntimeException("Password is incorrect");
        }

        String token = jwtHelper.generateToken(username);
        redisTemplate.opsForValue().set(username, token, 24, TimeUnit.HOURS);
        return token;
    }

    public void logout(String username) {
        redisTemplate.delete(username);
    }
}