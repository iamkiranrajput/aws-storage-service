package com.guardians.udss.controller;

import com.guardians.udss.services.S3FileService;
import com.guardians.udss.services.S3FileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/files")
public class FileController {

    private final S3FileService s3FileService;

    @Autowired
    public FileController(S3FileServiceImpl s3FileService) {
        this.s3FileService = s3FileService;
    }

    @Operation(summary = "Search files for a user in the S3 bucket")
    @GetMapping("/search")
    public ResponseEntity<List<String>> searchFiles(
            @RequestParam String userName,
            @RequestParam String searchTerm) {
        try {
            List<String> files = s3FileService.searchFiles(userName, searchTerm);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("Error searching files for user {}: {}", userName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Download a specific file from S3 for a user")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam String userName,
            @RequestParam String fileName) {
        String key = userName + "/" + fileName;

        try {
            InputStream inputStream = s3FileService.downloadFile(key);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            log.error("Error downloading file for user {}: {}", userName, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Upload a file to the S3 bucket for a user")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam String userName,
            @RequestParam MultipartFile file) {
        try {
            String fileKey = s3FileService.uploadFile(userName, file);
            log.info("File uploaded successfully");
            return ResponseEntity.ok("File uploaded successfully: " + fileKey);
        } catch (Exception e) {
            log.error("Error uploading file for user {}: {}", userName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    @Operation(summary = "Delete a specific file from S3 for a user")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String userName, @RequestParam String fileName) {
        try {
            s3FileService.deleteFile(userName, fileName); // Call delete from service layer
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting file for user {}: {}", userName, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting file");
        }
    }

}
