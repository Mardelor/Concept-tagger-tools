package fr.insee.stamina.nlp.utils;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.Pair;
import fr.insee.stamina.nlp.ProductExporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generate word \t pos file from corpus & POS pipeline
 */
public class LexicGenerator {

    /**
     * pos pipeline
     */
    private StanfordCoreNLP pipeline;

    /**
     * word, pos list
     */
    private Set<Pair<String, String>> lexic;

    /**
     * pos to lemmatize
     */
    private static List<String> pos;

    static {
        String[] posTab = {"ADJ", "ADP", "ADV", "AUX", "CCONJ", "DET", "INTJ", "NOUN", "NUM", "PART", "PRON", "SCONJ", "VERB"};
        pos = Arrays.asList(posTab);
    }

    /**
     * lexic generator
     */
    private LexicGenerator() throws IOException {
        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
        properties.put("annotators", "tokenize, ssplit, pos");

        pipeline = new StanfordCoreNLP(properties);
        lexic = new HashSet<>();
    }

    /**
     * update current lexic list
     * @param text
     *              source text
     */
    public void updateLexic(String text) {
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        Predicate<CoreLabel> filter = coreLabel -> pos.contains(coreLabel.tag());
        Consumer<CoreLabel> action = coreLabel -> lexic.add(new Pair<>(coreLabel.word(), coreLabel.tag()));
        annotation.get(CoreAnnotations.TokensAnnotation.class).stream().filter(filter).forEach(action);
    }

    /**
     * save current lexic
     * @param target
     *              target
     */
    public void saveLexic(Path target) {
        try {
            Files.write(target, (Iterable<String>) lexic.stream().map(printLexic).sorted()::iterator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Function<Pair<String, String>, String> printLexic = pair -> String.format("%s\t%s", pair.first, pair.second);

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get("src/main/resources/auth.props")));

        ProductExporter exporter = ProductExporter.getInstance();
        exporter.initialize(properties);

        // TODO : update exporter to test on a large amount of datas
    }
}
