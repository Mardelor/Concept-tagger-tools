package fr.insee.stamina.nlp;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
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
 * See http://www.erwanlenagard.com/general/tutoriel-implementer-stanford-corenlp-avec-talend-1354
 * 
 * French NER dataset available at http://lab.kbresearch.nl/static/html/eunews.html
 * 
 * @author Franck
 *
 */
public class FrenchPipeline {

	public static void main(String[] args) throws IOException {

		Properties frProperties = new Properties();

		// Initialize pipeline properties with default for French
		frProperties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
		System.out.println("Default pipeline properties:");
		for (Object key : frProperties.keySet()) System.out.println(key + " = " + frProperties.get(key));

		// Specifies annotators (default in StanfordCoreNLP-french.properties is tokenize, ssplit, pos, depparse 
		// See https://stanfordnlp.github.io/CoreNLP/dependencies.html
		// frProperties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		// frProperties.put("annotators", "tokenize, ssplit, pos, custom.lemma, ner");
		frProperties.put("annotators", "tokenize, ssplit, pos, custom.lemma, depparse");

		// Adding option for the tokenizer (https://stanfordnlp.github.io/CoreNLP/tokenize.html)
		frProperties.setProperty("tokenize.options", "untokenizable=noneDelete"); // Silently deletes untokenizable sequences

		// Adding option for the sentence splitter (https://stanfordnlp.github.io/CoreNLP/ssplit.html)
		frProperties.setProperty("ssplit.newlineIsSentenceBreak", "always");

		// Default option french-ud is OK for the POS tagger (https://github.com/stanfordnlp/CoreNLP/issues/312)

		// Adding reference of custom lemmatizer and pointer to lexicon file
		frProperties.setProperty("customAnnotatorClass.custom.lemma", "fr.insee.stamina.nlp.FrenchLemmaAnnotator");
		frProperties.setProperty("french.lemma.lemmaFile", "src/main/resources/data/lexique_fr.txt");

		System.out.println("\nModified pipeline properties:");
		for (Object key : frProperties.keySet()) System.out.println(key + " = " + frProperties.get(key));

		StanfordCoreNLP pipeline = new StanfordCoreNLP(frProperties);

		// Read some text in the text variable
		String text = "L'enfant mange une pomme à Paris. Le soleil brille ce matin.";

		// Create an empty Annotation just with the given text
		Annotation document = new Annotation(text);
		// Run all Annotators on this text
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			//String word = StringHandling.DOWNCASE(token.get(TextAnnotation.class));
			// Traverse the words in the sentence. A CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Text of the token
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				String lemma = token.get(LemmaAnnotation.class);

				System.out.println("\n" + word + "\n. POS: " + pos + "\n. LEMMA: " + lemma);
			}
			// Dependency graph of the sentence
			SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);
			System.out.println("Dependencies:\n" + dependencies);
			System.out.println("\n----------------------");
		}
	}
}


// Resources:
// The OpeNER project (FP7): http://www.opener-project.eu/
// Ambiverse NLU: https://github.com/ambiverse-nlu/ambiverse-nlu