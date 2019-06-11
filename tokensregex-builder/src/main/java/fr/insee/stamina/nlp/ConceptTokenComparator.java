package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.util.Comparator;
import java.util.Properties;

/**
 * Concept Comparator
 * @see Comparator
 */
public class ConceptTokenComparator implements Comparator<String> {

    /**
     * Stanford Core NLP pipeline
     */
    private static StanfordCoreNLP pipeline;

    static {
        try {
            Properties properties = new Properties();
            properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
            properties.put("annotators", "tokenize");
            pipeline = new StanfordCoreNLP(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compare(String s, String p) {
        Annotation as = new Annotation(s);
        Annotation ap = new Annotation(p);
        pipeline.annotate(as);
        pipeline.annotate(ap);
        return as.get(CoreAnnotations.TokensAnnotation.class).size() - ap.get(CoreAnnotations.TokensAnnotation.class).size();
    }
}
