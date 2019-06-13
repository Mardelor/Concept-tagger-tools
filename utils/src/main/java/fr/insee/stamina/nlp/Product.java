package fr.insee.stamina.nlp;

import java.sql.Date;
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
     * Embargo date
     */
    private Date diffusionDate;

    /**
     * Construct product
     * @param id
     *              product id
     * @param title
     *              product title
     * @param xmlPath
     *              product xml path descriptor
     */
    public Product(String id, String title, String xmlPath, Date diffusionDate) {
        this.id = id;
        this.title = title;
        this.xmlPath = xmlPath;
        this.diffusionDate = diffusionDate;
    }

    /**
     * Construct a product from a query result
     * @param resultSet
     *              query result
     */
    public Product(ResultSet resultSet) throws SQLException {
        this(resultSet.getString("idproduit"),
                resultSet.getString("titre"),
                resultSet.getString("path"),
                resultSet.getDate("datediffusion"));
    }

    /**
     * @return  product unique id
     */
    public String getId() {
        return id;
    }

    /**
     * @return  product title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return  product xml descriptor file path
     */
    public String getXmlPath() {
        return xmlPath;
    }

    /**
     * @return  product embargo date
     */
    public Date getDiffusionDate() {
        return diffusionDate;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", xmlPath='" + xmlPath + '\'' +
                ", embargoDate=" + diffusionDate +
                '}';
    }
}
