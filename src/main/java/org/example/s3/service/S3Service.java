package org.example.s3.service;


import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface S3Service  {

    List<String> listBuckets();
    String createBucket(String bucketName);
    String existsBucket(String bucketName);
    String deleteBucket(String bucketName);
    Boolean uploadFile(String bucketName, String fileName, MultipartFile file);
    byte[] downloadFile(String bucketName, String fileName) throws NoSuchFieldException;
}
