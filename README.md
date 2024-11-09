# S3 File Management API

This project is a Spring Boot REST API for managing files within an Amazon S3 bucket. It allows users to upload, download, search, and delete files stored in user-specific directories within the bucket. The application is intended for scenarios where users need secure and easy access to file storage on AWS.

## Features

- **Upload File**: Allows users to upload files to a user-specific directory in an S3 bucket.
- **Download File**: Enables downloading of a specified file from a user’s directory on S3.
- **Search Files**: Provides a search function to locate files in a user’s directory based on a search term.
- **Delete File**: Allows deletion of a specific file from a user's directory.

## Technologies Used

- **Spring Boot**: Framework for creating REST APIs.
- **Amazon S3 SDK**: AWS SDK for interacting with S3 buckets.
- **Lombok**: For reducing boilerplate code.
- **SLF4J & Logback**: For logging purposes.
- **JUnit & Mockito**: For unit testing.

## Prerequisites

- **Java 11 or later**
- **Amazon S3 bucket** with appropriate access permissions
- **AWS IAM credentials** with S3 access (read and write) permissions
- **Maven** for building the project

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/iamkiranrajput/aws-storage-service.git
cd aws-storage-service
```

### 2. Configure AWS Credentials

Ensure your AWS credentials are configured via environment variables or a credentials file:

- **AWS_BUCKET**: The name of your S3 bucket.
- **AWS_REGION**: The AWS region where the S3 bucket is located.
- **AWS_ACCESS_KEY**: Your AWS Access Key ID.
- **AWS_SECRET_KEY**: Your AWS Secret Access Key.

You can set these environment variables in your system.

For **Linux/macOS**:

```bash
export AWS_BUCKET=your-bucket-name
export AWS_REGION=your-region
export AWS_ACCESS_KEY=your-access-key
export AWS_SECRET_KEY=your-secret-key
```

### 3. Application Configuration

In the `src/main/resources/application.yml` file, configure the following properties:

```properties
bucket-name=${AWS_BUCKET}
region=${AWS_REGION}
access-key=${AWS_ACCESS_KEY}
secret-key=${AWS_SECRET_KEY}
```

### 4. Build and Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

The API will be accessible at `http://localhost:1111`.

## API Endpoints

| Endpoint                         | Method  | Description                                                      | Parameters                                                                 |
|-----------------------------------|---------|------------------------------------------------------------------|---------------------------------------------------------------------------|
| `/api/files/upload`              | `POST`  | Upload a file to the S3 bucket for a specific user.              | `userName`: The user-specific folder where the file will be uploaded. <br> `file`: The file to upload (multipart form data). |
| `/api/files/download`            | `GET`   | Download a specific file from a user's directory in the S3 bucket. | `userName`: The user-specific folder containing the file. <br> `fileName`: The name of the file to download. |
| `/api/files/search`              | `GET`   | Search for files within a user's directory based on a search term. | `userName`: The user-specific folder to search in. <br> `searchTerm`: The term to search for in file names. |
| `/api/files/delete`              | `DELETE`| Delete a specific file from a user's directory in the S3 bucket. | `userName`: The user-specific folder containing the file. <br> `fileName`: The name of the file to delete. |

---

## Testing

Unit tests are included and can be run with the following command:

```bash
mvn test
```

## Logging

Logging is handled with SLF4J and Logback. Logs include information about file uploads, downloads, and errors.

## Error Handling

If an error occurs (e.g., file not found, upload failure), the API returns an appropriate HTTP status code along with an error message.

## License

This project is licensed under the MIT License[license.md].

---
