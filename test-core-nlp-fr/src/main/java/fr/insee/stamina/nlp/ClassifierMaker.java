package fr.insee.stamina.nlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

// References: 
// http://blog.thehumangeo.com/ner-intro.html
// https://www.depends-on-the-definition.com/introduction-named-entity-recognition-python/
// https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/ie/crf/CRFClassifier.html
// https://nlp.stanford.edu/software/CRF-NER.shtml
// https://www.programcreek.com/java-api-examples/?api=edu.stanford.nlp.ie.crf.CRFClassifier

public class ClassifierMaker {

	public static void main(String[] args) {

		Properties properties = new Properties();

//		properties.setProperty("inputEncoding", "UTF-8");
//		properties.setProperty("map", "word=0,answer=1");
//		properties.setProperty("trainFile", "src/main/resources/data/training.tsv");
//		properties.setProperty("serializeTo", "src/main/resources/data/model.crf.gz");
//
//		CRFClassifier<CoreLabel> classifier = new CRFClassifier<CoreLabel>(properties);
//		classifier.train();

		prepareData();
	}

	/**
	 * Read the concepts and produces the formatted file.
	 */
	public static void prepareData() {

		// The list of concepts is first obtained by querying http://rdf.insee.fr/sparql (predefined query #6, change LIMIT) asking for CSV, and storing the results in concepts-query-results.csv
		String queryResultsFileName = "src/main/resources/data/concepts-query-results.csv";
		String formattedDataFileName = "src/main/resources/data/training-data.tsv";
		try (Stream<String> lines = Files.lines(Paths.get(queryResultsFileName))) {
			// Change the filter to exclude for example labels which are too long
			Files.write(Paths.get(formattedDataFileName), (Iterable<String>)lines.skip(1).filter(filterLine).map(mapToItem)::iterator);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Filter predicate for input lines.
	 */
	private static Predicate<String> filterLine = line -> true;

	/**
	 * Map an input line to a formatted line.
	 */
	private static Function<String, String> mapToItem = line -> {

		// The first comma separates the concept URI from the label
		int commaIndex = line.indexOf(',');

		String conceptId = StringUtils.substringAfterLast(line.substring(0, commaIndex), "/");

		// U+00A0 is unbreakable space, which sometimes happen at the end of concept labels
		return StringUtils.strip(line.substring(commaIndex + 1), "\"\u00A0") + "\tSTAT-CPT\t" + conceptId;

	};
}
