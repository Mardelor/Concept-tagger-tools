package fr.insee.stamina.nlp;

import org.junit.*;

import javax.xml.transform.Transformer;
import java.io.*;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestProductExporter {

    public static ProductExporter exporter;

    @BeforeClass
    public static void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/auth.props"));

        exporter = ProductExporter.getInstance();
        exporter.initialize(properties);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        exporter.closeClients();
    }

    @Test
    public void testSQLProducts() throws Exception {
        List<Product> products = exporter.getProducts(51);

        Assert.assertNotNull(products);
        Assert.assertTrue(products.size() > 630);
    }

    @Test
    public void testXMLDescriptorLocal() throws Exception {
        InputStream stream = exporter.getXMLDescriptor(
                new Product("2891810", "titre", "ip1657.xml"), ExportMode.LOCAL_FS);

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", reader.readLine());
        Assert.assertEquals("<publication-sans-sommaire afficher-sommaire=\"true\" afficher-sommaire-documentation=\"false\">", reader.readLine());
        Assert.assertEquals("<titre>L’industrie manufacturière en 2016</titre>", reader.readLine());
    }

    @Test
    public void testXMLDescriptorS3() throws Exception {
        InputStream stream = exporter.getXMLDescriptor(
                new Product("2891810", "titre", "ip1657.xml"), ExportMode.S3_FS);

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", reader.readLine());
        Assert.assertEquals("<publication-sans-sommaire afficher-sommaire=\"true\" afficher-sommaire-documentation=\"false\">", reader.readLine());
        Assert.assertEquals("    <titre>L’industrie manufacturière en 2016</titre>", reader.readLine().replace("\t", ""));
    }

    @Test
    public void testXMLDescriptorFam() throws Exception {
        S3FileManager s3 = S3FileManager.getInstance();
        ResultSet results = exporter.execute("SELECT * FROM bo.p_produit WHERE idfamille=51 AND datediffusion<'2019-01-01 00:00:00' AND langue='fr'");

        Product product;
        while (results.next()) {
            product = new Product(results);
            s3.copyObjectToFileSystem(System.getenv("BUCKET_ID"),
                    String.format("%s/publications/xml/%s-%s", System.getenv("BUCKET_ID"), product.getId(), product.getXmlPath()),
                    Paths.get(String.format("src/test/resources/data/%s", product.getXmlPath())));
        }
    }
}
