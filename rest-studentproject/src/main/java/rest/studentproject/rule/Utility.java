package rest.studentproject.rule;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.atteo.evo.inflector.English;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static rest.studentproject.rule.rules.SingularDocumentNameRule.PLURAL;
import static rest.studentproject.rule.rules.SingularDocumentNameRule.SINGULAR;

public class Utility {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String PATH_TO_ENGLISH_DICTIONARY =
            "src/main/java/rest/studentproject/docs/wordninja_words.txt";

    private Utility() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean getPathSegmentContained(String word, String filePath) {
        boolean isWordInDictionary = false;
        try (FileReader fileReader = new FileReader(filePath);) {
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNext()) {
                String wordFromDictionary = scanner.next();
                if (word.toLowerCase().contains(wordFromDictionary)) {
                    isWordInDictionary = true;
                    break;
                }
            }
        } catch (Exception e) {
            logger.severe("Error on checking if a word is contained in a dictionary: " + e.getMessage());
        }
        return isWordInDictionary;
    }

    public static boolean getPathSegmentMatch(String word, String filePath) {
        boolean isWordInDictionary = false;
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.useDelimiter("\\Z").next().matches(word)) isWordInDictionary = true;
        } catch (Exception e) {
            logger.severe("Error on checking if a word is contained in a dictionary: " + e.getMessage());
        }
        return isWordInDictionary;
    }

    /**
     * Method to determine the switchPathSegment based on the firstPathSegment.
     * @param pathSegments
     * @param switchPathSegment
     * @param firstPathSegment
     * @return
     */
    public static String getSwitchPathSegment(String[] pathSegments, String switchPathSegment, String firstPathSegment) {
        if(!firstPathSegment.isEmpty()) {
            switchPathSegment = getPluralOrSingularOfWord(firstPathSegment);
        }else if(pathSegments.length > 1){
            firstPathSegment = pathSegments[1].trim().toLowerCase();
            switchPathSegment = getPluralOrSingularOfWord(firstPathSegment);
        }
        return switchPathSegment;
    }

    /**
     * Method to get the plural or singular form of a word using the OxfordDictionaryAPI.
     * @param firstPathSegment
     * @return
     */
    public static String getPluralOrSingularOfWord(String firstPathSegment) {
        String switchPathSegment;
        boolean firstPathSegmentForm = !firstPathSegment.equals(English.plural(firstPathSegment.trim().toLowerCase(), 1));
        switchPathSegment = getControlPathSegmentForRule(firstPathSegmentForm);
        return switchPathSegment;
    }

    /**
     * Method to change the switchPathSegment from plural to singular a vice versa.
     * @param equals
     * @return
     */
    public static String getControlPathSegmentForRule(boolean equals) {
        if (equals) {
            return SINGULAR;
        } else {
            return PLURAL;
        }
    }

    public static List<String> splitContiguousWords(String sentence) throws IOException {
        String splitRegex = "[^a-zA-Z0-9']+";
        Map<String, Number> wordCost = new HashMap<>();
        List<String> dictionaryWords = new ArrayList<>();
        Stream<String> stringLines = null;
        try {
            stringLines = Files.lines(Paths.get(PATH_TO_ENGLISH_DICTIONARY), Charset.defaultCharset());
            dictionaryWords = stringLines.collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error on getting the english dictionary file {e}", e);
        } finally {
            if (stringLines != null) stringLines.close();
        }

        double naturalLogDictionaryWordsCount = Math.log(dictionaryWords.size());
        long wordIdx = 0;
        for (String word : dictionaryWords) {
            wordCost.put(word, Math.log(++wordIdx * naturalLogDictionaryWordsCount));
        }
        int maxWordLength = Collections.max(dictionaryWords, Comparator.comparing(String::length)).length();
        List<String> splitWords = new ArrayList<>();
        for (String partSentence : sentence.split(splitRegex)) {
            splitWords.add(split(partSentence, wordCost, maxWordLength));
        }
        return splitWords;
    }

    public static String split(String partSentence, Map<String, Number> wordCost, int maxWordLength) {
        List<ImmutablePair<Number, Number>> cost = new ArrayList<>();
        cost.add(new ImmutablePair<>(0, 0));
        for (int index = 1; index < partSentence.length() + 1; index++) {
            cost.add(bestMatch(partSentence, cost, index, wordCost, maxWordLength));
        }
        int idx = partSentence.length();
        List<String> output = new ArrayList<>();
        while (idx > 0) {
            ImmutablePair<Number, Number> candidate = bestMatch(partSentence, cost, idx, wordCost, maxWordLength);
            Number candidateCost = candidate.getKey();
            Number candidateIndexValue = candidate.getValue();
            if (candidateCost.doubleValue() != cost.get(idx).getKey().doubleValue())
                logger.log(Level.SEVERE, "Candidate cost unmatched; This should not be the case!");
            boolean newToken = true;
            String token = partSentence.substring(idx - candidateIndexValue.intValue(), idx);
            if (token.equals("'") && !output.isEmpty()) {
                String lastWord = output.get(output.size() - 1);
                if (lastWord.equalsIgnoreCase("'s") || (Character.isDigit(partSentence.charAt(idx - 1)) && Character.isDigit(lastWord.charAt(0)))) {
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

    public static ImmutablePair<Number, Number> bestMatch(String partSentence, List<ImmutablePair<Number, Number>> cost,
                                                    int index, Map<String, Number> wordCost, int maxWordLength) {
        List<ImmutablePair<Number, Number>> candidates = Lists.reverse(cost.subList(Math.max(0,
                index - maxWordLength), index));
        int enumerateIdx = 0;
        ImmutablePair<Number, Number> minPair = new ImmutablePair<>(Integer.MAX_VALUE, enumerateIdx);
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
