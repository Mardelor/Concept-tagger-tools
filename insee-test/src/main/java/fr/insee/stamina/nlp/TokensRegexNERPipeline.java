package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class TokensRegexNERPipeline {

    public StanfordCoreNLP pipeline;

    public void init() throws IOException {
        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
        properties.put("annotators", "tokenize, ssplit, pos, custom.lemma, ner, regexner");
        properties.setProperty("customAnnotatorClass.custom.lemma", "fr.insee.stamina.nlp.FrenchLemmaAnnotator");
        properties.put("regexner.mapping", "rules/insee-test.tsv");
        properties.put("ner.model", "");
        properties.put("ner.applyNumericClassifiers", "false");
        properties.put("ner.applyFineGrained", "false");
        properties.put("ner.buildEntityMentions", "false");
        properties.put("ner.useSUTime", "false");
        pipeline = new StanfordCoreNLP(properties);
    }

    public Annotation run(String text) {
        Annotation ann = new Annotation(text);
        pipeline.annotate(ann);
        return ann;
    }

    public StanfordCoreNLP getPipeline() {
        return pipeline;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage : <text>");
            return;
        }
        String text = args[0];
        TokensRegexNERPipeline pipeline = new TokensRegexNERPipeline();
        try {
            pipeline.init();
            Annotation annotation = pipeline.run(args[0]);
            pipeline.getPipeline().xmlPrint(annotation, Files.newOutputStream(Paths.get("../data/out/test-ner.xml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
