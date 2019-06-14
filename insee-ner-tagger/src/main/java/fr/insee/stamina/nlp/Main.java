package fr.insee.stamina.nlp;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Demo using custom property file
 */
public class Main {

    public static void main(String[] args) {
        Properties properties = new Properties();
        String xsl;
        String input;
        String output;

        if (args.length != 1) {
            System.err.println("Usage : tagger.jar <props file location>");
            return;
        }

        try {
            properties.load(Files.newInputStream(Paths.get(args[0])));
            FrenchNERPipeline.init(properties);

            xsl = properties.getProperty("xsl.sheet");
            input = properties.getProperty("xsl.input");
            output = properties.getProperty("xsl.output");
        } catch (Exception e) {
            System.err.println("Cannot load properies");
            e.printStackTrace();
            return;
        }

        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            Transformer transformer = factory.newTransformer(new StreamSource(xsl));
            transformer.transform(new StreamSource(input), new StreamResult(output));
        } catch (TransformerConfigurationException e) {
            System.err.println("Failed to parse xsl stylesheet");
            e.printStackTrace();
        } catch (TransformerException e) {
            System.err.println(String.format("Failed to transform input : %s", input));
            e.printStackTrace();
        }
    }
}
