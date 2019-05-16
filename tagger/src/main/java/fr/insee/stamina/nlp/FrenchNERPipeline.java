package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

public class FrenchNERPipeline {

    public static final String RESOURCES                    = "src/main/resources/";

    public static final String PUNCTUATION_REGEX            = "[,.;!:]";

    private static String NER_XML_TAG                       = "STAT-CPT";

    private static StanfordCoreNLP pipeline;

    public static void init() throws IOException {
        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));

        properties.put("annotators", "tokenize, ssplit, pos, custom.lemma, depparse, tokensregex");

        properties.setProperty("tokenize.options", "untokenizable=noneDelete");
        properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
        properties.setProperty("customAnnotatorClass.custom.lemma", "fr.insee.stamina.nlp.FrenchLemmaAnnotator");
        properties.setProperty("french.lemma.lemmaFile", RESOURCES + "lexique_fr.txt");
        properties.setProperty("encoding", "UTF-8");

        properties.setProperty("tokensregex.rules", RESOURCES + "concepts.rules");

        init(properties);
    }

    public static void init(Properties properties) {
        pipeline = new StanfordCoreNLP(properties);
    }

    public String run(String text) {
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        return format(putXMLTags(annotation));
    }

    private String format(String XMLTaggedText) {
        return XMLTaggedText.replace(String.format("</%s> <%s>", NER_XML_TAG, NER_XML_TAG), "");
    }

    private String putXMLTags(Annotation annotatedText) {
        StringBuilder builder = new StringBuilder("");
        for (CoreMap sentence : annotatedText.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                if (!Pattern.matches(PUNCTUATION_REGEX, token.word())) {
                    builder.append(" ");
                }
                if (token.ner().equals(NER_XML_TAG)) {
                    builder.append(String.format("<%s> %s </%s>", NER_XML_TAG, token.word(), NER_XML_TAG));
                } else {
                    builder.append(token.word());
                }
            }
        }
        return builder.toString();
    }
}
