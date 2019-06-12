package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Build rule file used by Stanford Core NLP to annotate documents
 */
public class TokensRegexBuilder {

    /**
     * this class is a singleton
     */
    private static TokensRegexBuilder instance          = null;

    /**
     * Stanford Core NLP pipeline used to generate lemma and pos annotation from labels
     */
    private StanfordCoreNLP pipeline;

    /**
     * Input file separator
     */
    private static String INPUT_SEPARATOR;

    /**
     * Properties namespace
     */
    public static final String PROP_NAMESPACE           = "rulesmaker";

    /**
     * Propterty name : input file separator
     */
    public static final String PROP_INPUT_SEPARATOR     = "inputseparator";

    /**
     * Core NLP adjective pattern
     */
    private static final String ADJ_PATTERN             = "[{tag:\"ADJ\"}]*";

    /**
     * Header files
     */
    private static final String RULE_HEADER             = "ner = { type: \"CLASS\", value: \"edu.stanford.nlp.ling.CoreAnnotations$NamedEntityTagAnnotation\" }\n" +
                                                          "tokens = { type: \"CLASS\", value: \"edu.stanford.nlp.ling.CoreAnnotations$TokensAnnotation\" }\n" +
                                                          "mention = { type: \"CLASS\", value: \"edu.stanford.nlp.ling.CoreAnnotations$MentionsAnnotation\" }\n\n";

    /**
     * Word pattern format
     */
    private static final String WORD_PATTERN_FORMAT     = "[{pos:\"%s\"} & {lemma:\"%s\"}]";

    /**
     * Tokensregex rule format
     */
    private static final String RULE_FORMAT             = "{ ruleType: \"tokens\", pattern: (%s), action: (Annotate($0, ner, \"%s\"), Annotate($0, mention, \"%s\")), result: \"%s\"}";

    /**
     * Build a TokensRegexBuilder from properties file
     * @param properties
     *              Stanford Core NLP property file
     */
    private TokensRegexBuilder(Properties properties) {
        pipeline = new StanfordCoreNLP(properties);
        INPUT_SEPARATOR = (String) properties.getOrDefault(String.format("%s.%s", PROP_NAMESPACE, PROP_INPUT_SEPARATOR), ",");
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
     * @throws TokensRegexBuilderException
     *              in cas of IOException
     */
    public void build(Path input, Path output) throws TokensRegexBuilderException {
        try(Stream<String> lines = Files.lines(input, StandardCharsets.UTF_8)) {
            Files.write(output, (RULE_HEADER).getBytes());
            Files.write(output, (Iterable<String>)lines.distinct().sorted(new ConceptTokenComparator()).map(buildRule)::iterator, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TokensRegexBuilderException("Fail to create rule file", e);
        }
    }

    /**
     * Transform a triple into a tokensregex
     * @param label
     *              entity label
     * @param id
     *              id
     * @param nerTag
     *              ner tag
     * @return  the tokensregex rule
     */
    private String getRule(String label, String id, String nerTag) {
        Annotation labelAnnotation = new Annotation(label);
        pipeline.annotate(labelAnnotation);

        String pattern = labelAnnotation.get(CoreAnnotations.TokensAnnotation.class).stream().map(buildWordPattern).reduce(buildPattern).orElse("");
        return String.format(RULE_FORMAT, pattern, nerTag, id, nerTag);
    }

    /**
     * Concatenate word patterns
     */
    private BinaryOperator<String> buildPattern = (r, s) -> String.format("%s%s%s", r, ADJ_PATTERN, s);

    /**
     * Build word pattern from Core NLP labels
     */
    private Function<CoreLabel, String> buildWordPattern = coreLabel -> String.format(WORD_PATTERN_FORMAT, coreLabel.tag(), coreLabel.lemma());

    /**
     * Transform named entity to tokensregex rule
     * @apiNote named entity can have multiple labels
     */
    private Function<String, String> buildRule = line -> {
        String[] items = line.split(INPUT_SEPARATOR);
        String nerTag = items[0];
        String id = items[1];
        String label = items[2];

        String rule = getRule(label, id, nerTag);

        if (items.length > 3) {
            for (int i=3; i<items.length; i++) {
                label = items[i];
                rule = String.format("%s\n%s", rule, getRule(label, id, nerTag));
            }
        }

        return rule;
    };

    /**
     * Gets the context instance
     * @return  the TokensRegexBuilder instance
     */
    public static TokensRegexBuilder instance() {
        return instance;
    }

    /**
     * Gets the context instance or create the one giving the properties
     * @param properties
     *              properties
     * @return  the TokensRegexBuilder instance
     */
    public static TokensRegexBuilder instance(Properties properties) {
        if (instance == null) {
            instance = new TokensRegexBuilder(properties);
        }
        return instance;
    }

    /**
     * Build rule file from csv file in args
     * @param args
     *              input file, output file
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage : <input file> <output file>");
        }
        try {
            Properties properties = new Properties();
            properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
            properties.put("annotators", "tokenize, ssplit, pos, custom.lemma");
            properties.setProperty("customAnnotatorClass.custom.lemma", "fr.insee.stamina.nlp.FrenchLemmaAnnotator");
            properties.setProperty("french.lemma.lemmaFile", "src/main/resources/lexique_fr.txt");

            TokensRegexBuilder builder = instance(properties);

            builder.build(Paths.get(args[0]), Paths.get(args[1]));
        } catch (IOException | TokensRegexBuilderException e) {
            e.printStackTrace();
        }
    }
}
