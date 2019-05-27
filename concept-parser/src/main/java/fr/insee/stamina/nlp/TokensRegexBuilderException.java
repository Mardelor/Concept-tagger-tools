package fr.insee.stamina.nlp;

/**
 * Custom exception
 */
public class TokensRegexBuilderException extends Exception {
    public TokensRegexBuilderException(String s) {
        super(s);
    }
    public TokensRegexBuilderException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
