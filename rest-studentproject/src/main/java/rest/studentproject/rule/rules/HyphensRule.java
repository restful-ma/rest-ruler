package rest.studentproject.rule.rules;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.lang3.tuple.ImmutablePair;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;
import rest.studentproject.rule.utility.MixIDK;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;

public class HyphensRule implements IRestRule {

    private static final String PATH_TO_ENGLISH_DICTIONARY =
            "src/main/java/rest/studentproject/docs/wordninja_words" + ".txt";
    private static final String TITLE = "Hyphens (-) should be used to improve the readability of URIs";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final RuleType RULE_TYPE = RuleType.STATIC;
    private static final List<RuleSoftwareQualityAttribute> RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST =
            List.of(RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;

    public HyphensRule(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     *
     */
    @Override
    public String getTitle() {
        return TITLE;
    }

    /**
     *
     */
    @Override
    public RuleCategory getCategory() {
        return HyphensRule.RULE_CATEGORY;

    }

    /**
     *
     */
    @Override
    public RuleSeverity getSeverityType() {
        return RULE_SEVERITY;
    }

    /**
     *
     */
    @Override
    public RuleType getRuleType() {
        return HyphensRule.RULE_TYPE;
    }

    /**
     *
     */
    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST;
    }

    /**
     *
     */
    @Override
    public boolean getIsActive() {
        return this.isActive;
    }

    /**
     *
     */
    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Rule to check if the path segments could contain more than one word, if so there is a violation.
     *
     * @param openAPI
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violations = new ArrayList<>();

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();

        if (paths.isEmpty()) return violations;
        // Loop through the paths
        return getLstViolations(violations, paths);
    }

    private List<Violation> getLstViolations(List<Violation> violations, Set<String> paths) {
        for (String path : paths) {
            if (path.trim().equals("")) continue;
            // Get the path without the curly braces
            String pathWithoutVariables = path.replaceAll("\\{" + ".*" + "\\}", "");
            String[] pathSegments = pathWithoutVariables.split("/");
            // Extract path segments based on / char and check if there are violations
            Violation violation = getLstViolationsFromPathSegments(path, pathSegments);
            if (violation != null) violations.add(violation);


        }
        return violations;
    }

    private Violation getLstViolationsFromPathSegments(String path, String[] pathSegments) {
        for (String pathSegment : pathSegments) {
            if (pathSegment.isEmpty()) continue;
            boolean isPathFullyContained;

            isPathFullyContained = MixIDK.getPathSegmentMatch(pathSegment, PATH_TO_ENGLISH_DICTIONARY);

            if (isPathFullyContained) continue;
            List<String> itemsFromHyphens = Arrays.asList(pathSegment.split("-"));
            List<String> itemsFromUnderscore = Arrays.asList(pathSegment.split("_"));
            // Math the segment path based on the regex. This solution is very fast to run
            List<String> pathWithoutParameters = Arrays.asList(pathSegment.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)" +
                    "(?=[A-Z][a-z])"));

            if (pathWithoutParameters.size() > 1) {
                return new Violation(this, locMapper.getLOCOfPath(path), ImprovementSuggestion.HYPHEN, path,
                        ErrorMessage.HYPHEN);
            }

            // If with the regex no substring was found then we need to check against a dictionary of english words
            try {
                List<String> subStringFromPath = splitContiguousWords(pathSegment);
                List<String> pathWithoutParameterDictionaryMatching = Arrays.asList(subStringFromPath.get(0).split(" "
                ));
                // If the path is correct and the matching regex creates the same split with the "-" as split, then
                // continue with the next segment path
                if (itemsFromHyphens.equals(pathWithoutParameterDictionaryMatching) || itemsFromHyphens.equals(subStringFromPath) || subStringFromPath.equals(itemsFromUnderscore))
                    continue;
                // Add violations if there is some match
                return new Violation(this, locMapper.getLOCOfPath(path), ImprovementSuggestion.HYPHEN, path,
                        ErrorMessage.HYPHEN);
            } catch (IOException e) {
                Logger logger = Logger.getLogger(HyphensRule.class.getName());
                logger.log(Level.SEVERE, "Error on checking substring against a dictionary{e}", e);
            }

        }
        return null;
    }

    public List<String> splitContiguousWords(String sentence) throws IOException {

        String splitRegex = "[^a-zA-Z0-9']+";
        Map<String, Number> wordCost = new HashMap<>();
        List<String> dictionaryWords = new ArrayList<>();
        Stream<String> stringLines = null;
        try {
            stringLines = Files.lines(Paths.get(PATH_TO_ENGLISH_DICTIONARY), Charset.defaultCharset());
            dictionaryWords = stringLines.collect(Collectors.toList());
        } catch (Exception e) {
            Logger logger = Logger.getLogger(HyphensRule.class.getName());
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

    private String split(String partSentence, Map<String, Number> wordCost, int maxWordLength) {
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
                throw new RuntimeException("Candidate cost unmatched; This should not be the case!");
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


    private ImmutablePair<Number, Number> bestMatch(String partSentence, List<ImmutablePair<Number, Number>> cost,
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
