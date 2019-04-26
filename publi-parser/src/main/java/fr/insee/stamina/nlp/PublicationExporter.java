package fr.insee.stamina.nlp;

import java.io.InputStream;
import java.util.List;

public class PublicationExporter {
    /**
     * singleton
     */
    private static PublicationExporter instance;

    /**
     *
     */
    private PublicationExporter() {
        // TODO : complete using properties file
    }

    /**
     * Download all product XML descriptor file from a family
     * @param idFamille
     *              family id
     */
    public void downloadXMLDescriptors(String idFamille) {
        List<String> productIds = this.getProductIds(idFamille);
        for (String id : productIds) downloadXMLDescriptor(id);
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
    public List<String> getProductIds(String idFamille) {
        // TODO
        return null;
    }
}
