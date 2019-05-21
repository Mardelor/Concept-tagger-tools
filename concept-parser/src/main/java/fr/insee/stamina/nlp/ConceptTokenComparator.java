package fr.insee.stamina.nlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Comparator;

public class ConceptTokenComparator implements Comparator<String> {

    private static StanfordCoreNLP pipeline;

    @Override
    public int compare(String s, String t1) {
        // TODO : count tokens in concept libelle to order it
        return s.split(" ").length - t1.split(" ").length;
    }
}
