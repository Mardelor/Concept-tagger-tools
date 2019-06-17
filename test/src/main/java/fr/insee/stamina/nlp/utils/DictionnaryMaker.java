package fr.insee.stamina.nlp.utils;

import edu.stanford.nlp.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 *
 */
public class DictionnaryMaker {

    private static HashMap<String, String> lemmaMap;

    public static void main(String args[]) {
        String input = "src/main/resources/concepts.csv";
        String lexique = "src/main/resources/lexique_fr.txt";
        String output = "src/main/resources/concepts-lemme.tsv";

        buildLemmaMap(lexique);
        try (Stream<String> lines = Files.lines(Paths.get(input))) {
            Files.write(Paths.get(output), (Iterable<String>)lines.skip(1).filter(filterLine).map(mapToItem)::iterator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Build lemmatiseur map
     * @param lemmaFile
     *              source file
     */
    private static void buildLemmaMap(String lemmaFile) {
        lemmaMap = new HashMap<>();
        List<String> lemmaEntries = IOUtils.linesFromFile(lemmaFile);
        for (String lemmaEntry : lemmaEntries) {
            lemmaMap.put(lemmaEntry.split("\\t")[0], lemmaEntry.split("\\t")[1]);
        }
    }

    private static boolean isLemme(String line) {
        int commaIndex = line.indexOf(',');
        String libelle = StringUtils.strip(line.substring(commaIndex + 1), "\"\u00A0").toLowerCase(Locale.FRENCH);

        Pattern spliter = Pattern.compile("[ ']");
        String words[] = spliter.split(libelle);
        for (String word : words) {
            if (!lemmaMap.containsKey(word)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Filter predicate for input lines.
     */
    private static Predicate<String> filterLine = line ->
            !Pattern.matches(".*[\\(\\)].*", line) &&
                    line.split(" ").length < 3 &&
                    isLemme(line);

    /**
     * Map an input line to a formatted line.
     */
    private static Function<String, String> mapToItem = line -> {
        // The first comma separates the concept URI from the label
        int commaIndex = line.indexOf(',');
        String conceptId = StringUtils.substringAfterLast(line.substring(0, commaIndex), "/");
        String libelle = StringUtils.strip(line.substring(commaIndex + 1), "\"\u00A0").toLowerCase(Locale.FRENCH);

        Pattern billy = Pattern.compile("[ ']");
        String words[] = billy.split(libelle);
        StringBuilder lemmeBuilder = new StringBuilder();
        for (String word : words) {
            lemmeBuilder.append(lemmaMap.get(word));
            lemmeBuilder.append(" ");
        }

        String lemmaLibelle = lemmeBuilder.toString().substring(0, lemmeBuilder.lastIndexOf(" "));
        return String.format("%s\t%s\t%s", conceptId, libelle, lemmaLibelle);
    };
}
