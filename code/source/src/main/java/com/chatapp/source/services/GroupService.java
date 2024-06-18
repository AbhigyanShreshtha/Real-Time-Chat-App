package com.chatapp.source.services;

import com.chatapp.source.models.Groups;
import com.chatapp.source.models.UserProfile;
import com.chatapp.source.databases.mongo.MongoGroup;
import com.chatapp.source.databases.mongo.MongoProfile;
import com.chatapp.source.helpers.base64.Base64Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final MongoGroup groupRepository;
    private final MongoProfile userProfileRepository;
    private final Base64Helper base64Helper;

    @Autowired
    public GroupService(MongoGroup groupRepository, MongoProfile userProfileRepository, Base64Helper base64Helper) {
        this.groupRepository = groupRepository;
        this.userProfileRepository = userProfileRepository;
        this.base64Helper = base64Helper;
    }

    public Groups createGroup(MultipartFile groupPhoto, String about, String uuid) throws IOException {
        Groups group = new Groups();
        group.setGroupId(UUID.randomUUID().toString());
        group.setGroupPhoto(base64Helper.encodeFileToBase64(groupPhoto));
        group.setAbout(about);
        group.setParticipants(Collections.singletonList(uuid));
        group.setAdmins(Collections.singletonList(uuid));

        Groups savedGroup = groupRepository.save(group);

        // Update user profile with the new group
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUuid(uuid);
        if (userProfileOptional.isPresent()) {
            UserProfile userProfile = userProfileOptional.get();
            List<String> userGroups = userProfile.getGroups();
            userGroups.add(savedGroup.getGroupId());
            userProfile.setGroups(userGroups);
            userProfileRepository.save(userProfile);
        }

        return savedGroup;
    }

    public Groups updateGroup(String groupId, MultipartFile groupPhoto, String about, List<String> newParticipants, List<String> newAdmins, String uuid) throws IOException {
        Optional<Groups> optionalGroup = groupRepository.findByGroupId(groupId);
        if (!optionalGroup.isPresent()) {
            throw new RuntimeException("Group not found.");
        }
        Groups group = optionalGroup.get();
        if (!group.getAdmins().contains(uuid)) {
            throw new RuntimeException("Only admins can update groups.");
        }

        group.setGroupPhoto(base64Helper.encodeFileToBase64(groupPhoto));
        group.setAbout(about);

        // Add new participants and admins
        List<String> participantUuids = newParticipants.stream()
                .map(email -> userProfileRepository.findByEmailId(email).map(UserProfile::getUuid).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        group.getParticipants().addAll(participantUuids);

        List<String> adminUuids = newAdmins.stream()
                .map(email -> userProfileRepository.findByEmailId(email).map(UserProfile::getUuid).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        group.getAdmins().addAll(adminUuids);

        Groups updatedGroup = groupRepository.save(group);

        // Update user profiles with the group
        updateProfilesWithGroup(participantUuids, groupId);

        return updatedGroup;
    }

    public Groups fetchGroup(String groupId, String uuid) {
        Optional<Groups> optionalGroup = groupRepository.findByGroupId(groupId);
        if (!optionalGroup.isPresent()) {
            throw new RuntimeException("Group not found.");
        }
        Groups group = optionalGroup.get();
        if (!group.getParticipants().contains(uuid)) {
            throw new RuntimeException("Only participants can fetch group details.");
        }
        return group;
    }

    public void deleteGroup(String groupId, String uuid) {
        Optional<Groups> optionalGroup = groupRepository.findByGroupId(groupId);
        if (!optionalGroup.isPresent()) {
            throw new RuntimeException("Group not found.");
        }
        Groups group = optionalGroup.get();
        if (!group.getAdmins().contains(uuid)) {
            throw new RuntimeException("Only admins can delete groups.");
        }
        // Remove group from all user profiles
        List<String> participants = group.getParticipants();
        participants.forEach(participantUuid -> {
            Optional<UserProfile> userProfileOptional = userProfileRepository.findByUuid(participantUuid);
            userProfileOptional.ifPresent(userProfile -> {
                userProfile.getGroups().remove(groupId);
                userProfileRepository.save(userProfile);
            });
        });
        groupRepository.delete(group);
    }

    public List<Groups> getUserGroups(String emailId) {
        Optional<UserProfile> optionalUserProfile = userProfileRepository.findByEmailId(emailId);
        if (!optionalUserProfile.isPresent()) {
            throw new RuntimeException("User profile not found");
        }
        UserProfile userProfile = optionalUserProfile.get();
        List<String> groupIds = userProfile.getGroups();
        return groupRepository.findAllById(groupIds);
    }

    private void updateProfilesWithGroup(List<String> uuids, String groupId) {
        uuids.forEach(uuid -> {
            Optional<UserProfile> userProfileOptional = userProfileRepository.findByUuid(uuid);
            userProfileOptional.ifPresent(userProfile -> {
                userProfile.getGroups().add(groupId);
                userProfileRepository.save(userProfile);
            });
        });
    }
}
