package fr.insee.stamina.nlp.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPublicationParser {

    private PublicationParser publicationParser;

    @BeforeClass
    public void setUp() {
        publicationParser = PublicationParser.getInstance();
    }

    @AfterClass
    public void tearDown() {
        publicationParser = null;
    }

    @Test
    public void testParse() {

    }
}
