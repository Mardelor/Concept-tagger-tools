package fr.insee.stamina.nlp;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

public class TestConceptParser {

    @Ignore
    @Test
    public void test() {
        S3FileManager s3 = S3FileManager.getInstance();
        s3.copyObjectToFileSystem(
                System.getenv("BUCKET_ID"),
                "concepts/concepts.csv",
                "./src/test/resources/insee-concepts.csv"
        );

        // TODO : find a way to get RDF graph ...
        Model model = null;

        String PREFIX = "PREFIX skos:<http://www.w3.org/2004/02/skos/core#>";

        Query query = QueryFactory.create(PREFIX +
                "SELECT ?s ?p ?o WHERE {" +
                "?s ?p ?o ." +
                "}"
        );

        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        OutputStream output = new OutputStream() {
            private StringBuilder string = new StringBuilder();
            @Override
            public void write(int b) throws IOException {
                this.string.append((char) b);
            }
            //Netbeans IDE automatically overrides this toString()
            public String toString() {
                return this.string.toString();
            }
        };
        ResultSetFormatter.out(output, results, query);
        System.out.println(output.toString());
    }

    @Test
    public void conceptTrainTest() {
        S3FileManager s3 = S3FileManager.getInstance();
        s3.copyObjectToFileSystem(
                System.getenv("BUCKET_ID"),
                "concepts/concepts-query-results.csv",
                "./src/test/resources/concepts-query-results.csv"
        );
    }
}
