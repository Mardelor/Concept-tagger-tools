package fr.insee.stamina.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.types.Value;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.TypesafeMap;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BasicFrenchNERPipeline {

    // TODO remettre un main adapté

    public static void main(String arg[]) throws IOException {
        System.out.println(run(arg[0]));
    }

    public static String run(String text) throws IOException {
        String annotatedText;

        Properties properties = new Properties();
        properties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));

        properties.put("annotators", "tokenize, ssplit, pos, custom.lemma, depparse, tokensregex");

        properties.setProperty("tokenize.options", "untokenizable=noneDelete");
        properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
        properties.setProperty("customAnnotatorClass.custom.lemma", "fr.insee.stamina.nlp.lemma.FrenchLemmaAnnotator");
        properties.setProperty("french.lemma.lemmaFile", "src/main/resources/lexique_fr.txt");
        properties.setProperty("encoding", "UTF-8");

        properties.setProperty("tokensregex.rules", "src/main/resources/concepts.rules");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        StringBuilder builder = new StringBuilder("");
        ArrayList<String> cBuilder = new ArrayList<>();
        StringBuilder out = new StringBuilder();
        CoreLabel current;
        CoreLabel next = new CoreLabel();

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (int i=0; i < sentence.get(CoreAnnotations.TokensAnnotation.class).size(); i++) {
                current = sentence.get(CoreAnnotations.TokensAnnotation.class).get(i);
                if (i < sentence.get(CoreAnnotations.TokensAnnotation.class).size() - 1) {
                    next = sentence.get(CoreAnnotations.TokensAnnotation.class).get(i + 1);
                }
                if (current.ner().equals("O")) {
                    if (Pattern.matches("[,.;]", current.word())) {
                        out.append(current.originalText());
                    } else {
                        out.append(String.format(" %s", current.originalText()));
                    }
                } else {
                    if (cBuilder.isEmpty()) {
                        cBuilder.add(String.format("%s\t%s", current.ner(), current.get(CoreAnnotations.MentionsAnnotation.class)));
                        out.append(String.format(" <%s id=\"%s\" uri=\"%s\">%s",
                                current.ner(),
                                ((String)(Object) current.get(CoreAnnotations.MentionsAnnotation.class)).split("\t")[0],
                                ((String)(Object) current.get(CoreAnnotations.MentionsAnnotation.class)).split("\t")[1],
                                current.originalText()));
                    } else {
                        out.append(String.format(" %s", current.originalText()));
                    }
                    if (i < sentence.get(CoreAnnotations.TokensAnnotation.class).size() - 1 &&
                            !cBuilder.get(cBuilder.size() - 1).equals(String.format("%s\t%s", next.ner(), next.get(CoreAnnotations.MentionsAnnotation.class)))) {
                        cBuilder.clear();
                        out.append(String.format("</%s>", current.ner()));
                    }
                }
                builder.append(out.toString());
                out = new StringBuilder("");
            }
        }

        return builder.toString();
    }
}
