package com.chatapp.source.controllers;

import com.chatapp.source.models.Groups;
import com.chatapp.source.models.UserProfile;
import com.chatapp.source.services.GroupService;
import com.chatapp.source.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;
    private final UserProfileService userProfileService;

    @Autowired
    public GroupController(GroupService groupService, UserProfileService userProfileService) {
        this.groupService = groupService;
        this.userProfileService = userProfileService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(
            @RequestPart("emailId") String emailId,
            @RequestPart("groupPhoto") MultipartFile groupPhoto,
            @RequestPart("about") String about) throws IOException {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(emailId);
        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User profile not found");
        }
        String uuid = userProfile.getUuid();
        Groups group = groupService.createGroup(groupPhoto, about, uuid);
        return ResponseEntity.ok(group);
    }

    @PutMapping("/update/{groupId}")
    public ResponseEntity<?> updateGroup(
            @PathVariable String groupId,
            @RequestPart("emailId") String emailId,
            @RequestPart("groupPhoto") MultipartFile groupPhoto,
            @RequestPart("about") String about,
            @RequestPart("newParticipants") List<String> newParticipants,
            @RequestPart("newAdmins") List<String> newAdmins) throws IOException {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(emailId);
        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User profile not found");
        }
        String uuid = userProfile.getUuid();
        Groups group = groupService.updateGroup(groupId, groupPhoto, about, newParticipants, newAdmins, uuid);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/fetch/{groupId}/{emailId}")
    public ResponseEntity<?> fetchGroup(@PathVariable String groupId, @PathVariable String emailId) {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(emailId);
        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User profile not found");
        }
        String uuid = userProfile.getUuid();
        Groups group = groupService.fetchGroup(groupId, uuid);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable String groupId, @RequestPart("emailId") String emailId) {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(emailId);
        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User profile not found");
        }
        String uuid = userProfile.getUuid();
        groupService.deleteGroup(groupId, uuid);
        return ResponseEntity.ok("Group deleted successfully");
    }

    @GetMapping("/user-groups/{emailId}")
    public ResponseEntity<?> getUserGroups(@PathVariable String emailId) {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(emailId);
        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User profile not found");
        }
        List<Groups> groups = groupService.getUserGroups(emailId);
        return ResponseEntity.ok(groups);
    }
}
