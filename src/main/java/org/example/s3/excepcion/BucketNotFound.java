package org.example.s3.excepcion;

public class BucketNotFound extends RuntimeException{
    public BucketNotFound(String message) {
        super(message);
    }
}
