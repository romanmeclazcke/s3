package org.example.s3.controller;

import org.example.s3.dto.CreateBucket;
import org.example.s3.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;


    @GetMapping("/buckets")
    public ResponseEntity<List<String>> getBuckets(){
        return new ResponseEntity<>(this.s3Service.listBuckets(), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<String> createBucket(@RequestBody CreateBucket bucketRequest) {
        String response = this.s3Service.createBucket(bucketRequest.getBucketName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{bucketName}")
    public ResponseEntity<String> deleteBucket(@PathVariable String bucketName) {
        String response = this.s3Service.deleteBucket(bucketName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{bucketName}/file")
    public ResponseEntity<String> uploadFile(
            @PathVariable String bucketName,
            @RequestParam("fileName") String fileName,
            @RequestParam("file") MultipartFile file) {

        boolean success = s3Service.uploadFile(bucketName, fileName, file);
        if (success) {
            return ResponseEntity.ok("Archivo subido correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir el archivo");
        }
    }

    @GetMapping("/{bucketName}/file/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String bucketName, @PathVariable String fileName) throws NoSuchFieldException {
        byte[] response = this.s3Service.downloadFile(bucketName, fileName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
