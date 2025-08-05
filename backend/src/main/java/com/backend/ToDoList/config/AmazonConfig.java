package com.backend.ToDoList.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AmazonConfig {
    @Value("${aws.access-key}")
    private String accessKeyId;
    @Value("${aws.secret-key}")
    private String secretAccessKey;
    @Value("${aws.bucket-name}")
    private String bucketName;
    @Value("${aws.region}")
    private String region;
    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

//        log.info(accessKeyId + ":" + secretAccessKey);
        return AmazonS3Client.builder()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
