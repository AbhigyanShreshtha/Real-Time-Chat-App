package com.chatapp.source.controllers;

import com.chatapp.source.models.UserProfile;
import com.chatapp.source.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    public static class CreateUserProfileReq {
        public String name;
        public String username;
        public String status;
        public String emailid;
    }

    @PostMapping("/create")
    public UserProfile createUserProfile(
            @RequestPart("name") String name,
            @RequestPart("username") String username,
            @RequestPart("status") String status,
            @RequestPart("emailid") String emailid,
            @RequestPart("password") String password,
            @RequestPart(value = "profilephoto", required = false) MultipartFile profilephoto) throws IOException {
        return userProfileService.createUserProfile(username, name, status, emailid, password, profilephoto);
    }

    @PutMapping("/modify/{emailid}")
    public UserProfile modifyUserProfile(
            @PathVariable String emailid,
            @RequestPart("name") String name,
            @RequestPart("username") String username,
            @RequestPart("status") String status,
            @RequestPart(value = "profilephoto", required = false) MultipartFile profilephoto) throws IOException  {
        return userProfileService.modifyUserProfile(name, username, status, emailid, profilephoto);
    }

    public class GetUserProfileReq {
        public String emailid;
    }

    @GetMapping("/get/{emailid}")
    public UserProfile getUserProfile(@PathVariable String emailid) {
        return userProfileService.getUserProfile(emailid);
    }
}
