package fr.insee.stamina.nlp.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

public class CSVCleaner {

    private static String NER_TAG;

    public static Function<String, String> simpleRule = line -> String.format("%s,\"%s\"", NER_TAG, line.split("['\"]?,['\"]?")[1]);

    public static void setNerTag(String nerTag) {
        NER_TAG = nerTag;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage : <ner tag> <input> <output>");
            return;
        }
        NER_TAG = args[0];
        String input = args[1];
        String output = args[2];
        try(Stream<String> lines = Files.lines(Paths.get(input))) {
            Files.write(Paths.get(output), (Iterable<String>) lines.skip(1).map(simpleRule)::iterator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
