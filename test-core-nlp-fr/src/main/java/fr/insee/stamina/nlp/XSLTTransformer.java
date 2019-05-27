package fr.insee.stamina.nlp;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XSLTTransformer {

    public static void main(String[] args) {
        String styleSheet = "src/main/resources/publication-transformer.xsl";
        String web = "src/main/resources/web-rendering.xsl";
        String input = "src/main/resources/ip1174.xml";
        String output = String.format("%s.result.xml", input.substring(0, input.length()-4));
        String webout = String.format("%s.result.html", input.substring(0, input.length()-4));

        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            Transformer transformer = factory.newTransformer(new StreamSource(styleSheet));
            transformer.transform(new StreamSource(input), new StreamResult(output));

            transformer = factory.newTransformer(new StreamSource(web));
            transformer.transform(new StreamSource(output), new StreamResult(webout));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            System.out.println("Failed to parse xsl stylesheet");
        } catch (TransformerException e) {
            e.printStackTrace();
            System.out.println(String.format("Failed to transform input : %s", input));
        }
    }
}
