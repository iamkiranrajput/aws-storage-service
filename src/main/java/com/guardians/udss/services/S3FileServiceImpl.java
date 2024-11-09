package com.guardians.udss.services;

import com.guardians.udss.config.S3Config;
import com.guardians.udss.exception.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class S3FileServiceImpl implements S3FileService{


    private final S3Config s3Config;
    private final S3Client s3Client;

    @Autowired
    public S3FileServiceImpl(S3Config s3Config) {
        this.s3Config = s3Config;
        this.s3Client = S3Client.builder()
                .region(Region.of(s3Config.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3Config.getAccessKey(), s3Config.getSecretKey())))
                .build();
    }

    public List<String> searchFiles(String username, String searchTerm) {
        String prefix = username + "/";
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(s3Config.getBucketName())
                .prefix(prefix)
                .build();

        try {
            ListObjectsV2Response result = s3Client.listObjectsV2(request);

            return result.contents().stream()
                    .map(S3Object::key)
                    .filter(key -> key.contains(searchTerm))
                    .collect(Collectors.toList());
        } catch (S3Exception e) {
            log.error("Error searching files in S3 for user {} with term {}: {}", username, searchTerm, e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error searching files in S3 bucket");
        }
    }

    public InputStream downloadFile(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(key)
                .build();

        try {
            return s3Client.getObject(getObjectRequest);
        } catch (NoSuchKeyException e) {
            log.error("File not found in S3: {}", key);
            throw new FileNotFoundException("File not found in S3 bucket for key: " + key);
        } catch (S3Exception e) {
            log.error("Error downloading file from S3 with key {}: {}", key, e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error downloading file from S3 bucket");
        }
    }

    public String uploadFile(String username, MultipartFile file) {
        String key = username + "/" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));
            return key;
        } catch (IOException e) {
            log.error("Error reading file input stream: {}", e.getMessage());
            throw new RuntimeException("Error reading file input stream");
        } catch (S3Exception e) {
            log.error("Error uploading file to S3 bucket with key {}: {}", key, e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error uploading file to S3 bucket");
        }
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(key)
                .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
            log.info("File with key {} deleted successfully from S3 bucket.", key);
        } catch (NoSuchKeyException e) {
            log.error("File not found in S3 for deletion: {}", key);
            throw new FileNotFoundException("File not found in S3 bucket for key: " + key);
        } catch (S3Exception e) {
            log.error("Error deleting file from S3 with key {}: {}", key, e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error deleting file from S3 bucket");
        }
    }

    public void deleteFile(String username, String filename) {
        String key = username + "/" + filename;

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted file with key: {}", key);
        } catch (S3Exception e) {
            log.error("Error deleting file from S3 with key {}: {}", key, e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error deleting file from S3 bucket");
        }
    }

}
