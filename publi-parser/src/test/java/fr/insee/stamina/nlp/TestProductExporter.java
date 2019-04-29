package fr.insee.stamina.nlp;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
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
        List<Product> products = exporter.getProducts("51");

        Assert.assertNotNull(products);
        Assert.assertTrue(products.size() > 630);
    }

    @Test
    public void testXMLDescriptor() throws Exception {
        InputStream stream = exporter.getXMLDescriptor(new Product("2891810", "titre", "ip1657"));

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        do {
            line = reader.readLine();
            System.out.println(line);
        } while (line != null && !line.equals(""));
    }
}
