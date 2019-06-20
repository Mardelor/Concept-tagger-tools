package fr.insee.stamina.nlp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class RemoteTest {

    // private AmazonS3Client s3;

    private String BUCKET;

    // TODO
    /*
            BasicSessionCredentials credentials = new BasicSessionCredentials(
                System.getenv("AWS_ACCESS_KEY_ID"),
                System.getenv("AWS_SECRET_ACCESS_KEY"),
                System.getenv("AWS_SESSION_TOKEN")
        );

        AwsClientBuilder.EndpointConfiguration config =
                new AwsClientBuilder.EndpointConfiguration(
                        System.getenv("AWS_S3_ENDPOINT"),
                        System.getenv("AWS_DEFAULT_REGION"));
        this.s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(config)
                .build();
     */
}
