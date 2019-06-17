package fr.insee.stamina.nlp.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class LexicMaker {
    private static TreeMap<String, Integer> lexique;
    private static Pattern spliter = Pattern.compile("[ '\\u0019\n\\.;,\\(\\)\t\\u00A0[0-9]+!\\+’]");

    public static void main(String args[]) throws Exception {
        String inputFile = "src/main/resources/ip-texte.txt";
        String output = "src/main/resources/ip-lexique.txt";
        lexique = new TreeMap<>();
        for (String line : Files.readAllLines(Paths.get(inputFile), StandardCharsets.UTF_8)) {
            buildLexic(line);
        }
        writeLexic(Paths.get(output));
    }

    private static void buildLexic(String text) {
        String[] words = spliter.split(text.toLowerCase(Locale.FRENCH));
        for (String word : words) {
            if (Pattern.matches("[A-Za-z\\-À-ÿ]+", word)) {
                lexique.put(word, lexique.getOrDefault(word, 0) + 1);
            }
        }
    }

    private static void writeLexic(Path target) throws IOException {
        Files.deleteIfExists(target);
        Files.createFile(target);
        for (String key : lexique.keySet()) {
            Files.write(target, String.format("%s\t%d\n", key, lexique.get(key)).getBytes(), StandardOpenOption.APPEND);
        }
    }
}
