package fr.insee.stamina.nlp;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String styleSheet = "src/main/resources/publication-transformer.xsl";
        String input = "src/main/resources/data/ip1174.xml";
        String output = String.format("%s.result.xml", input.substring(0, input.length() - 4));

        FrenchNERPipeline.init();

        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            Transformer transformer = factory.newTransformer(new StreamSource(styleSheet));
            transformer.transform(new StreamSource(input), new StreamResult(output));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            System.out.println("Failed to parse xsl stylesheet");
        } catch (TransformerException e) {
            e.printStackTrace();
            System.out.println(String.format("Failed to transform input : %s", input));
        }
    }
}
