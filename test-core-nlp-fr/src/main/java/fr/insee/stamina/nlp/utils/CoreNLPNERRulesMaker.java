package fr.insee.stamina.nlp.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CoreNLPNERRulesMaker {

    private static HashMap<String, String> posMapper;
    private static String WORD = "/[A-Za-zÀ-ÿ]+/";
    private static String ADJ_RULE = "[{tag:\"ADJ\"}]* ";

    public static void main(String args[]) throws IOException {
        String input = "src/main/resources/concepts-lemme.tsv";
        String output = "src/main/resources/concepts.rules";

        buildPosMapper();
        Files.write(Paths.get(output), ("ner = { type: \"CLASS\", value: \"edu.stanford.nlp.ling.CoreAnnotations$NamedEntityTagAnnotation\" }\n" +
                "tokens = { type: \"CLASS\", value: \"edu.stanford.nlp.ling.CoreAnnotations$TokensAnnotation\" }\n\n" +
                "{ ruleType: \"tokens\", pattern: ([{word:/.+/}]), action: Annotate($0, ner, \"O\"), result: \"O\"}\n\n").getBytes());
        try (Stream<String> lines = Files.lines(Paths.get(input))) {
            Files.write(Paths.get(output), (Iterable<String>)lines.skip(1).filter(filterLine).map(mapToItem)::iterator, StandardOpenOption.APPEND);
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
    private static Predicate<String> filterLine = line -> true;

    /**
     * Map an input line to a formatted line.
     */
    private static Function<String, String> mapToItem = line -> {
        String tags[] = line.split("\\t");
        String ner = tags[2];
        String lemmaLibelle = tags[0];
        String pos = tags[1];

        StringBuilder tokensRule = new StringBuilder("(" + ADJ_RULE);
        for (int i=0; i<pos.split(" ").length; i++) {
            tokensRule.append(String.format("[{lemma:\"%s\"} & {tag:\"%s\"}] ", lemmaLibelle.split(" ")[i], posMapper.get(pos.split(" ")[i])));
            tokensRule.append(ADJ_RULE);
        }
        String subRule = tokensRule.toString().substring(0, tokensRule.lastIndexOf(" ")) + ")";
        String rule = String.format("{ ruleType: \"tokens\", pattern: %s, action: Annotate($0, ner, \"%s\"), result: \"%s\" }", subRule, ner, ner);

        return rule;
    };
}
