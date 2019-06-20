import fr.insee.stamina.nlp.InseeTagger;
import org.junit.Before;

public class TestInseeTagger {

    private InseeTagger tagger;

    @Before
    public void setUp() throws Exception {
        tagger = new InseeTagger();
        tagger.init();
    }
}
