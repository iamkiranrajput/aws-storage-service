package com.guardians.udss.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface S3FileService {

     List<String> searchFiles(String username, String searchTerm);
     InputStream downloadFile(String key);
     String uploadFile(String username, MultipartFile file);
     void deleteFile(String username, String filename);

}
