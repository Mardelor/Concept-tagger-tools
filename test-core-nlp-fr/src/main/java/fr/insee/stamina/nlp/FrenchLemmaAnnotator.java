package fr.insee.stamina.nlp;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;

/**
 * French lemmatizer.
 * 
 * @author Franck
 */
public class FrenchLemmaAnnotator implements Annotator {

	Map<String, String> wordToLemma = new HashMap<String, String>();

	public FrenchLemmaAnnotator(String name, Properties properties) {
		// Load the lemma file; format should be TSV: {token}<tab>{lemma}
		String lemmaFile = properties.getProperty("french.lemma.lemmaFile");
		List<String> lemmaEntries = IOUtils.linesFromFile(lemmaFile);
		for (String lemmaEntry : lemmaEntries) {
			wordToLemma.put(lemmaEntry.split("\\t")[0], lemmaEntry.split("\\t")[1]);
		}
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
			CoreAnnotations.SentencesAnnotation.class,
			CoreAnnotations.PartOfSpeechAnnotation.class
		)));
	}
}
