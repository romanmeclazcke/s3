package org.example.s3.service.impl;

import org.apache.logging.log4j.util.InternalException;
import org.example.s3.excepcion.BucketNotFound;
import org.example.s3.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;

@Service
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Autowired
    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public List<String> listBuckets() {
        return this.s3Client.listBuckets().buckets().stream()
                .map(Bucket::name)
                .toList();
    }

    @Override
    public String createBucket(String bucketName) {
        try {
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            CreateBucketResponse createBucketResponse = this.s3Client.createBucket(bucketRequest);
            return "Bucket created: " + createBucketResponse.location();
        } catch (BucketAlreadyOwnedByYouException ex) {
            throw  BucketAlreadyOwnedByYouException.builder().message("Bucket already exists and is owned by you: " + bucketName).build();
        } catch (BucketAlreadyExistsException ex) {
            throw BucketAlreadyExistsException.builder().message("Bucket already exists: " + bucketName).build();
        } catch (S3Exception ex) {
            return "Error creating bucket: " + ex.getMessage();
        }
    }

    @Override
    public String existsBucket(String bucketName) {
        try {
            this.s3Client.headBucket(b -> b.bucket(bucketName));
        } catch (NoSuchBucketException ex) {
            NoSuchBucketException.builder().message("Bucket not found: " + bucketName).build();
        }
        return "Bucket exists: " + bucketName;
    }

    @Override
    public String deleteBucket(String bucketName) {
        DeleteBucketResponse deleteBucketResponse = this.s3Client.deleteBucket(bucketBuilder-> bucketBuilder.bucket(bucketName));
        if (deleteBucketResponse.sdkHttpResponse().isSuccessful()) {
            return "Bucket deleted: " + bucketName;
        } else {
            throw new BucketNotFound("Bucket not found");
        }
    }

    @Override
    public Boolean uploadFile(String bucketName, String fileName, MultipartFile file) {
        try {

            this.existsBucket(bucketName);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return response.sdkHttpResponse().isSuccessful();
        } catch (IOException e) {
            throw new InternalException("Error al leer el archivo", e);
        }
    }

    @Override
    public byte[] downloadFile(String bucketName, String fileName) throws NoSuchFieldException {
        try {
            this.existsBucket(bucketName);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            return this.s3Client.getObjectAsBytes(getObjectRequest).asByteArray();

        } catch (NoSuchKeyException ex) {
            throw  new NoSuchFieldException("El archivo con clave " + fileName + " no existe en el bucket " + bucketName);
        }
    }
}
