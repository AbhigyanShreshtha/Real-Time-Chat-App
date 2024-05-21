package com.chatapp.source.services;

import com.chatapp.source.databases.s3.S3Uploader;
import com.chatapp.source.helpers.base64.Base64Helper;
import com.chatapp.source.models.UserProfile;
import com.chatapp.source.databases.mongo.MongoProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {

    private final Base64Helper base64Helper;
    private final MongoProfile mongoProfile;
    private final S3Uploader s3Uploader;

    @Autowired
    public UserProfileService(Base64Helper base64Helper, MongoProfile mongoProfile, S3Uploader s3Uploader) {
        this.base64Helper = base64Helper;
        this.mongoProfile = mongoProfile;
        this.s3Uploader = s3Uploader;
    }

    public UserProfile createUserProfile(String username, String name, String status, String emailid, MultipartFile profilephoto) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String base64Photo = base64Helper.encodeFileToBase64(profilephoto);

        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);
        userProfile.setUuid(uuid);
        userProfile.setName(name);
        userProfile.setStatus(status);
        userProfile.setEmailId(emailid);
        userProfile.setProfilePhoto(base64Photo);
        //testing S3 Upload
        String filelocation = s3Uploader.uploadFile(profilephoto, "pace-jobs/daily-reports/test/filelocation.jpg");
        System.out.println("------File Saved to this:" + filelocation);

        System.out.println("Upload Details:" + userProfile);

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
}