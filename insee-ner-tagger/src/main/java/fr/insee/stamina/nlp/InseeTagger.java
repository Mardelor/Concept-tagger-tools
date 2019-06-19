package fr.insee.stamina.nlp;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Demo using custom property file
 */
public class InseeTagger {

    /**
     * Stanford Core NLP french pipeline properties
     */
    private static final String INSEE_PROPS          = "/insee-tagger.props";

    /**
     * xsl web4g sheet
     */
    private static final String INSEE_XSL            = "/insee-tagger.xsl";

    /**
     * tokensregex insee rules
     */
    private static final String INSEE_RULES          = "insee-tagger.rules";

    /**
     * xslt transformer
     */
    private Transformer transformer;

    /**
     * initialize tagger
     * @throws TransformerException
     *              in case of xsl transformation exception
     * @throws IOException
     *              in case of props reading exception
     */
    public void init() throws TransformerException, IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream(INSEE_PROPS));
        properties.put("tokensregex.rules", INSEE_RULES);
        FrenchNERPipeline.init(properties);

        transformer = TransformerFactory
                .newInstance()
                .newTransformer(
                        new StreamSource(getClass().getResourceAsStream(INSEE_XSL)));
    }

    /**
     * tag a publication
     * @param input
     *              input file
     * @param output
     *              output file
     * @throws TransformerException
     *              in case of xsl transformation exception
     * @throws IOException
     *              in case of io problems
     */
    public void tag(Path input, Path output) throws TransformerException, IOException {
        this.tag(Files.newInputStream(input), Files.newOutputStream(output));
    }

    /**
     * tag a publication
     * @param input
     *              input stream
     * @param output
     *              output stream
     * @throws TransformerException
     *              in case of xsl transformation exception
     */
    public void tag(InputStream input, OutputStream output) throws TransformerException {
        transformer.transform(new StreamSource(input), new StreamResult(output));
    }

    /**
     * run tagger on one publication
     * @param args
     *              <input file> <output file>
     * @throws TransformerException
     *              in case of xslt transformation exception
     * @throws IOException
     *              in case of io exception
     */
    public static void main(String[] args) throws TransformerException, IOException {
        if (args.length != 2) {
            System.err.println("Usage : <input file> <output file>");
            return;
        }
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);
        InseeTagger tagger = new InseeTagger();
        tagger.init();
        tagger.tag(input, output);
    }
}
