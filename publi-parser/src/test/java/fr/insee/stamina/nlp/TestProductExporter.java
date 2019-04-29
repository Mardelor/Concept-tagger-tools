package fr.insee.stamina.nlp;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class TestProductExporter {

    public static ProductExporter exporter;

    @BeforeClass
    public static void setUp() throws IOException {
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
}
