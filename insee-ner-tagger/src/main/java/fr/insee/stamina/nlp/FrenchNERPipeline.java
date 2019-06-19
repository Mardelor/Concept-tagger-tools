package fr.insee.stamina.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Custom NER pipeline, formatting output into specific xml format
 */
public class FrenchNERPipeline {

    /**
     * Core NLP pipeline
     */
    private static StanfordCoreNLP pipeline;

    /**
     * Init pipeline
     * @param properties
     *              pipeline properties
     */
    public static void init(Properties properties) {
        pipeline = new StanfordCoreNLP(properties);
    }

    /**
     * run pipeline on a text
     * @param text
     *              text to annotate
     * @return  annotate named entities using xml tags
     */
    public String run(String text) {
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        return annotation.get(CoreAnnotations.SentencesAnnotation.class).stream()
                .map(format)
                .reduce(String::concat)
                .orElse("");
    }

    /**
     * Annotate a sentence
     */
    private static Function<CoreMap, String> format = sentence -> {
        ArrayList<CoreLabel> ne = new ArrayList<>();
        StringBuilder out = new StringBuilder();

        for (CoreLabel current: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            if (current.ner().equals("O")) {
                if (!ne.isEmpty()) {
                    out.append(neFormat(ne));
                    ne.clear();
                }
                if (current.tag().equals("PUNCT")) {
                    out.append(current.originalText());
                } else {
                    out.append(String.format(" %s", current.originalText()));
                }
            } else if (ne.isEmpty() ||
                    (ne.get(0).ner().equals(current.ner()) &&
                            (((String) (Object) ne.get(0).get(CoreAnnotations.MentionsAnnotation.class)).equals((String) (Object) current.get(CoreAnnotations.MentionsAnnotation.class))))) {
                ne.add(current);
            } else {
                out.append(neFormat(ne));
                ne.clear();
                ne.add(current);
            }
        }

        if(!ne.isEmpty()) {
            out.append(neFormat(ne));
        }

        return out.toString();
    };

    /**
     * put xml tags to a ne
     * @param labels
     *              list of Core NLP labels
     * @return  string with xml tags
     */
    private static String neFormat(List<CoreLabel> labels) {
        return String.format("<%s id=\"%s\">%s</%s>",
                labels.get(0).ner(),
                labels.get(0).get(CoreAnnotations.MentionsAnnotation.class),
                labels.stream().map(CoreLabel::originalText).reduce(concatenate).orElse(""),
                labels.get(0).ner());
    }

    private static BinaryOperator<String> concatenate = (r, s) -> String.format("%s %s", r ,s);
}
