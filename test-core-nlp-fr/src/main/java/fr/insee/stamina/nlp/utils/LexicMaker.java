package fr.insee.stamina.nlp.utils;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import fr.insee.stamina.nlp.Product;
import fr.insee.stamina.nlp.ProductExporter;
import fr.insee.stamina.nlp.S3FileManager;

import java.sql.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class LexicMaker {
    private static StanfordCoreNLP nlp;
    private static S3FileManager s3;

    public static void main(String args[]) throws Exception {
        // TODO construire le dico de lemme à partir du fichier pour sortir les mots n'ayant pas de lemme & peut-être plus

        ProductExporter exporter = ProductExporter.getInstance();
        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("src/main/resouces/auth-properties.props"));
        exporter.initialize(properties);

        s3 = S3FileManager.getInstance();
        properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
        properties.put("annotators","tokenize, ssplit, pos");
        nlp = new StanfordCoreNLP(properties);

        List<Product> products = exporter.getProducts(51);
        products.stream().filter(predicate).forEach(analyze);
    }

    private static Predicate<Product> predicate = product -> product.getDiffusionDate().before(Date.valueOf("2018-12-31"));

    private static Consumer<Product> analyze = product -> {
        // TODO : appeler du xslt qui appelle la methode du dessous !
    };

    public static void lemmatize(String text) {

    }
}
