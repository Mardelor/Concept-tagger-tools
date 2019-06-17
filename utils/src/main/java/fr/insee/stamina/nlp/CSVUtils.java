package fr.insee.stamina.nlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CSVUtils {

    private static int columns;

    public static String nerTag;

    public static void cleanCSV(Path file, Path target) {
        try {
            columns = Files.readAllLines(file).get(0).split("['\"]?,['\"]?").length;
            Stream<String> lines = Files.lines(file);
            Files.write(target, (Iterable<String>)lines
                    .skip(0)
                    .filter(filter)
                    .map(addNERTag)::iterator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Predicate<String> filter = line -> line.split("['\"]?,['\"]?").length <= columns;

    private static Function<String, String> addNERTag = line -> String.format("%s,%s", nerTag, line);
}
