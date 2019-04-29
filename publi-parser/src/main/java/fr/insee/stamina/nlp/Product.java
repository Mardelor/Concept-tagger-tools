package fr.insee.stamina.nlp;

import java.sql.ResultSet;

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
    public Product(ResultSet resultSet) {
        // TODO : complete
    }
}
