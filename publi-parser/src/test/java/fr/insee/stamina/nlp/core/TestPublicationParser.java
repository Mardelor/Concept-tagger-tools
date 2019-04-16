package fr.insee.stamina.nlp.core;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
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
        HashMap<String, String> res = publicationParser
                .parse("./src/test/resources/test.xml", "root", tags);
        Assert.assertEquals("AH", res.get("ah"));
        Assert.assertEquals("Hello !", res.get("hello"));
    }
}
