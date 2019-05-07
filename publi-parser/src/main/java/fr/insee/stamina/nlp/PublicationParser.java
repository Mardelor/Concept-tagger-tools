package fr.insee.stamina.nlp;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Handle publication parsing : for now, only XML files
 */
public class PublicationParser {
    /**
     * singleton
     */
    private static PublicationParser instance;

    /**
     * Document builder to parse XML files
     */
    private DocumentBuilder documentBuilder;

    /**
     * Transformer to parse & filter XML files
     */
    private Transformer transformer;

    /**
     * Build the document builder
     */
    private PublicationParser() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            this.documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize XML parser
     * @param properties
     *              properties file : must contain propery name "xsl"
     * @throws TransformerConfigurationException
     *              ...
     */
    public void initialize(Properties properties) throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        this.transformer = factory.newTransformer(new StreamSource(properties.getProperty("xsl")));
    }

    /**
     * Parse !
     * @param stream
     *              input stream
     * @return  all match texte
     * @throws TransformerException
     *              ...
     */
    public String parse(InputStream stream) throws TransformerException {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        this.transformer.transform(new StreamSource(stream), new StreamResult(s));
        // Delete XML header
        return s.toString().substring(38);
    }

    /**
     * Parse a XML file and format it into a map <\tag, content>
     * @param inputStream
     *              input stream from the file
     * @param rootTag
     *              root tag to parse from
     * @param tagToParse
     *              text tag to parse
     * @return  a map <\tag, content>
     * @throws SAXException
     *              in case of xml parsing problem
     * @throws IOException
     *              in case of I/O problems
     * @deprecated
     */
    public HashMap<String, String> parse(InputStream inputStream, String rootTag, String... tagToParse)
            throws SAXException, IOException {
        Document doc = documentBuilder.parse(inputStream);
        Node root = doc.getElementsByTagName(rootTag).item(0);

        HashMap<String, String> map = new HashMap<>();
        for (String tag : tagToParse) {
            map.put(tag, findTextTag(root, tag));
        }
        return map;
    }

    /**
     * Parse a XML file and format it into a map <\tag, content>
     * @param XMLPath
     *              XML file path
     * @param rootTag
     *              root tag to parse from
     * @param tagToParse
     *              text tag to parse
     * @return  a map <\tag, content>
     * @throws SAXException
     *              in case of xml parsing problem
     * @throws IOException
     *              in case of I/O problems
     * @deprecated
     */
    public HashMap<String, String> parse(String XMLPath, String rootTag, String... tagToParse)
            throws SAXException, IOException {
        return this.parse(new FileInputStream(XMLPath), rootTag, tagToParse);
    }

    /**
     * Recursive method to explore XML tree
     * @param root
     *              XML tree root
     * @param tag
     *              tag to find
     * @return  tag content in the given tree
     */
    private String findTextTag(Node root, final String tag) {
        NodeList children = root.getChildNodes();
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=0; i<children.getLength(); i++) {
            if (children.item(i).getNodeName().equals(tag)) {
                stringBuilder.append(children.item(i).getTextContent());
            } else {
                stringBuilder.append(findTextTag(children.item(i), tag));
            }
        }
        return stringBuilder.toString().replaceAll("\t", "");
    }

    /**
     * Get the instance
     * @return  the PublicationParser instance
     */
    public static PublicationParser getInstance() {
        if (instance == null) {
            instance = new PublicationParser();
        }
        return instance;
    }
}
