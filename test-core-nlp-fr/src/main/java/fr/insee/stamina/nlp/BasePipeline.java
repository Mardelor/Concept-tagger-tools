package fr.insee.stamina.nlp;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class BasePipeline {

	/**
	 * Run with -Xms2G -Xmx4G
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Create a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
		Properties properties = new Properties();
		properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

		// Read some text in the text variable
		String text = "One American soldier was killed and three others were wounded in the raid on the home of a senior Qaeda collaborator, and there are allegations that several civilians were killed."; // Add your text here!

		// Create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// Run all Annotators on this text
		pipeline.annotate(document);

		// All the sentences in the document. A CoreMap is a map that uses class objects as keys and has values with custom types
		System.out.println("Tokens with part-of-speech and named entity tags");
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Traverse the words in the sentence. A CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Text of the token
				String word = token.get(TextAnnotation.class);
				// POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);

				System.out.println("\n" + word + "\n. POS:" + pos + "\n. NE:" + ne);
			}

			// Syntactic parse tree of the current sentence
			Tree tree = sentence.get(TreeAnnotation.class);
			// Dependency graph of the sentence
			SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);
			// Coreferences
			Map<Integer, CorefChain> chain = document.get(CorefChainAnnotation.class);

			// Print the different elements
			tree.pennPrint(System.out);
			System.out.println("Syntactic tree:\n" + tree);
			System.out.println("Dependencies:\n" + dependencies);
			System.out.println("Coreferences:\n" + chain);
		}
	}
}
