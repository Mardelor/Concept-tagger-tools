package fr.insee.stamina.nlp;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

public class TestPublicationParser {

    // TODO : use properties file instead of env variables

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
        S3FileManager s3 = S3FileManager.getInstance();
        s3.copyObjectToFileSystem(
                System.getenv("BUCKET_ID"),
                "publications/test.xml",
                Paths.get("./src/test/resources/test.xml")
        );
        HashMap<String, String> res = publicationParser
                .parse("./src/test/resources/test.xml", "root", "hello", "ah");
        Assert.assertEquals("AH", res.get("ah"));
        Assert.assertEquals("Hello !", res.get("hello"));
    }

    @Test
    public void testXSLT() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource("src/test/resources/publication-extract.xsl"));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        transformer.transform(new StreamSource("src/test/resources/data/ip1174.xml"), new StreamResult(stream));

        System.out.println(stream.toString().substring(38));
    }

    @Test
    public void testParseXSLT() throws Exception {
        S3FileManager s3 = S3FileManager.getInstance();
        PublicationParser parser = PublicationParser.getInstance();

        Properties properties = new Properties();
        properties.setProperty("xsl", "src/test/resources/publication-extract.xsl");

        parser.initialize(properties);

        InputStream input = s3.readObject(System.getenv("BUCKET_ID"), System.getenv("BUCKET_ID") + "/publications/xml/1280638-ip1174.xml");
        Files.write(Paths.get("src/test/resources/texte.txt"), parser.parse(input).getBytes());
    }
}
