package fr.insee.stamina.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Build rule file used by Stanford Core NLP to annotate documents
 */
public class TokensRegexRulesBuilder {

    /**
     * this class is a singleton
     */
    private static TokensRegexRulesBuilder instance = null;

    /**
     * Properties namespace
     */
    public static final String PROP_NAMESPACE           = "rulesmaker";

    /**
     * Propterty name : input file separator
     */
    public static final String PROP_INPUT_SEPARATOR     = "inputseparator";

    /**
     * Propterty name : ner tag
     */
    public static final String PROP_NER_TAG             = "nertag";

    /**
     * Core NLP adjective pattern
     */
    private static final String ADJ_PATTERN             = "[{tag:\"ADJ\"}]*";

    /**
     * Header files
     */
    private static final String RULE_HEADER             = "ner = { type: \"CLASS\", value: \"edu.stanford.nlp.ling.CoreAnnotations$NamedEntityTagAnnotation\" }\n" +
                                                            "tokens = { type: \"CLASS\", value: \"edu.stanford.nlp.ling.CoreAnnotations$TokensAnnotation\" }\n\n";
    /**
     * Default rule
     */
    private static final String DEFAULT_RULE            = "{ ruleType: \"tokens\", pattern: ([{word:\".*\"}]), action: Annotate($0, ner, \"O\"), result: \"O\" }\n\n";

    /**
     * Input file separator
     */
    private String INPUT_SEPARATOR = ",";

    /**
     * ner tag
     */
    private String NER_TAG;

    /**
     * Stanford Core NLP pipeline used to generate lemma and pos annotation from labels
     */
    private StanfordCoreNLP pipeline;

    /**
     * Initialize pipeline
     * @param properties
     *              Core NLP & specials properties
     * @throws TokensRegexRulesBuilderException
     *              in case of missing properties
     */
    public void init(Properties properties) throws TokensRegexRulesBuilderException {
        pipeline = new StanfordCoreNLP(properties);
        if (properties.containsKey(String.format("%s.%s", PROP_NAMESPACE, PROP_INPUT_SEPARATOR))) {
            INPUT_SEPARATOR = properties.getProperty(String.format("%s.%s", PROP_NAMESPACE, PROP_INPUT_SEPARATOR));
        }
        if (!properties.containsKey(String.format("%s.%s", PROP_NAMESPACE, PROP_NER_TAG))) {
            throw new TokensRegexRulesBuilderException(String.format("Property %s.%s required to build rules", PROP_NAMESPACE, PROP_NER_TAG));
        }
        NER_TAG = properties.getProperty(String.format("%s.%s", PROP_NAMESPACE, PROP_NER_TAG));
    }

    /**
     * Build the tokensregex rule file
     * Each line of the input file must contain named entity id and named entity label, separated by the specified separator
     * in the property file. By default, the input separator is a comma (csv file).
     * Output file format is Core NLP tokensregex format.
     * @param input
     *              input file
     * @param output
     *              output file
     * @throws TokensRegexRulesBuilderException
     *              in cas of IOException
     */
    public void build(Path input, Path output) throws TokensRegexRulesBuilderException {
        try{ Files.write(output, (RULE_HEADER + DEFAULT_RULE).getBytes()); } catch (IOException e) {
            throw new TokensRegexRulesBuilderException(String.format("Unable to open %s", output));
        }

        Stream<String> lines;
        try {
            lines = Files.lines(input, StandardCharsets.UTF_8);
            lines = lines.sorted(new SimpleTokenComparator());
        } catch (IOException e) {
            e.printStackTrace();
            throw new TokensRegexRulesBuilderException(String.format("Unable to open %s", input));
        }
        try { Files.write(output, (Iterable<String>) lines.map(buildRule)::iterator, StandardOpenOption.APPEND); } catch (IOException e) {
            e.printStackTrace();
            throw new TokensRegexRulesBuilderException(String.format("Unable to open %s", output));
        }
    }

    /**
     * Transform named entity to tokensregex rule
     */
    private Function<String, String> buildRule = line -> {
        String rule;
        String pattern;

        String[] items = line.split(INPUT_SEPARATOR);
        String id = items[0];
        String label = items[1];

        Annotation annotation = new Annotation(label);
        pipeline.annotate(annotation);

        ArrayList<String> lemmas = new ArrayList<>();
        ArrayList<String> poss = new ArrayList<>();
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                lemmas.add(token.lemma());
                poss.add(token.tag());
            }
        }
        pattern = buildPattern(lemmas, poss);

        rule = String.format("{ ruleType: \"tokens\", pattern: %s, action: Annotate($0, ner, \"%s\"), result: \"%s\" }", pattern, NER_TAG, NER_TAG);

        return rule;
    };

    /**
     * Build matching Core NLP Pattern based on lemmas and pos tags from one named entity.
     * @param lemmas
     *              list of lemmas
     * @param poss
     *              list of pos tags
     * @return  tokensregex pattern
     */
    private String buildPattern(ArrayList<String> lemmas, ArrayList<String> poss) {
        StringBuilder builder = new StringBuilder("(");
        builder.append(ADJ_PATTERN);
        builder.append(" ");
        for (int i=0; i<poss.size(); i++) {
            builder.append(String.format("[{lemma:\"%s\"} & {tag:\"%s\"}]%s",
                    lemmas.get(i).replace("\"", "\\\""), poss.get(i), ADJ_PATTERN));
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * Gets the context instance
     * @return  the TokensRegexRulesBuilder instance
     */
    public static TokensRegexRulesBuilder getInstance() {
        if (instance == null) {
            instance = new TokensRegexRulesBuilder();
        }
        return instance;
    }
}
