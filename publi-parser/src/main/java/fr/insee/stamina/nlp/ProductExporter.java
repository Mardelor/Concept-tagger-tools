package fr.insee.stamina.nlp;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
     * http token
     */
    private String HTTP_URL;
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
    public void initialize(Properties properties) throws Exception {
        this.DB_URL = properties.getProperty("jdbcURL");
        this.DB_USER = properties.getProperty("user");
        this.DB_PASSWORD = properties.getProperty("password");
        this.CERT_PATH = properties.getProperty("certificate");
        this.HTTP_URL = properties.getProperty("httpURL");

        this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        SSLConnectionSocketFactory sslSocketFactory;
        SSLContext sslContext;

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate serverCertificate =
                (X509Certificate) certificateFactory.generateCertificate(new FileInputStream(CERT_PATH));
        KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null); // Creates empty key store
        trustStore.setCertificateEntry("insee-bo", serverCertificate);

        // Add the key store to the HTTP client SSL context
        sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, null).build();
        sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

        // Initialize http client
        httpClient = HttpClients.custom().setSSLContext(sslContext).build();
    }

    /**
     * Close both PostgresSQL & Http clients
     */
    public void closeClients() {
        try {
            this.connection.close();
            this.httpClient.close();
        } catch (SQLException | IOException e) {
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
        HttpGet httpGet = new HttpGet(HTTP_URL+product.getId());
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        // the bo response with a redirection page, so we look for the url by parsing the http response
        String entityString = EntityUtils.toString(entity);
        int startURL = entityString.indexOf("url=") + 4;
        int endURL = entityString.indexOf("\">", startURL);
        String redirectURL = entityString.substring(startURL, endURL);

        HttpGet redirectGet = new HttpGet(redirectURL);
        CloseableHttpResponse redirectResponse = httpClient.execute(redirectGet);
        HttpEntity xmlDescriptor = redirectResponse.getEntity();

        EntityUtils.consumeQuietly(entity);
        response.close();

        return xmlDescriptor.getContent();
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
            // TODO : Complete with custom exceptions
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
