package fr.insee.stamina.nlp;

import org.apache.http.impl.client.CloseableHttpClient;

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
    private CloseableHttpClient httpClient;

    /**
     * postgre tokens
     */
    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;

    /**
     * Certification path
     */
    private String CERT_PATH;

    /**
     *
     */
    private ProductExporter() {}

    /**
     * Inittializes the exporter with properties
     * @param properties
     *              authentification properties
     */
    public void initialize(Properties properties) {
        this.DB_URL = properties.getProperty("jdbcURL");
        this.DB_USER = properties.getProperty("user");
        this.DB_PASSWORD = properties.getProperty("password");
        this.CERT_PATH = properties.getProperty("certificate");

        try {
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // TODO : initialize http client
    }

    /**
     * Close both PostgresSQL & Http clients
     */
    public void closeClients() {
        try {
            this.connection.close();
            // TODO : close http client
        } catch (SQLException e) {}
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
     * Gets an input stream of the XML descriptor refered by the id product
     * @param idProduct
     *              product id
     * @return  an input stream to read the xml descriptor
     */
    public InputStream getXMLDescriptor(String idProduct) {
        // TODO
        return null;
    }

    /**
     * Query the Postgre DB to find list of products ids which belongs the given family
     * @param idFamille
     *              family id
     * @return  list of product ids
     */
    public List<Product> getProducts(String idFamille) {
        ArrayList<Product> products = new ArrayList<>();
        Statement statement;
        ResultSet results;
        try {
            statement = connection.createStatement();
            results = statement.executeQuery("SELECT * FROM bo.p_produit WHERE idfamille=" + idFamille);
            while (results.next()) {
                Product product = new Product(results);
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // TODO : Complete with custom excpetions
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
