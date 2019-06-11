package fr.insee.stamina.nlp;

import org.junit.Before;

public class TestTokensRegexBuilder {

    public TokensRegexBuilder builder;

    public static final String RESOURCES = "src/test/resources/";

    @Before
    public void setUp() {
        builder = TokensRegexBuilder.instance();
    }

    // TODO : add tests
}
