package fr.insee.stamina.nlp.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompareLexics {
    private static HashMap<String, Integer> lexic;
    private static Set<String> frenchLexic;
    private static Integer totalWords;
    private static Set<String> commonWords;

    public static void main(String args[]) throws IOException {
        String lexique = "src/main/resources/ip-lexique.txt";
        String frenchLexique = "src/main/resources/lexique_fr.txt";
        commonWords = new HashSet<>();

        frenchLexic = new HashSet<>();
        List<String> lines = Files.readAllLines(Paths.get(frenchLexique));
        for (String line : lines) {
            frenchLexic.add(line.split("\\t")[0]);
        }

        lexic = new HashMap<>();
        totalWords = 0;
        lines = Files.readAllLines(Paths.get(lexique));
        String word;
        Integer freq;
        for (String line : lines) {
            word = line.split("\\t")[0];
            freq = Integer.parseInt(line.split("\\t")[1]);
            lexic.put(word, freq);
            totalWords += freq;
            if (frenchLexic.contains(word)) {
                commonWords.add(word);
            }
        }

        System.out.println("Total Words : " + lexic.keySet().size() + ", Common Words : " + commonWords.size() +  ", Freq : " + (float) commonWords.size()/lexic.keySet().size());

        int commonWordsCount = 0;
        for (String key : lexic.keySet()) {
            if (commonWords.contains(key)) {
                commonWordsCount += lexic.get(key);
            }
        }

        System.out.println("Total Words : " + totalWords + ", Missing Words frequency : " + commonWordsCount + ", Freq : " + (float) commonWordsCount/totalWords);
    }
}
