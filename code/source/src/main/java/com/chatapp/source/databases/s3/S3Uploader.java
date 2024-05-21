package com.chatapp.source.databases.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${aws_s3_config.bucket_name}")
    private String bucketName;

    public S3Uploader(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file, String saveLocation) {
        String fileUrl = "";

        try {
            // Convert MultipartFile to ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.wrap(file.getBytes());

            // Save the file locally before uploading to S3 (optional)
            Path path = Paths.get(saveLocation);
            Files.createDirectories(path.getParent());
            Files.write(path, byteBuffer.array());

            // Upload file to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(saveLocation)
                    .acl("public-read")
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, path);

            if (response.sdkHttpResponse().isSuccessful()) {
                fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(saveLocation)).toExternalForm();
            }

        } catch (IOException | S3Exception e) {
            e.printStackTrace();
        }

        return fileUrl;
    }
}

