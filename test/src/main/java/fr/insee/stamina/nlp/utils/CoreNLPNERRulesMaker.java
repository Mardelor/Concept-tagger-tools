package fr.insee.stamina.nlp.utils;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CoreNLPNERRulesMaker {

    private static HashMap<String, String> posMapper;
    private static String ADJ_RULE = "[{tag:\"ADJ\"}]* ";
    private static final Integer SEP_POS = 45;
    private static StanfordCoreNLP pipeline;

    public static void main(String args[]) throws IOException {
        String input = "src/main/resources/concepts.csv";
        String output = "src/main/resources/concepts.rules";

        // buildPosMapper();
        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
        properties.put("annotators", "tokenize, ssplit, pos, custom.lemma");

        properties.setProperty("tokenize.options", "untokenizable=noneDelete");
        properties.setProperty("customAnnotatorClass.custom.lemma", "fr.insee.stamina.nlp.lemma.FrenchLemmaAnnotator");
        properties.setProperty("french.lemma.lemmaFile", "src/main/resources/lexique_fr.txt");
        properties.setProperty("encoding", "UTF-8");

        pipeline = new StanfordCoreNLP(properties);

        Files.write(Paths.get(output), (
                "ner = { type: \"CLASS\", value: \"edu.stanford.nlp.ling.CoreAnnotations$NamedEntityTagAnnotation\" }\n" +
                "tokens = { type: \"CLASS\", value: \"edu.stanford.nlp.ling.CoreAnnotations$TokensAnnotation\" }\n" +
                        "mention = { type: \"CLASS\", value: \"edu.stanford.nlp.ling.CoreAnnotations$MentionsAnnotation\" }\n\n" +
                "{ ruleType: \"tokens\", pattern: ([{word:/.+/}]), action: (Annotate($0, ner, \"O\"), Annotate($0, mention, \"O\")), result: \"O\"}\n\n").getBytes());
        try (Stream<String> lines = Files.lines(Paths.get(input))) {
            Files.write(Paths.get(output), (Iterable<String>)lines.skip(1).sorted(new ConceptComparator()).map(mapToItem)::iterator, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void buildPosMapper() {
        posMapper = new HashMap<>();
        posMapper.put("nom", "NOUN");
        posMapper.put("adj", "ADJ");
        posMapper.put("ver", "VBD");
        posMapper.put("pre", "PREP");
        posMapper.put("aux", "AUX");
        posMapper.put("pro_ind", "NOUN");
        posMapper.put("nom_sup", "NOUN");
    }

    /**
     * Filter predicate for input lines.
     */
    private static Predicate<String> filterLine = line -> line.split(",").length > 3;

    /**
     * Map an input line to a formatted line.
     */
    private static Function<String, String> mapToItem = line -> {
        String[] items = line.replaceAll("\"", "").split(",");
        String uri = items[0];
        String label = items[1].toLowerCase().replaceAll("(\\(.*\\))", "");

        System.out.println(label);

        Annotation annotation = new Annotation(label);
        pipeline.annotate(annotation);

        StringBuilder tokenRule = new StringBuilder("(" + ADJ_RULE);
        for (CoreLabel cLabel: annotation.get(CoreAnnotations.TokensAnnotation.class)) {
            tokenRule.append(String.format("[{tag:\"%s\"} & {lemma:\"%s\"}] %s",
                    cLabel.tag(), cLabel.lemma().replace("\"", "\\\""), ADJ_RULE));
        }
        String subRule = tokenRule.toString().substring(0, tokenRule.lastIndexOf(" ")) + ")";
        String rule = String.format("{ ruleType: \"tokens\", pattern: %s, action: (Annotate($0, ner, \"%s\"), Annotate($0, mention, \"%s\t%s\")), result: \"%s\" }",
                subRule, "STAT-CPT", label.replace("\"", "\\\""), uri, "STAT-CPT");

        if (items.length > 2) {
            String accronym = items[2];

            System.out.println(accronym);

            Annotation annAccro = new Annotation(accronym);
            pipeline.annotate(annAccro);

            StringBuilder AccroTokenRule = new StringBuilder("(" + ADJ_RULE);
            for (CoreLabel cLabel: annAccro.get(CoreAnnotations.TokensAnnotation.class)) {
                AccroTokenRule.append(String.format("[{tag:\"%s\"} & {lemma:\"%s\"}] %s",
                        cLabel.tag(), cLabel.lemma().replace("\"", "\\\""), ADJ_RULE));
            }
            String AccroSubRule = AccroTokenRule.toString().substring(0, AccroTokenRule.lastIndexOf(" ")) + ")";
            rule = String.format("%s\n{ ruleType: \"tokens\", pattern: %s, action: (Annotate($0, ner, \"%s\"), Annotate($0, mention, \"%s\t%s\")), result: \"%s\" }",
                    rule, AccroSubRule, "STAT-CPT", label.replace("\"", "\\\""), uri, "STAT-CPT");
        }
        return rule;
    };
}
