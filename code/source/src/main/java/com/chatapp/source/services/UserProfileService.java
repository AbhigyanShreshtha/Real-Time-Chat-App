package com.chatapp.source.services;

import com.chatapp.source.models.Login;
import com.chatapp.source.databases.mongo.MongoLogin;
import com.chatapp.source.databases.s3.S3Uploader;
import com.chatapp.source.helpers.base64.Base64Helper;
import com.chatapp.source.helpers.hash.PasswordHelper;
import com.chatapp.source.models.UserProfile;
import com.chatapp.source.databases.mongo.MongoProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserProfileService {

    private final Base64Helper base64Helper;
    private final MongoProfile mongoProfile;
    private final S3Uploader s3Uploader;
    private final PasswordHelper passwordHelper;
    private final MongoLogin mongoLogin;

    @Autowired
    public UserProfileService(Base64Helper base64Helper, MongoProfile mongoProfile, S3Uploader s3Uploader, PasswordHelper passwordHelper, MongoLogin mongoLogin) {
        this.base64Helper = base64Helper;
        this.mongoProfile = mongoProfile;
        this.s3Uploader = s3Uploader;
        this.passwordHelper = passwordHelper;
        this.mongoLogin = mongoLogin;
    }

    public UserProfile getUserProfileByEmail(String emailId) {
        Optional<UserProfile> optionalUserProfile = mongoProfile.findByEmailId(emailId);
        return optionalUserProfile.orElse(null);
    }

    public UserProfile createUserProfile(String username, String name, String status, String emailid, String password, MultipartFile profilephoto) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String base64Photo = base64Helper.encodeFileToBase64(profilephoto);
        String hashedPassword = passwordHelper.hashPassword(password);

        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);
        userProfile.setUuid(uuid);
        userProfile.setName(name);
        userProfile.setStatus(status);
        userProfile.setEmailId(emailid);
        userProfile.setHashedPassword(hashedPassword);
        userProfile.setProfilePhoto(base64Photo);

        Login login = new Login();
        login.setUsername(emailid);
        login.setHashedPassword(hashedPassword);
        mongoLogin.save(login);

        try {
            return mongoProfile.save(userProfile);
        } catch (Exception e) {
            System.err.println("Error saving UserProfile to MongoDB: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to save UserProfile", e);
        }
    }

    public UserProfile modifyUserProfile(String name, String username, String status, String emailid, MultipartFile profilephoto) throws IOException {
        Optional<UserProfile> optionalUserProfile = mongoProfile.findByEmailId(emailid);
        if (!optionalUserProfile.isPresent()) {
            throw new RuntimeException("User profile not found");
        }

        UserProfile userProfile = optionalUserProfile.get();
        userProfile.setName(name);
        userProfile.setStatus(status);
        userProfile.setUsername(username);

        if (profilephoto != null && !profilephoto.isEmpty()) {
            String base64Photo = base64Helper.encodeFileToBase64(profilephoto);
            userProfile.setProfilePhoto(base64Photo);
        }

        try {
            return mongoProfile.save(userProfile);
        } catch (Exception e) {
            System.err.println("Error saving UserProfile to MongoDB: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to save UserProfile", e);
        }
    }

    public UserProfile getUserProfile(String email) {
        Optional<UserProfile> optionalUserProfile = mongoProfile.findByEmailId(email);
        if (!optionalUserProfile.isPresent()) {
            throw new RuntimeException("User profile not found");
        }
        return optionalUserProfile.get();
    }

    public UserProfile addContacts(String emailId, List<String> contacts) {
    Optional<UserProfile> optionalUserProfile = mongoProfile.findByEmailId(emailId);
        if (!optionalUserProfile.isPresent()) {
            throw new RuntimeException("User profile not found");
        }

        UserProfile userProfile = optionalUserProfile.get();
        Set<String> existingContactsSet = new HashSet<>(userProfile.getContacts());
        existingContactsSet.addAll(contacts);
        userProfile.setContacts(new ArrayList<>(existingContactsSet));

        return mongoProfile.save(userProfile);
    }

    public UserProfile addGroups(String emailId, List<String> groups) {
        Optional<UserProfile> optionalUserProfile = mongoProfile.findByEmailId(emailId);
        if (!optionalUserProfile.isPresent()) {
            throw new RuntimeException("User profile not found");
        }

        UserProfile userProfile = optionalUserProfile.get();
        Set<String> existingGroupsSet = new HashSet<>(userProfile.getGroups());
        existingGroupsSet.addAll(groups);
        userProfile.setGroups(new ArrayList<>(existingGroupsSet));

        return mongoProfile.save(userProfile);
    }
}