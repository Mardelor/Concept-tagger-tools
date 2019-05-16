package fr.insee.stamina.nlp;

import java.util.Comparator;

public class SimpleTokenComparator implements Comparator<String> {
    @Override
    public int compare(String s, String t1) {
         return s.split(" ").length - t1.split(" ").length;
    }
}
