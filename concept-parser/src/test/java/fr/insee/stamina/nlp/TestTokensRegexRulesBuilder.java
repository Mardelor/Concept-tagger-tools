package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Properties;

public class TestTokensRegexRulesBuilder {

    public TokensRegexRulesBuilder builder;

    public static final String RESOURCES = "src/test/resources/";

    @Before
    public void setUp() {
        builder = TokensRegexRulesBuilder.getInstance();
    }

    @Test
    public void rawFullCorpusTest() throws Exception {
        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));

        properties.put("annotators", "tokenize, ssplit, pos, custom.lemma");

        properties.setProperty("tokenize.options", "untokenizable=noneDelete");
        properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
        properties.setProperty("customAnnotatorClass.custom.lemma", "FrenchLemmaAnnotator");
        properties.setProperty("french.lemma.lemmaFile", RESOURCES + "lexique_fr.txt");
        properties.setProperty("encoding", "UTF-8");

        properties.setProperty(String.format("%s.%s", TokensRegexRulesBuilder.PROP_NAMESPACE, TokensRegexRulesBuilder.PROP_NER_TAG), "STAT-CPT");

        builder.init(properties);

        builder.build(Paths.get(RESOURCES + "concepts.csv"), Paths.get(RESOURCES + "concepts.rules"));
    }
}
