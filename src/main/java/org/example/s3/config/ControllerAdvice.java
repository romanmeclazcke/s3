package org.example.s3.config;


import org.example.s3.excepcion.BucketNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(BucketAlreadyOwnedByYouException.class)
    public ResponseEntity<String> handleBucketAlreadyOwnedByYouException(BucketAlreadyOwnedByYouException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BucketAlreadyExistsException.class)
    public ResponseEntity<String> handleBucketAlreadyExistsException(BucketAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BucketNotFound.class)
    public ResponseEntity<String> handleBucketNotFound(BucketNotFound ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchKeyException.class)
    public ResponseEntity<String> handleNoSuchKeyException(NoSuchKeyException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchFieldException.class)
    public ResponseEntity<String> handleNoSuchFieldException(NoSuchFieldException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchBucketException.class)
    public ResponseEntity<String> handleNoSuchBucketException(NoSuchBucketException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
