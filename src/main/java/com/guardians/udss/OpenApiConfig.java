package com.guardians.udss;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("UDSS (User Document Storage Service) API")
                        .version("1.0")
                        .description("The UDSS (Universal Document Storage System) API is a RESTful service designed to facilitate file storage, retrieval, and management. " +
                                "This service allows users to upload, download, search, and delete files in a secure cloud storage environment. " +
                                "With the UDSS API, users can efficiently manage their files with the following features: " +
                                "File upload, File download, File search, File deletion, and much more. " +
                                "This system is built using Spring Boot and integrates with AWS S3 for scalable and secure file storage.\n\n" +
                                "For testing and exploring the API, refer to the [Postman UDSS API](https://www.postman.com/glitch-guardians/guardians/collection/085b6kw/aws-udss-api).").contact(new Contact()
                        .name("Kiran Rajput")
                        .email("rajputkiran2805@gmail.com")
                        .url("https://www.linkedin.com/in/iamkiranrajput")));
    }
}
