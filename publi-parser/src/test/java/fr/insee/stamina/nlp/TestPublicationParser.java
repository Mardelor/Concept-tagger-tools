package fr.insee.stamina.nlp;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TestPublicationParser {

    private static PublicationParser publicationParser;

    @BeforeClass
    public static void setUp() {
        publicationParser = PublicationParser.getInstance();
    }

    @AfterClass
    public static void tearDown() {
        publicationParser = null;
    }

    @Test
    public void testParse() throws SAXException, IOException {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("hello");
        tags.add("ah");

        S3FileManager s3 = S3FileManager.getInstance();
        s3.copyObjectToFileSystem(
                System.getenv("BUCKET_ID"),
                "publications/test.xml",
                "./src/test/resources/test.xml"
        );
        HashMap<String, String> res = publicationParser
                .parse("./src/test/resources/test.xml", "root", tags);
        Assert.assertEquals("AH", res.get("ah"));
        Assert.assertEquals("Hello !", res.get("hello"));
    }

    @Test
    public void testSQL() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" +
                        System.getenv("POSTGRE_HOST")+ ":" +
                        System.getenv("POSTGRE_PORT") + "/" +
                        System.getenv("POSTGRE_DB"),
                System.getenv("BUCKET_ID"), System.getenv("PSSWD"));

    }
}
