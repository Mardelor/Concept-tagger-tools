package fr.insee.stamina.nlp;

import fr.insee.stamina.nlp.datas.Product;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Utilitaire pour tester le tagger insee sur un jeu de données
 */
public class LocalTest extends Test {

    private Path BO_PATH;

    public void init() throws IOException, SQLException, TransformerException, ClassNotFoundException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get("../data/props/test.props")));
        BO_PATH = Paths.get(properties.getProperty("bo.path"));
        super.init(properties);
    }

    @Override
    void process(Product product) throws TransformerException, IOException {
        Path input = Paths.get(String.format("%s/%s/%s", BO_PATH, product.getId(), product.getPath()));
        Path xmlOutput = Paths.get(String.format("../data/out/xml/%s.%s", product.getId(), product.getPath()));
        Path htmlOutput = Paths.get(String.format("../data/out/html/%s.%s.html", product.getId(), product.getPath().split(".xml")[0]));
        tagger.tag(input, xmlOutput);
        transformer.transform(new StreamSource(xmlOutput.toFile()), new StreamResult(htmlOutput.toFile()));
    }

    public static void main(String[] args) {
        LocalTest test = new LocalTest();
        try {
            test.init();
            test.run("SELECT * from bo.p_produit WHERE idfamille=51 AND datediffusion<'2019-01-01 00:00:00'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
