package cli.rule.rules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutablePair;
import com.google.common.collect.Lists;
import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Utility;
import cli.rule.Violation;
import cli.rule.constants.ErrorMessage;
import cli.rule.constants.ImprovementSuggestion;
import cli.rule.constants.RuleCategory;
import cli.rule.constants.RuleSeverity;
import cli.rule.constants.RuleSoftwareQualityAttribute;
import cli.utility.Output;
import io.swagger.v3.oas.models.OpenAPI;
import cli.rule.constants.RuleIdentifier;

public class HyphensRule implements IRestRule {

    private static final String PATH_TO_ENGLISH_DICTIONARY = "/wordninja_words.txt";

    private static final String TITLE =
            "Hyphens (-) should be used to improve the readability of URIs";
    private static final RuleIdentifier RULE_IDENTIFIER = RuleIdentifier.HYPHENS;
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST =
            List.of(RuleSoftwareQualityAttribute.COMPATIBILITY,
                    RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean isActive;

    public HyphensRule(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public RuleIdentifier getIdentifier() {
        return RULE_IDENTIFIER;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public RuleCategory getCategory() {
        return HyphensRule.RULE_CATEGORY;
    }

    @Override
    public RuleSeverity getSeverityType() {
        return RULE_SEVERITY;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST;
    }

    @Override
    public boolean getIsActive() {
        return this.isActive;
    }

    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Rule to check if the path segments could contain more than one word, if so there is a
     * violation.
     *
     * @param openAPI
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violations = new ArrayList<>();

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();

        if (paths.isEmpty())
            return violations;
        // Loop through the paths
        return getLstViolations(violations, paths);
    }

    private List<Violation> getLstViolations(List<Violation> violations, Set<String> paths) {
        int curPath = 1;
        int totalPaths = paths.size();
        for (String path : paths) {
            Output.progressPercentage(curPath, totalPaths);
            curPath++;
            if (path.trim().equals(""))
                continue;
            // Get the path without the curly braces
            String pathWithoutVariables = path.replaceAll("\\{" + ".*" + "\\}", "");
            String[] pathSegments = pathWithoutVariables.split("/");
            // Extract path segments based on / char and check if there are violations
            Violation violation = getLstViolationsFromPathSegments(path, pathSegments);
            if (violation != null)
                violations.add(violation);
        }
        return violations;
    }

    private Violation getLstViolationsFromPathSegments(String path, String[] pathSegments) {
        for (String pathSegment : pathSegments) {
            if (pathSegment.isEmpty())
                continue;
            if (pathSegment.contains("\\"))
                continue;
            boolean isPathFullyContained;

            isPathFullyContained =
                    Utility.getPathSegmentMatch(pathSegment, PATH_TO_ENGLISH_DICTIONARY);

            if (isPathFullyContained)
                continue;
            List<String> itemsFromHyphens = Arrays.asList(pathSegment.split("-"));
            List<String> itemsFromUnderscore = Arrays.asList(pathSegment.split("_"));
            // Math the segment path based on the regex. This solution is very fast to run
            List<String> pathWithoutParameters = Arrays
                    .asList(pathSegment.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)" + "(?=[A-Z][a-z])"));

            if (pathWithoutParameters.size() > 1) {
                return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path),
                        ImprovementSuggestion.HYPHEN, path, ErrorMessage.HYPHEN);
            }

            // If with the regex no substring was found then we need to check against a
            // dictionary of english words
            try {
                List<String> subStringFromPath = splitContiguousWords(pathSegment);
                List<String> pathWithoutParameterDictionaryMatching =
                        Arrays.asList(subStringFromPath.get(0).split(" "));
                // If the path is correct and the matching regex creates the same split with the
                // "-" as split, then
                // continue with the next segment path
                if (itemsFromHyphens.equals(pathWithoutParameterDictionaryMatching)
                        || itemsFromHyphens.equals(subStringFromPath)
                        || subStringFromPath.equals(itemsFromUnderscore))
                    continue;
                // Add violations if there is some match
                return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path),
                        ImprovementSuggestion.HYPHEN, path, ErrorMessage.HYPHEN);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error on checking substring against a dictionary{e}", e);
            }

        }
        return null;
    }

    public List<String> splitContiguousWords(String sentence) throws IOException {

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

    private String split(String partSentence, Map<String, Number> wordCost, int maxWordLength) {
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

    private ImmutablePair<Number, Number> bestMatch(String partSentence,
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
