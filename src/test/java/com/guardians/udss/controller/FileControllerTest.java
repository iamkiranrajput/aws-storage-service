package com.guardians.udss.controller;

import com.guardians.udss.controller.FileController;
import com.guardians.udss.services.S3FileService;
import com.guardians.udss.services.S3FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private S3FileServiceImpl s3FileService;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for searchFiles
    @Test
    void searchFilesListOfFiles() throws Exception {
        String userName = "kiranrana";
        String searchTerm = "project";
        List<String> mockFiles = Arrays.asList("project1.pdf", "project2.pdf");

        when(s3FileService.searchFiles(userName, searchTerm)).thenReturn(mockFiles);
        ResponseEntity<List<String>> response = fileController.searchFiles(userName, searchTerm);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockFiles, response.getBody());
    }

    @Test
    void searchFilesInternalServerErrorOnFailure() throws Exception {
        String userName = "kiranrana";
        String searchTerm = "project";

        when(s3FileService.searchFiles(userName, searchTerm)).thenThrow(new RuntimeException("Search error"));

        ResponseEntity<List<String>> response = fileController.searchFiles(userName, searchTerm);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Test case for downloadFile
    @Test
    void downloadFileFileAsInputStreamResource() throws Exception {
        String userName = "kiranrana";
        String fileName = "dataengineer.pdf";
        String key = userName + "/" + fileName;
        InputStream mockInputStream = new ByteArrayInputStream("file content".getBytes());
        when(s3FileService.downloadFile(key)).thenReturn(mockInputStream);
        ResponseEntity<InputStreamResource> response = fileController.downloadFile(userName, fileName);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    void downloadFileNotFoundOnFailure() throws Exception {
        String userName = "kiranrana";
        String fileName = "dataengineer.pdf";
        String key = userName + "/" + fileName;

        when(s3FileService.downloadFile(key)).thenThrow(new RuntimeException("Download error"));

        ResponseEntity<InputStreamResource> response = fileController.downloadFile(userName, fileName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void uploadFileSuccessMessage() throws Exception {
        String userName = "kiranrana";
        MockMultipartFile file = new MockMultipartFile("file", "dataengineer.pdf", "application/pdf", "sample content".getBytes());
        String fileKey = userName + "/dataengineer.pdf";

        when(s3FileService.uploadFile(userName, file)).thenReturn(fileKey);

        ResponseEntity<String> response = fileController.uploadFile(userName, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File uploaded successfully: " + fileKey, response.getBody());
    }

    @Test
    void uploadFileInternalServerErrorOnFailure() throws Exception {
        String userName = "kiranrana";
        MockMultipartFile file = new MockMultipartFile("file", "dataengineer.pdf", "application/pdf", "sample content".getBytes());

        when(s3FileService.uploadFile(userName, file)).thenThrow(new RuntimeException("Upload error"));

        ResponseEntity<String> response = fileController.uploadFile(userName, file);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error uploading file", response.getBody());
    }

    // Test case for deleteFile
    @Test
    void deleteFile_ShouldReturnSuccessMessage() throws Exception {
        String userName = "kiranrana";
        String fileName = "dataengineer.pdf";

        doNothing().when(s3FileService).deleteFile(userName, fileName);
        ResponseEntity<String> response = fileController.deleteFile(userName, fileName);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File deleted successfully", response.getBody());
    }

    @Test
    void deleteFile_ShouldReturnNotFoundOnFailure() throws Exception {
        String userName = "kiranrana";
        String fileName = "dataengineer.pdf";

        doThrow(new RuntimeException("Delete error")).when(s3FileService).deleteFile(userName, fileName);
        ResponseEntity<String> response = fileController.deleteFile(userName, fileName);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error deleting file", response.getBody());
    }
}
