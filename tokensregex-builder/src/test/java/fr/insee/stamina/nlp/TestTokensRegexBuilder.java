package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class TestTokensRegexBuilder {

    public TokensRegexBuilder builder;

    public static final String RESOURCES        = "src/test/resources/";

    @Before
    public void setUp() throws IOException {
        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
        properties.put("annotators", "tokenize, ssplit, pos, custom.lemma");
        properties.setProperty("customAnnotatorClass.custom.lemma", "fr.insee.stamina.nlp.FrenchLemmaAnnotator");
        properties.setProperty("french.lemma.lemmaFile", "src/test/resources/lexique_fr.txt");

        builder = TokensRegexBuilder.instance(properties);
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get(String.format("%stest.rules", RESOURCES)));
    }

    @Test
    public void testSimple1() throws Exception {
        builder.build(Paths.get(String.format("%stest.csv", RESOURCES)), Paths.get(String.format("%stest.rules", RESOURCES)));

        Assert.assertTrue(Files.exists(Paths.get(String.format("%stest.rules", RESOURCES))));
        Assert.assertEquals(
                Files.readAllLines(Paths.get(String.format("%sexpected.rules", RESOURCES))),
                Files.readAllLines(Paths.get(String.format("%stest.rules", RESOURCES))));
    }

    @Test(expected = TokensRegexBuilderException.class)
    public void testException1() throws Exception {
        builder.build(Paths.get(String.format("%syolo.csv", RESOURCES)), Paths.get(String.format("%stest.rules", RESOURCES)));
    }
}
