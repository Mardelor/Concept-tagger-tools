package fr.insee.stamina.nlp;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class representing a product
 */
public class Product {

    /**
     * product id
     */
    private String id;

    /**
     * product title
     */
    private String title;

    /**
     * XML descriptor file path
     */
    private String xmlPath;

    /**
     * Construct a product from a query result
     * @param resultSet
     *              query result
     */
    public Product(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getString("idproduit");
        this.title = resultSet.getString("titre");
        this.xmlPath = resultSet.getString("path");
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", xmlPath='" + xmlPath + '\'' +
                '}';
    }
}
