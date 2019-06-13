package fr.insee.stamina.nlp;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import edu.stanford.nlp.io.IOUtils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class IPTest {

    private static Transformer nlp;

    private static Transformer web;

    private static S3FileManager s3;

    private static String BUCKET;

    public static void main(String[] args) {
        String styleSheet = "src/main/resources/publication-transformer.xsl";
        String webSheet = "src/main/resources/web-rendering.xsl";

        TransformerFactory factory = TransformerFactory.newInstance();
        s3 = S3FileManager.getInstance();
        ProductExporter exporter = ProductExporter.getInstance();
        BUCKET = System.getenv("BUCKET_ID");
        Properties properties = new Properties();

        try {
            nlp = factory.newTransformer(new StreamSource(styleSheet));
            web = factory.newTransformer(new StreamSource(webSheet));

            properties.load(IOUtils.readerFromString("src/main/resources/auth-properties.props"));
            exporter.initialize(properties);

            List<Product> products = exporter.getProducts(51);
            products.stream().filter(productFilter).forEach(analyze);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            System.out.println("Failed to parse xsl stylesheet");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Predicate<Product> productFilter = product ->
            product.getDiffusionDate().before(Date.valueOf("2018-12-31")) &&
                    product.getDiffusionDate().after(Date.valueOf("2018-01-01"));

    public static Consumer<Product> analyze = product -> {
        try {
            InputStream input = s3.readObject(BUCKET,
                    String.format("%s/publications/xml/%s-%s", BUCKET, product.getId(), product.getXmlPath()));
            String interOutput = String.format("src/main/resources/data/%s.result.xml", product.getXmlPath().substring(0, product.getXmlPath().length() - 4));
            String output = String.format("src/main/resources/html/%s.html", product.getXmlPath().substring(0, product.getXmlPath().length() - 4));

            try {
                nlp.transform(
                        new StreamSource(input),
                        new StreamResult(interOutput));
                web.transform(
                        new StreamSource(interOutput),
                        new StreamResult(output)
                );
            } catch (TransformerException e) {
                e.printStackTrace();
            }

            s3.putObject(BUCKET, Paths.get(output), String.format("%s/results/%s.html", BUCKET, product.getXmlPath().substring(0, product.getXmlPath().length() - 4)));
        } catch (AmazonS3Exception e) {
            // e.printStackTrace();
            System.err.println("This file doesn't exist ! " + product.getId() + " - " + product.getXmlPath() + " (Date :" + product.getDiffusionDate() + ")");
        }
    };
}
