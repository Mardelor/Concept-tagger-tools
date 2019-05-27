package fr.insee.stamina.nlp;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class gathering PostgreSQL client & s3client in order to fetch data from remote DB
 * TODO : add http client
 */
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
     * S3 client
     */
    private S3FileManager s3FileManager;

    /**
     * postgre tokens
     */
    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;

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
        this.XML_PRODUCTS_PATH = properties.getProperty("boPath");

        this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        this.s3FileManager = S3FileManager.getInstance();
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
     * @param target
     *              file destination
     */
    public void downloadXMLDescriptors(String idFamille, Path target) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(target);
        // TODO
    }

    /**
     * Download product XML descriptor file from its product id
     * @param product
     *              product
     * @param target
     *              file destination
     */
    public void downloadXMLDescriptor(Product product, Path target) throws IOException {
        InputStream stream = s3FileManager.readObject(DB_USER, String .format("publications/xml/%s-%s", product.getId(), product.getXmlPath()));
        Files.copy(stream, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Gets an input stream of the XML descriptor of a product
     * @param product
     *              product
     * @return  an input stream to read the xml descriptor
     */
    public InputStream getXMLDescriptor(Product product, ExportMode mode) throws IOException {
        if (mode.equals(ExportMode.LOCAL_FS)) {
            return new FileInputStream(String.format("%s/%s/%s", XML_PRODUCTS_PATH, product.getId(), product.getXmlPath()));
        } else if (mode.equals(ExportMode.S3_FS)) {
            return s3FileManager.readObject(DB_USER, String.format("publications/xml/%s-%s", product.getId(), product.getXmlPath()));
        } else {
            return null;
        }
    }

    /**
     * Query the Postgre DB to find list of products ids which belongs the given family
     * @param idFamille
     *              family id
     * @return  list of product ids
     */
    public List<Product> getProducts(int idFamille) throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        String query = "SELECT * FROM bo.p_produit WHERE idfamille=" + idFamille + "AND langue=fr";
        ResultSet results = this.execute(query);
        while (results.next()) {
            Product product = new Product(results);
            products.add(product);
        }
        return products;
    }

    /**
     * Query the Postgre DB
     * @param query
     *              SQL query
     * @return  ResultSet object
     * @throws SQLException
     *              ...
     */
    public ResultSet execute(String query) throws SQLException {
        Statement statement;
        ResultSet results;
        statement = connection.createStatement();
        results = statement.executeQuery(query);
        return results;
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
