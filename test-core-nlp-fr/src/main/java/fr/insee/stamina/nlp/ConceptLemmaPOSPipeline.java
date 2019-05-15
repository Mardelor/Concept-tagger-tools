package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

public class ConceptLemmaPOSPipeline {

    private static StanfordCoreNLP pipeline;

    public static void main(String args[]) throws IOException {
        String input = "src/main/resources/concepts-lemme.tsv";
        String output = "src/main/resources/concepts-lemme-pos.tsv";

        setUpPipeline();
        try (Stream<String> lines = Files.lines(Paths.get(input))) {
            Files.write(Paths.get(output), (Iterable<String>)lines.map(mapToItem)::iterator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setUpPipeline() throws IOException {
        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));

        properties.put("annotators", "tokenize, ssplit, pos");

        properties.setProperty("tokenize.options", "untokenizable=noneDelete");
        properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
        properties.setProperty("encoding", "UTF-8");

        pipeline = new StanfordCoreNLP(properties);
    }

    private static Function<String, String> mapToItem = line -> {
        String pos;
        String[] items = line.split("\\t");
        Annotation annotation = new Annotation(items[1]);

        pipeline.annotate(annotation);

        StringBuilder builder = new StringBuilder();
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                builder.append(token.tag());
                builder.append(" ");
            }
        }
        pos = builder.toString();

        return String.format("%s\t%s\t%s\t%s", items[0], items[1], items[2], pos);
    };
}
