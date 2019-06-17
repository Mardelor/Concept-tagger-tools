package fr.insee.stamina.nlp.utils;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.util.Comparator;
import java.util.Properties;

public class ConceptComparator implements Comparator<String> {
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
        Annotation as = new Annotation(s.split("c[0-9]{4},")[1]);
        Annotation ap = new Annotation(p.split("c[0-9]{4},")[1]);
        pipeline.annotate(as);
        pipeline.annotate(ap);
        return as.get(CoreAnnotations.TokensAnnotation.class).size() - ap.get(CoreAnnotations.TokensAnnotation.class).size();
    }

}
