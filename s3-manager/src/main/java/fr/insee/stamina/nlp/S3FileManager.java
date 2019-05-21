package fr.insee.stamina.nlp;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

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
     * @param target
     *              desired location of the file
     */
    public void copyObjectToFileSystem(String bucketName, String fileKey, Path target) throws IOException {
        InputStream stream = readObject(bucketName, fileKey);
        Files.copy(stream, target);
    }

    /**
     * Get object stream from the specified s3 file system file
     * @param bucketName
     *              bucket name
     * @param fileKey
     *              file key in s3
     * @return  stream to read the file
     */
    public InputStream readObject(String bucketName, String fileKey) {
        S3Object object = s3.getObject(bucketName, fileKey);
        return object.getObjectContent();
    }

    /**
     * Get the instance
     * @return fr.insee.stamina.nlp.S3FileManager instance
     */
    public static S3FileManager getInstance() {
        if (instance == null) {
            instance = new S3FileManager();
        }
        return instance;
    }

    /**
     * Utils for command line
     * @param args
     *              command and command argument
     */
    public static void main(String args[]) {
        if (args.length < 3) {
            System.out.println("Usage : minio <command> <command args>");
            System.out.println("Commands:");
            System.out.println("\tget <bucket> <files...>");
            System.out.println("\tOptions:");
            System.out.println("\t\t-req <bucket> <filelist>");
        }
        String command = args[0];
        instance = getInstance();

        switch (command) {
            case "get" :
                get(args);
            default:
                System.out.println(String.format("command %s not found", command));
        }
    }

    /**
     * Handle get command
     * @param args
     *              get command args
     */
    private static void get(String[] args) {
        if (args[1].equals("-req")) {
            // TODO : terminer
        } else {
            String bucket = args[1];
            ArrayList<String> files = new ArrayList<>();
            for (int i=2; i<args.length; i++) {
                files.add(args[i]);
            }
            for (String file : files) {
                // TODO : terminer
            }
        }
    }
}
