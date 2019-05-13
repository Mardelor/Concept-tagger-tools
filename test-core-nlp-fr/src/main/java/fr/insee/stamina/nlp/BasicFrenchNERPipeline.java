package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.stream.Collectors;

public class BasicFrenchNERPipeline {

    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
        System.out.println("Default pipeline properties:");
        for (Object key : properties.keySet()) System.out.println(key + " = " + properties.get(key));

        properties.put("annotators", "tokenize, ssplit, pos, custom.lemma, depparse, tokensregex");

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
        properties.setProperty("tokensregex.rules", "src/main/resources/testing.rules");

        System.out.println("\nModified pipeline properties:");
        for (Object key : properties.keySet()) System.out.println(key + " = " + properties.get(key));

        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        Path path = Paths.get("src/main/resources/texte.txt");
        String text = Files.lines(path).collect(Collectors.joining("\n"));

        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        Files.deleteIfExists(Paths.get("src/main/resources/results.xml"));
        Files.write(Paths.get("src/main/resources/results.xml"),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<annotations>\n".getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                Files.write(Paths.get("src/main/resources/results.xml"),
                        String.format("<token>\n<word>%s</word>\n<POS>%s</POS>\n<NER>%s</NER>\n</token>\n", token.word(), token.tag(), token.ner()).getBytes(),
                        StandardOpenOption.APPEND);
            }
        }
        Files.write(Paths.get("src/main/resources/results.xml"), "</annotations>".getBytes(), StandardOpenOption.APPEND);
    }
}
