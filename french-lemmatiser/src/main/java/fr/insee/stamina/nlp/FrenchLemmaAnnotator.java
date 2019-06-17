package fr.insee.stamina.nlp;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * French lemmatizer. Map words to lemma
 * 
 * @author Franck, rem
 */
public class FrenchLemmaAnnotator implements Annotator {

	/**
	 * map words to lemma
	 */
	private Map<String, String> wordToLemma = new HashMap<>();

	/**
	 *
	 */
	public FrenchLemmaAnnotator(String name, Properties properties) throws IOException {
		InputStream input;
		if (properties.containsKey("french.lemma.lemmaFile")) {
			input = Files.newInputStream(Paths.get(properties.getProperty("french.lemma.lemmaFile")));
		} else {
			input = getClass().getResourceAsStream("/lexicon/french-word-lemma.txt");
		}
		Function<String, String[]> split = line -> line.split("\\t");
		Consumer<String[]> fillmap = items -> wordToLemma.put(items[0], items[1]);
		new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
				.lines()
				.map(split)
				.forEach(fillmap);
	}

	@Override
	public void annotate(Annotation annotation) {
		for (CoreLabel token : annotation.get(CoreAnnotations.TokensAnnotation.class)) {
			String lemma = wordToLemma.getOrDefault(token.word(), token.word());
			token.set(CoreAnnotations.LemmaAnnotation.class, lemma);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
		return Collections.singleton(CoreAnnotations.LemmaAnnotation.class);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Set<Class<? extends CoreAnnotation>> requires() {
		return Collections.unmodifiableSet(new ArraySet<>(Arrays.asList(
			CoreAnnotations.TextAnnotation.class,
			CoreAnnotations.TokensAnnotation.class,
			CoreAnnotations.SentencesAnnotation.class
		)));
	}
}
