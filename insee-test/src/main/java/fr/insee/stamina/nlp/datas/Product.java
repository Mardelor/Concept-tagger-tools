package fr.insee.stamina.nlp.datas;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
    private String id;
    private String path;
    private Date date;

    public Product(ResultSet resultSet) throws SQLException {
        this(resultSet.getString("idproduit"),
                resultSet.getString("path"),
                resultSet.getDate("datediffusion"));
    }

    public Product(String id, String path, Date date) {
        this.id = id;
        this.path = path;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public Date getDate() {
        return date;
    }
}
