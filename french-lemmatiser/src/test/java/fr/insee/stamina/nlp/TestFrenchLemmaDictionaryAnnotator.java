package fr.insee.stamina.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.function.Predicate;

public class TestFrenchLemmaDictionaryAnnotator {

    private Properties properties;

    @Before
    public void setUp() throws Exception {
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("/config/default.props"));
        properties.setProperty("annotators", "tokenize, ssplit, pos, custom.lemma");
    }

    @After
    public void tearDown() throws Exception {
        properties = null;
    }

    @Test
    public void testDefault() throws Exception {
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        Annotation annotation = new Annotation("Ceci est un test");
        pipeline.annotate(annotation);

        Predicate<CoreLabel> lemma = label -> label.lemma() != null && !label.lemma().isEmpty();
        Assert.assertTrue(annotation.get(CoreAnnotations.TokensAnnotation.class)
                .stream()
                .allMatch(lemma));
    }
}
