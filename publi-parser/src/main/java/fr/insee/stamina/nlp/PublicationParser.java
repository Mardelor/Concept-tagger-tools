package fr.insee.stamina.nlp;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
     * Parse a XML file and format it into a map <\tag, content>
     * @param xmlFilePath
     *              file path of the publication
     * @return  a map <\tag, content>
     */
    public HashMap<String, String> parse(String xmlFilePath, String rootTag, ArrayList<String> tagToParse)
            throws SAXException, IOException {
        Document doc = documentBuilder.parse(xmlFilePath);
        Node root = doc.getElementsByTagName(rootTag).item(0);

        HashMap<String, String> map = new HashMap<>();
        for (String tag : tagToParse) {
            map.put(tag, findTextTag(root, tag));
        }
        return map;
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
