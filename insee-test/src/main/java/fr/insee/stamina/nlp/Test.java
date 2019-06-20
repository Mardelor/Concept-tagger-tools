package fr.insee.stamina.nlp;

import fr.insee.stamina.nlp.datas.Product;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public abstract class Test {

    protected InseeTagger tagger;
    protected Connection sqldb;
    protected Transformer transformer;

    public void init(Properties properties) throws IOException, SQLException, TransformerException, ClassNotFoundException {
        tagger = new InseeTagger();
        tagger.init();
        sqldb = DriverManager.getConnection(
                properties.getProperty("sql.url"),
                properties.getProperty("sql.user"),
                properties.getProperty("sql.passwd")
        );
        transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream("/render.xsl")));
    }

    public void run(String query) throws SQLException, TransformerException, IOException {
        Statement statement = sqldb.createStatement();
        ResultSet results = statement.executeQuery(query);
        while(results.next()) {
            process(new Product(results));
        }
    }

    abstract void process(Product product) throws TransformerException, IOException;
}
