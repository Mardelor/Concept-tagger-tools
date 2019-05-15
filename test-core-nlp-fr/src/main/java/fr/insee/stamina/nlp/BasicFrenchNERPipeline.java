package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BasicFrenchNERPipeline {

    // TODO remettre un main adapté

    public static void main(String arg[]) throws IOException {
        System.out.println(run(arg[0]));
    }

    public static String run(String text) throws IOException {
        String annotatedText;

        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));

        properties.put("annotators", "tokenize, ssplit, pos, custom.lemma, depparse, tokensregex");

        properties.setProperty("tokenize.options", "untokenizable=noneDelete");
        properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
        properties.setProperty("customAnnotatorClass.custom.lemma", "fr.insee.stamina.nlp.lemma.FrenchLemmaAnnotator");
        properties.setProperty("french.lemma.lemmaFile", "src/main/resources/lexique_fr.txt");
        properties.setProperty("encoding", "UTF-8");

        properties.setProperty("tokensregex.rules", "src/main/resources/concepts.rules");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        StringBuilder builder = new StringBuilder("");
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                if (!Pattern.matches("[,.;!]", token.word())) {
                    builder.append(" ");
                }
                if (token.ner().equals("STAT-CPT")) {
                    builder.append(String.format("<STAT-CPT> %s </STAT-CPT>", token.word()));
                } else {
                    builder.append(token.word());
                }
            }
        }
        annotatedText = builder.toString().replace(" </STAT-CPT> <STAT-CPT>", "");

        return annotatedText;
    }
}
