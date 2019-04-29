package fr.insee.stamina.nlp;

import org.apache.http.impl.client.CloseableHttpClient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ProductExporter {
    /**
     * singleton
     */
    private static ProductExporter instance;

    /**
     * DB client
     */
    private Connection connection;

    /**
     * HTTP Client
     */
    // private CloseableHttpClient httpClient;

    /**
     * postgre tokens
     */
    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;

    /**
     * http token
     */
    // private String HTTP_URL;
    // private String CERT_PATH;

    /**
     * XML descritpor products path
     */
    private String XML_PRODUCTS_PATH;

    /**
     *
     */
    private ProductExporter() {}

    /**
     * Inittializes the exporter with properties
     * @param properties
     *              authentification properties
     */
    public void initialize(Properties properties) throws SQLException {
        this.DB_URL = properties.getProperty("jdbcURL");
        this.DB_USER = properties.getProperty("user");
        this.DB_PASSWORD = properties.getProperty("password");
        // this.CERT_PATH = properties.getProperty("certificate");
        // this.HTTP_URL = properties.getProperty("httpURL");
        this.XML_PRODUCTS_PATH = properties.getProperty("boPath");

        this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        // TODO : retry with http
    }

    /**
     * Close both PostgresSQL & Http clients
     */
    public void closeClients() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Download all product XML descriptor file from a family
     * @param idFamille
     *              family id
     */
    public void downloadXMLDescriptors(String idFamille) {
        // TODO
    }

    /**
     * Download product XML descriptor file from its product id
     * @param idProduct
     *              product id
     */
    public void downloadXMLDescriptor(String idProduct) {
        // TODO
    }

    /**
     * Gets an input stream of the XML descriptor of a product
     * @param product
     *              product
     * @return  an input stream to read the xml descriptor
     */
    public InputStream getXMLDescriptor(Product product) throws IOException {
        return new FileInputStream(String.format("%s/%s/%s", XML_PRODUCTS_PATH, product.getId(), product.getXmlPath()));
    }

    /**
     * Query the Postgre DB to find list of products ids which belongs the given family
     * @param idFamille
     *              family id
     * @return  list of product ids
     */
    public List<Product> getProducts(String idFamille) throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        Statement statement;
        ResultSet results;
        statement = connection.createStatement();
        results = statement.executeQuery("SELECT * FROM bo.p_produit WHERE idfamille=" + idFamille);
        while (results.next()) {
            Product product = new Product(results);
            products.add(product);
        }

        return products;
    }

    /**
     * @return  instance
     */
    public static ProductExporter getInstance() {
        if (instance == null) {
            instance = new ProductExporter();
        }
        return instance;
    }
}
