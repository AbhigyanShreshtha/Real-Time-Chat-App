package com.chatapp.source.controllers;

import com.chatapp.source.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    public static class SignupRequest {
        public String username;
        public String password;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest req) {
        loginService.signup(req.username, req.password);
        return "User signed up successfully";
    }

    public static class ChangePasswordRequest {
        public String username;
        public String currentPassword;
        public String newPassword;
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequest req) {
        loginService.changePassword(req.username, req.currentPassword, req.newPassword);
        return "Password changed successfully";
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) {
        return loginService.login(req.username, req.password);
    }

    public static class LogoutRequest {
        public String username;
    }

    @PostMapping("/logout")
    public String logout(@RequestBody LogoutRequest req) {
        loginService.logout(req.username);
        return "User logged out successfully";
    }
}