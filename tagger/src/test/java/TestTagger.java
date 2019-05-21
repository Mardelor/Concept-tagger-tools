import fr.insee.stamina.nlp.FrenchNERPipeline;
import org.junit.Test;

public class TestTagger {
    @Test
    public void testPipeline() throws Exception {
        String text = "Et c'est un nouvel essai pour l'équipe NER.";
        FrenchNERPipeline.init();
        FrenchNERPipeline pipeline = new FrenchNERPipeline();

        System.out.println(pipeline.run(text));
    }
}
