package cli.rule.rules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Utility;
import cli.rule.Violation;
import cli.rule.constants.*;
import cli.utility.Output;
import io.swagger.v3.oas.models.OpenAPI;

public class CamelCaseRule implements IRestRule {

    private static final String PATH_TO_ENGLISH_DICTIONARY = "/wordninja_words.txt";
    private static final String TITLE = "CamelCase should be used for multi-word URI segments";
    private static final RuleIdentifier RULE_IDENTIFIER = RuleIdentifier.CAMEL_CASE;
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST = List
            .of(RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean isActive;

    public CamelCaseRule(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public RuleIdentifier getIdentifier() {
        return RULE_IDENTIFIER;
    }

    @Override
    public RuleCategory getCategory() {
        return RULE_CATEGORY;
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

    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violations = new ArrayList<>();
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

            // Check if the segment contains any separators (hyphens or underscores)
            if (pathSegment.contains("-") || pathSegment.contains("_")) {
                return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path),
                        ImprovementSuggestion.CAMELCASE, 
                        path, ErrorMessage.CAMELCASE);
            }

            // First check if the segment starts with uppercase (violation)
            if (Character.isUpperCase(pathSegment.charAt(0))) {
                return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path),
                        ImprovementSuggestion.CAMELCASE, 
                        path, ErrorMessage.CAMELCASE);
            }

            boolean isPathFullyContained = Utility.getPathSegmentMatch(pathSegment, PATH_TO_ENGLISH_DICTIONARY);
            if (isPathFullyContained)
                continue;
    
            List<String> itemsFromCamelCase = Arrays.asList(
                    pathSegment.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"));
    
            if (itemsFromCamelCase.size() > 1) {
                // multiple words detected, check if camelCase
                if (!isValidCamelCase(pathSegment, itemsFromCamelCase)) {
                    return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path),
                            ImprovementSuggestion.CAMELCASE, path, ErrorMessage.CAMELCASE);
                }
                continue;
            }
    
            try {
                List<String> subStringFromPath = splitContiguousWords(pathSegment);
                List<String> pathFromDictionary =
                        Arrays.asList(subStringFromPath.get(0).split(" "));
    
                if (pathFromDictionary.size() > 1) {
                    // Check if not camelCase
                    if (!isValidCamelCaseForDictionaryWords(pathSegment, pathFromDictionary)) {
                        return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path),
                                ImprovementSuggestion.CAMELCASE, path, ErrorMessage.CAMELCASE);
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error checking segment against dictionary", e);
            }
        }
        return null;
    }    

    private boolean isValidCamelCase(String segment, List<String> words) {
        // First word should be lowercase
        if (!Character.isLowerCase(segment.charAt(0)))
            return false;

        // For each word after the first, check if it starts with uppercase
        int currentPos = 0;
        for (int i = 1; i < words.size(); i++) {
            String word = words.get(i);
            currentPos = segment.indexOf(word, currentPos);
            if (currentPos == -1 || !Character.isUpperCase(segment.charAt(currentPos)))
                return false;
            currentPos += word.length();
        }

        return true;
    }

    private List<String> splitContiguousWords(String sentence) throws IOException {
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

        int maxWordLength = Collections.max(dictionaryWords, Comparator.comparing(String::length)).length();
        List<String> splitWords = new ArrayList<>();
        for (String partSentence : sentence.split(splitRegex)) {
            splitWords.add(split(partSentence, wordCost, maxWordLength));
        }
        return splitWords;
    }

    private String split(String partSentence, Map<String, Number> wordCost, int maxWordLength) {
        List<Map.Entry<Number, Number>> cost = new ArrayList<>();
        cost.add(new AbstractMap.SimpleEntry<>(0, 0));
        
        for (int index = 1; index <= partSentence.length(); index++) {
            cost.add(bestMatch(partSentence, cost, index, wordCost, maxWordLength));
        }

        int idx = partSentence.length();
        List<String> output = new ArrayList<>();
        
        while (idx > 0) {
            Map.Entry<Number, Number> candidate = bestMatch(partSentence, cost, idx, wordCost, maxWordLength);
            Number candidateCost = candidate.getKey();
            Number candidateIndexValue = candidate.getValue();
            
            if (candidateCost.doubleValue() != cost.get(idx).getKey().doubleValue()) {
                logger.log(Level.SEVERE, "Candidate cost unmatched; This should not be the case!");
            }
            
            String token = partSentence.substring(idx - candidateIndexValue.intValue(), idx);
            output.add(token);
            idx -= candidateIndexValue.intValue();
        }
        
        return String.join(" ", output);
    }

    private Map.Entry<Number, Number> bestMatch(String partSentence, List<Map.Entry<Number, Number>> cost, 
            int index, Map<String, Number> wordCost, int maxWordLength) {
        List<Map.Entry<Number, Number>> candidates = new ArrayList<>(cost.subList(
            Math.max(0, index - maxWordLength), index));
        Collections.reverse(candidates);
        
        int enumerateIdx = 0;
        Map.Entry<Number, Number> minPair = new AbstractMap.SimpleEntry<>(Integer.MAX_VALUE, enumerateIdx);
        
        for (Map.Entry<Number, Number> pair : candidates) {
            ++enumerateIdx;
            String subsequence = partSentence.substring(index - enumerateIdx, index).toLowerCase();
            Number minCost = Integer.MAX_VALUE;
            
            if (wordCost.containsKey(subsequence)) {
                minCost = pair.getKey().doubleValue() + wordCost.get(subsequence).doubleValue();
            }
            
            if (minCost.doubleValue() < minPair.getKey().doubleValue()) {
                minPair = new AbstractMap.SimpleEntry<>(minCost.doubleValue(), enumerateIdx);
            }
        }
        
        return minPair;
    }

    private boolean isValidCamelCaseForDictionaryWords(String segment, List<String> words) {
        if (words.size() <= 1) return true;
        
        // Build expected camelCase version
        StringBuilder expectedCamelCase = new StringBuilder();
        expectedCamelCase.append(words.get(0).toLowerCase());
        
        for (int i = 1; i < words.size(); i++) {
            String word = words.get(i);
            expectedCamelCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
        }
        
        return segment.equals(expectedCamelCase.toString());
    }
} 