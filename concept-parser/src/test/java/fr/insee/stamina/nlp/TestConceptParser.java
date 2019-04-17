package fr.insee.stamina.nlp;

import org.junit.Test;

public class TestConceptParser {

    @Test
    public void test() {
        S3FileManager s3 = S3FileManager.getInstance();
        s3.copyObjectToFileSystem(
                System.getenv("BUCKET_ID"),
                "concepts/concepts.csv",
                "./src/test/resources/insee-concepts.csv"
        );

    }

}
