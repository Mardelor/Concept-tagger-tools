package fr.insee.stamina.nlp;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

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
    public void testCopyObjectToFileSystem() throws IOException {
        s3FileManager.copyObjectToFileSystem(
                System.getenv("BUCKET_ID"),"publications/test.xml", Paths.get("./src/test/resources/test.xml"));
        Assert.assertTrue((new File("./src/test/resources/test.xml")).exists());
    }

    @Test
    public void getExample() throws Exception {
        s3FileManager.copyObjectToFileSystem(
                System.getenv("BUCKET_ID"),
                System.getenv("BUCKET_ID") + "/publications/xml/1280638-ip1174.xml",
                Paths.get("src/test/resources/ip1174.xml"));

        s3FileManager.copyObjectToFileSystem(
                System.getenv("BUCKET_ID"),
                System.getenv("BUCKET_ID") + "/concepts/concepts-query-results.csv",
                Paths.get("src/test/resources/concepts.csv"));
    }
}
