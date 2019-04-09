package fr.insee.stamina.nlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * Pipeline for named entity recognition in French.
 * 
 * Adapted from http://www.erwanlenagard.com/general/tutoriel-implementer-stanford-corenlp-avec-talend-1354
 * 
 * Run with -Xms2G -Xmx4G
 * 
 * @author Franck
 */
public class FrenchPipeline {

	public static void main(String[] args) throws IOException {

		Properties frProperties = new Properties();

		// Initialize pipeline properties with default for French
		frProperties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
		System.out.println("Default pipeline properties:");
		for (Object key : frProperties.keySet()) System.out.println(key + " = " + frProperties.get(key));

		// Specify annotators (default in StanfordCoreNLP-french.properties is tokenize, ssplit, pos, depparse 
		// See https://stanfordnlp.github.io/CoreNLP/dependencies.html
		// We add a custom lemmatizer and a classifier for NER
		// TODO: see if dcoref can also be added
		frProperties.put("annotators", "tokenize, ssplit, pos, custom.lemma, depparse, ner");

		// Adding option for the tokenizer (https://stanfordnlp.github.io/CoreNLP/tokenize.html)
		frProperties.setProperty("tokenize.options", "untokenizable=noneDelete"); // Silently deletes untokenizable sequences

		// Adding option for the sentence splitter (https://stanfordnlp.github.io/CoreNLP/ssplit.html)
		frProperties.setProperty("ssplit.newlineIsSentenceBreak", "always");

		// Default option french-ud is OK for the POS tagger (https://github.com/stanfordnlp/CoreNLP/issues/312)

		// Adding reference of custom lemmatizer and pointer to lexicon file
		// lexique_fr.txt can be found at https://sourceforge.net/projects/iramuteq/files/
		frProperties.setProperty("customAnnotatorClass.custom.lemma", "fr.insee.stamina.nlp.FrenchLemmaAnnotator");
		frProperties.setProperty("french.lemma.lemmaFile", "src/main/resources/data/lexique_fr.txt");

		// Adding pointer to entity file
		// The French NER dataset can be downloaded from http://lab.kbresearch.nl/static/html/eunews.html
		frProperties.setProperty("ner.model", "src/main/resources/data/eunews.fr.crf.gz");

		System.out.println("\nModified pipeline properties:");
		for (Object key : frProperties.keySet()) System.out.println(key + " = " + frProperties.get(key));

		StanfordCoreNLP pipeline = new StanfordCoreNLP(frProperties);

		// Read the text file to analyse into the 'text' variable
		Path path = Paths.get("src/main/resources/data/texte.txt");
		String text = Files.lines(path).collect(Collectors.joining("\n"));

		// Create an empty Annotation with just the text to analyze
		Annotation document = new Annotation(text);
		// Run all Annotators of the pipeline on this text
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			//String word = StringHandling.DOWNCASE(token.get(TextAnnotation.class)); 
			// Traverse the words in the sentence. A CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Text of the token
				String word = token.get(TextAnnotation.class);
				// LEMMA tag of the token
				String lemma = token.get(LemmaAnnotation.class);
				// POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);

				System.out.println("\n" + word + "\n. POS: " + pos + "\n. LEMMA: " + lemma + "\n. NER: " + ne);
			}
			// Dependency graph of the sentence
			SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);
			System.out.println("\n----------------------");
			System.out.println("Dependencies:\n" + dependencies);
		}
	}
}


// Resources:
// The OpeNER project (FP7): http://www.opener-project.eu/
// Ambiverse NLU: https://github.com/ambiverse-nlu/ambiverse-nlu