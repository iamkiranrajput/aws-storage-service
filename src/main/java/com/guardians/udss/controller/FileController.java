package com.guardians.udss.controller;

import com.guardians.udss.response.ApiResponse;
import com.guardians.udss.services.S3FileService;
import com.guardians.udss.services.S3FileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "Upload a file to the S3 bucket for a user")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @RequestParam String userName,
            @RequestPart("file") MultipartFile file) {
        try {
            String fileKey = s3FileService.uploadFile(userName, file);
            log.info("File uploaded successfully for user: {}", userName);
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.CREATED.value(), "File uploaded successfully", fileKey);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error uploading file for user {}: {}", userName, e.getMessage());
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to upload file. Please try again.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Search files for a user in the S3 bucket")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<String>>> searchFiles(
            @RequestParam String userName,
            @RequestParam String searchTerm) {
        try {
            List<String> files = s3FileService.searchFiles(userName, searchTerm);
            if (files.isEmpty()) {
                ApiResponse<List<String>> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "No files found matching the search term.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            ApiResponse<List<String>> response = new ApiResponse<>(HttpStatus.OK.value(), "Files retrieved successfully", files);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching files for user {}: {}", userName, e.getMessage());
            ApiResponse<List<String>> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to search files. Please try again.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Download a specific file from S3 for a user")
    @GetMapping("/download")
    public ResponseEntity<ApiResponse<String>> downloadFile(
            @RequestParam String userName,
            @RequestParam String fileName) {
        String key = userName + "/" + fileName;

        try {
            String fileDownloadUrl = s3FileService.downloadFile(key).toString();
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "File downloaded successfully", fileDownloadUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error downloading file for user {}: {}", userName, e.getMessage());
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "File not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Delete a specific file from S3 for a user")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteFile(@RequestParam String userName, @RequestParam String fileName) {
        try {
            s3FileService.deleteFile(userName, fileName); // Call delete from service layer
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "File deleted successfully", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting file for user {}: {}", userName, e.getMessage());
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Error deleting file", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
