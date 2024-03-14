package cli.rule;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutablePair;
import com.google.common.collect.Lists;
import cli.rule.rules.SingularDocumentNameRule;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;

public class Utility {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String PATH_TO_ENGLISH_DICTIONARY = "/wordninja_words.txt";

    public static final String MODELS_EN_POS_MAXENT_BIN = "/models/en-pos-maxent.bin";

    private Utility() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean getPathSegmentContained(String word, String filePath) {
        boolean isWordInDictionary = false;
        try (InputStream is = Utility.class.getResourceAsStream(filePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String wordFromDictionary;
            while ((wordFromDictionary = br.readLine()) != null) {
                if (word.toLowerCase().contains(wordFromDictionary)) {
                    isWordInDictionary = true;
                    break;
                }
            }
        } catch (Exception e) {
            logger.severe(
                    "Error on checking if a word is contained in a dictionary: " + e.getMessage());
        }
        return isWordInDictionary;
    }

    public static boolean getPathSegmentMatch(String word, String filePath) {
        boolean isWordInDictionary = false;
        try (Scanner scanner = new Scanner(Utility.class.getResourceAsStream(filePath))) {
            if (scanner.useDelimiter("\\Z").next().matches(word))
                isWordInDictionary = true;
        } catch (Exception e) {
            logger.severe(
                    "Error on checking if a word is contained in a dictionary: " + e.getMessage());
        }
        return isWordInDictionary;
    }

    public static OpenAPI getOpenAPI(String path) {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(path, null, null);
        return swaggerParseResult.getOpenAPI();
    }

    /**
     * Method to change the switchPathSegment from plural to singular a vice versa.
     * 
     * @param equals
     * @return
     */
    public static String getControlPathSegmentForRule(boolean equals) {
        if (equals) {
            return SingularDocumentNameRule.SINGULAR;
        } else {
            return SingularDocumentNameRule.PLURAL;
        }
    }

    /**
     * Get a token from a word using the nlp apache pos tagger library.
     * 
     * @param pathSegment
     * @return
     */
    public static String getTokenNLP(String pathSegment) {
        if (pathSegment.equals("")) {
            return null;
        }
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(pathSegment);
        try (InputStream modelIn = Utility.class.getResourceAsStream(MODELS_EN_POS_MAXENT_BIN);) {
            POSModel posModel = new POSModel(modelIn);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            String tags[] = posTagger.tag(tokens);
            return tags[0];
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Given a word check if it is plural or singular based on token.
     * 
     * @param token
     * @return
     */
    public static String getTokenFromWord(String token) {
        String currentSwitchPathSegment;
        if (token.equals("NNS") || token.equals("NNPS")) {
            currentSwitchPathSegment = SingularDocumentNameRule.PLURAL;
        } else if (token.equals("NN") || token.equals("NNP")) {
            currentSwitchPathSegment = SingularDocumentNameRule.SINGULAR;
        } else {
            currentSwitchPathSegment = SingularDocumentNameRule.PLURAL;
        }
        return currentSwitchPathSegment;
    }

    /**
     * Split a string into words if possible using an english dictionary to match the words.
     * 
     * @param sentence
     * @return
     * @throws IOException
     */
    public static List<String> splitContiguousWords(String sentence) throws IOException {
        String splitRegex = "[^a-zA-Z0-9']+";
        Map<String, Number> wordCost = new HashMap<>();
        List<String> dictionaryWords = new ArrayList<>();
        InputStream is = null;

        try {
            is = Utility.class.getResourceAsStream(PATH_TO_ENGLISH_DICTIONARY);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            dictionaryWords = br.lines().collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error on getting the english dictionary file {e}", e);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        double naturalLogDictionaryWordsCount = Math.log(dictionaryWords.size());
        long wordIdx = 0;
        for (String word : dictionaryWords) {
            wordCost.put(word, Math.log(++wordIdx * naturalLogDictionaryWordsCount));
        }
        int maxWordLength =
                Collections.max(dictionaryWords, Comparator.comparing(String::length)).length();
        List<String> splitWords = new ArrayList<>();
        for (String partSentence : sentence.split(splitRegex)) {
            splitWords.add(split(partSentence, wordCost, maxWordLength));
        }
        return splitWords;
    }

    /**
     * Split a string into sub strings.
     * 
     * @param partSentence
     * @param wordCost
     * @param maxWordLength
     * @return
     */
    public static String split(String partSentence, Map<String, Number> wordCost,
            int maxWordLength) {
        List<ImmutablePair<Number, Number>> cost = new ArrayList<>();
        cost.add(new ImmutablePair<>(0, 0));
        for (int index = 1; index < partSentence.length() + 1; index++) {
            cost.add(bestMatch(partSentence, cost, index, wordCost, maxWordLength));
        }
        int idx = partSentence.length();
        List<String> output = new ArrayList<>();
        while (idx > 0) {
            ImmutablePair<Number, Number> candidate =
                    bestMatch(partSentence, cost, idx, wordCost, maxWordLength);
            Number candidateCost = candidate.getKey();
            Number candidateIndexValue = candidate.getValue();
            if (candidateCost.doubleValue() != cost.get(idx).getKey().doubleValue())
                logger.log(Level.SEVERE, "Candidate cost unmatched; This should not be the case!");
            boolean newToken = true;
            String token = partSentence.substring(idx - candidateIndexValue.intValue(), idx);
            if (token.equals("'") && !output.isEmpty()) {
                String lastWord = output.get(output.size() - 1);
                if (lastWord.equalsIgnoreCase("'s")
                        || (Character.isDigit(partSentence.charAt(idx - 1))
                                && Character.isDigit(lastWord.charAt(0)))) {
                    output.set(output.size() - 1, token + lastWord);
                    newToken = false;
                }
            }
            if (newToken) {
                output.add(token);
            }
            idx -= candidateIndexValue.intValue();
        }
        return String.join(" ", Lists.reverse(output));
    }

    /**
     * Get the best match for a word.
     * 
     * @param partSentence
     * @param cost
     * @param index
     * @param wordCost
     * @param maxWordLength
     * @return
     */
    public static ImmutablePair<Number, Number> bestMatch(String partSentence,
            List<ImmutablePair<Number, Number>> cost, int index, Map<String, Number> wordCost,
            int maxWordLength) {
        List<ImmutablePair<Number, Number>> candidates =
                Lists.reverse(cost.subList(Math.max(0, index - maxWordLength), index));
        int enumerateIdx = 0;
        ImmutablePair<Number, Number> minPair =
                new ImmutablePair<>(Integer.MAX_VALUE, enumerateIdx);
        for (ImmutablePair<Number, Number> pair : candidates) {
            ++enumerateIdx;
            String subsequence = partSentence.substring(index - enumerateIdx, index).toLowerCase();
            Number minCost = Integer.MAX_VALUE;
            if (wordCost.containsKey(subsequence)) {
                minCost = pair.getKey().doubleValue() + wordCost.get(subsequence).doubleValue();
            }
            if (minCost.doubleValue() < minPair.getKey().doubleValue()) {
                minPair = new ImmutablePair<>(minCost.doubleValue(), enumerateIdx);
            }
        }
        return minPair;
    }
}
