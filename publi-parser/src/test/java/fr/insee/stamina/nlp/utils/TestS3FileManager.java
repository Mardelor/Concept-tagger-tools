package fr.insee.stamina.nlp.utils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

public class TestS3FileManager {

    private static S3FileManager s3FileManager;

    @BeforeClass
    public static void setUp() {
        s3FileManager = S3FileManager.getInstance();
    }

    @AfterClass
    public static void tearDown() {
        s3FileManager = null;
    }

    @Test
    public void testCopyObjectToFileSystem() {
        s3FileManager.copyObjectToFileSystem(
                System.getenv("BUCKET_ID"),"publications/test.xml", "./src/test/resources/test.xml");
        Assert.assertTrue((new File("./src/test/resources/test.xml")).exists());
    }
}
