package fr.insee.stamina.nlp;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XSLT {

    private Transformer transformer;

    public XSLT() throws TransformerException {
        transformer = TransformerFactory
                .newInstance()
                .newTransformer(
                        new StreamSource(getClass().getResourceAsStream("/insee-text.xsl")));
    }

    public void transform(InputStream input, OutputStream output) throws TransformerException {
        transformer.transform(new StreamSource(input), new StreamResult(output));
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage : <input file> <output file>");
            return;
        }
        try {
            XSLT xslt = new XSLT();
            xslt.transform(Files.newInputStream(Paths.get(args[0])), Files.newOutputStream(Paths.get(args[1])));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
