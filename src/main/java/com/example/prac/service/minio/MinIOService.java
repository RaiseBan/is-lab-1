package com.example.prac.service.minio;

import io.minio.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinIOService {

    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    private static final String TEMP_PREFIX = "temp/";


    public void uploadTempFile(String fileName, InputStream fileStream, String contentType) throws Exception {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(TEMP_PREFIX + fileName)
                            .stream(fileStream, fileStream.available(), -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error uploading temp file to MinIO: " + e.getMessage(), e);
        }
    }

    public void commitFile(String fileName) throws Exception {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .source(CopySource.builder()
                                    .bucket(bucketName)
                                    .object(TEMP_PREFIX + fileName)
                                    .build())
                            .object(fileName)
                            .build()
            );
            deleteTempFile(fileName);
        } catch (Exception e) {
            throw new RuntimeException("Error committing file in MinIO: " + e.getMessage(), e);
        }
    }

    public void deleteTempFile(String fileName) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(TEMP_PREFIX + fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error deleting temp file from MinIO: " + e.getMessage(), e);
        }
    }


    public void deleteFinalFile(String fileName) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (MinioException e) {
            System.err.println("Error deleting final file from MinIO: " + e.getMessage());
            throw e;
        }
    }

    public InputStream downloadFile(String fileName) throws Exception {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName) // Имя бакета, из которого будет загружен файл
                            .object(fileName) // Имя файла
                            .build()
            );
        } catch (MinioException e) {
            throw new RuntimeException("Error downloading file from MinIO: " + e.getMessage(), e);
        }
    }

}
