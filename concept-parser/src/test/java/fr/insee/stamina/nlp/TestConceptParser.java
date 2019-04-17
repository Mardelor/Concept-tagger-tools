package fr.insee.stamina.nlp;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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

        Model model = ModelFactory.createDefaultModel();
        model.read("./src/test/resources/test.csv");
    }

}
