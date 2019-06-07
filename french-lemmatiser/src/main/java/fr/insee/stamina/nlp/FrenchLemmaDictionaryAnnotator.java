package fr.insee.stamina.nlp;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Lemmatizer based on <<POS, word>, lemma> map
 *
 * @author rem
 */
public class FrenchLemmaDictionaryAnnotator implements Annotator {

    /**
     * map POS and word to lemma
     */
    private HashMap<Pair<String, String>, String> lemmap;

    /**
     * input file separator
     */
    public static final String SEPARATOR        = "\t";

    /**
     * default lemma
     */
    public static final String DEFAULT_LEMMA    = "X";

    /**
     * build a french lemma annotator
     * @param properties
     *              lemma properties
     */
    public FrenchLemmaDictionaryAnnotator(Properties properties) {
        lemmap = new HashMap<>();
        Path posWordLemmaFile = Paths.get(properties.getProperty("french.lemma.file"));
        try (Stream<String> lines = Files.lines(posWordLemmaFile)) {
            lines.forEach(fillLemma);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * fill the lemmap from a triple word \t tag \t lemma
     */
    private Consumer<String> fillLemma = line -> {
        String[] items = line.split(SEPARATOR);
        lemmap.put(new Pair<>(items[0], items[1]), items[2]);
    };

    @Override
    public void annotate(Annotation annotation) {
        String lemma;
        for (CoreLabel token : annotation.get(CoreAnnotations.TokensAnnotation.class)) {
            lemma = lemmap.getOrDefault(new Pair<>(token.word(), token.tag()), DEFAULT_LEMMA);
            token.set(CoreAnnotations.LemmaAnnotation.class, lemma);
        }
    }

    @Override
    public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
        return Collections.singleton(CoreAnnotations.LemmaAnnotation.class);
    }

    @Override
    public Set<Class<? extends CoreAnnotation>> requires() {
        return Collections.unmodifiableSet(new ArraySet<>(Arrays.asList(
                CoreAnnotations.TextAnnotation.class,
                CoreAnnotations.TokensAnnotation.class,
                CoreAnnotations.SentencesAnnotation.class,
                CoreAnnotations.PartOfSpeechAnnotation.class
        )));
    }
}
