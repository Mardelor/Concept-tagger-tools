package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Collectors;

public class BasicFrenchNERPipeline {
    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
        System.out.println("Default pipeline properties:");
        for (Object key : properties.keySet()) System.out.println(key + " = " + properties.get(key));

        properties.put("annotators", "tokenize, ssplit, pos, custom.lemma, depparse, ner");

        properties.setProperty("tokenize.options", "untokenizable=noneDelete");
        properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
        properties.setProperty("customAnnotatorClass.custom.lemma", "fr.insee.stamina.nlp.lemma.FrenchLemmaAnnotator");
        properties.setProperty("french.lemma.lemmaFile", "src/main/resources/lexique_fr.txt");

        // Run no models
        properties.setProperty("ner.model", "");
        // Specify regex rules
        properties.setProperty("ner.fine.regexner.mapping", "src/main/resources/concepts.rules");
        // Remove entity mention sub-annotator
        properties.setProperty("ner.buildEntityMentions", "false");

        System.out.println("\nModified pipeline properties:");
        for (Object key : properties.keySet()) System.out.println(key + " = " + properties.get(key));

        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        Path path = Paths.get("src/main/resources/texte.txt");
        String text = Files.lines(path).collect(Collectors.joining("\n"));

        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        pipeline.xmlPrint(document, Files.newOutputStream(Paths.get("src/main/resources/results.xml")));
    }
}
