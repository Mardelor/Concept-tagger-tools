package fr.insee.stamina.nlp;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;

import java.util.Properties;

public class CRFClassifierMaker {

    public static void main(String arg[]) {
        String modelOutPath = "src/main/resources/output/model.fr.ser.gz";
        String trainingFilepath = "src/main/resources/data/training-data.tsv";

        Properties props = new Properties();
        props.setProperty("serializeTo", modelOutPath);
        props.setProperty("trainFile", trainingFilepath);

        SeqClassifierFlags flags = new SeqClassifierFlags(props);
        CRFClassifier<CoreLabel> crf = new CRFClassifier<>(flags);
        crf.train();
        crf.serializeClassifier(modelOutPath);
    }
}
