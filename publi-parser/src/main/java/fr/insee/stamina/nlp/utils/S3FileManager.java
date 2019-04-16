package fr.insee.stamina.nlp.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Little class to handle files stored in Minio
 */
public class S3FileManager {
    /**
     * This class is a singleton
     */
    private static S3FileManager instance;

    /**
     * AmazonS3 client
     */
    private AmazonS3 s3;

    /**
     * Build the AmazonS3 client
     */
    private S3FileManager() {
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
    }

    /**
     * Copy object from s3 file system to local file system
     * @param bucketName
     *              name of the bucket which contains the object
     * @param fileKey
     *              file path
     * @param destinationPath
     *              desired location of the file
     */
    public void copyObjectToFileSystem(String bucketName, String fileKey, String destinationPath) {
        File file = new File(destinationPath);
        S3Object object = s3.getObject(bucketName, fileKey);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the instance
     * @return S3FileManager instance
     */
    public static S3FileManager getInstance() {
        if (instance == null) {
            instance = new S3FileManager();
        }
        return instance;
    }
}
