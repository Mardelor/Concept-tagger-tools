import com.amazonaws.services.s3.model.AmazonS3Exception;
import fr.insee.stamina.nlp.ExportMode;
import fr.insee.stamina.nlp.Product;
import fr.insee.stamina.nlp.ProductExporter;
import fr.insee.stamina.nlp.PublicationParser;

import org.junit.Test;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class TestTagger {
    @Test
    public void test() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/auth.props"));

        ProductExporter productExporter = ProductExporter.getInstance();
        PublicationParser parser = PublicationParser.getInstance();
        productExporter.initialize(properties);

        List<Product> products = productExporter.getProducts(51);
        for (Product product : products) {
            try {
                System.out.println(parser.parse(productExporter.getXMLDescriptor(product, ExportMode.S3_FS),
                        "publication-sans-sommaire",
                        "titre", "chapo"));
            } catch (AmazonS3Exception e) {
                // Ignore
            }
        }

        // TODO : Complete & update product exporter to support s3
    }
}
