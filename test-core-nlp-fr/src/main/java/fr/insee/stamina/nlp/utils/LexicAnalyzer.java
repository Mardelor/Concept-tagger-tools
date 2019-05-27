package fr.insee.stamina.nlp.utils;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import fr.insee.stamina.nlp.Product;
import fr.insee.stamina.nlp.ProductExporter;
import fr.insee.stamina.nlp.S3FileManager;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LexicAnalyzer {
    private static StanfordCoreNLP nlp;
    private static S3FileManager s3;
    private static String BUCKET;
    private static Transformer transformer;
    private static HashMap<String, ArrayList<String>> lemmap;
    private static HashMap<String, String> posMap;

    public static void main(String args[]) throws Exception {
        // TODO construire le dico de lemme à partir du fichier pour sortir les mots n'ayant pas de lemme & peut-être plus
        ProductExporter exporter = ProductExporter.getInstance();
        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("src/main/resouces/auth-properties.props"));
        exporter.initialize(properties);
        BUCKET = System.getenv("BUCKET_ID");
        transformer = TransformerFactory.newInstance().newTransformer(new StreamSource("src/main/resources/text-extract.xsl"));

        s3 = S3FileManager.getInstance();
        properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
        properties.put("annotators","tokenize, ssplit, pos");
        nlp = new StanfordCoreNLP(properties);
        initLemmap();

        List<Product> products = exporter.getProducts(51);
        products.stream().filter(predicate).forEach(analyze);
    }

    private static void initLemmap() {
        lemmap = new HashMap<>();
        posMap = new HashMap<>();
        posMap.put("aux", "AUX");
        posMap.put("pre","PREP");
        posMap.put("nom","NOUN");
        posMap.put("adj","ADJ");
        posMap.put("adv","ADV");
        posMap.put("ver","VERB");
        posMap.put("det","DET");
        posMap.put("art_ind","ART");
        posMap.put("art_def","ART");
        posMap.put("con","CONJ");
        posMap.put("pro_rel","PRO");
        posMap.put("pro_ind","PRO");
        posMap.put("pro_per","PRO");
        posMap.put("pro_dem","PRO");
        try (Stream<String> lines = Files.lines(Paths.get("src/main/resources/lexique_fr.txt"))) {
            lines.forEach(fillMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Consumer<String> fillMap = line -> {
        String[] items = line.split("\t");
        ArrayList<String> list = new ArrayList<>();
        list.add(items[1]);
        list.add(posMap.get(items[2]));
        lemmap.put(items[0], list);
    };

    private static Predicate<Product> predicate = product -> product.getDiffusionDate().before(Date.valueOf("2018-12-31"));

    private static Consumer<Product> analyze = product -> {
        // TODO : appeler du xslt qui appelle la methode du dessous !
        try {
            InputStream input = s3.readObject(BUCKET, String.format("%s/publications/%s-%s", BUCKET, product.getId(), product.getXmlPath()));
            transformer.transform(
                    new StreamSource(input),
                    new StreamResult("src/main/resources/out.xml"));
        } catch (AmazonS3Exception e) {
            System.err.println(String.format("Missing file : %s (id : %s, date : %s)", product.getXmlPath(), product.getId(), product.getDiffusionDate()));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    };

    public static void lemmatize(String text) {
        Annotation annotation = new Annotation(text);
        nlp.annotate(annotation);

        for (CoreLabel coreLabel : annotation.get(CoreAnnotations.TokensAnnotation.class)) {

        }
    }
}
