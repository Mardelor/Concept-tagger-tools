package fr.insee.stamina.nlp;

public class PublicationParser {
    /**
     * singleton
     */
    private static PublicationParser instance;

    /**
     *
     */
    private PublicationParser() {

    }

    /**
     * Get the instance
     * @return  the PublicationParser instance
     */
    public static PublicationParser getInstance() {
        return instance;
    }
}
