package rest.studentproject.rules;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.split;

public class HyphensRule implements IRestRule {

    private static final String title = "Hyphens (-) should be used to improve the readability of URIs";
    private static final RuleCategory ruleCategory = RuleCategory.URIS;
    private static final RuleSeverity ruleSeverity = RuleSeverity.ERROR;
    private static final RuleType ruleType = RuleType.STATIC;
    private static final List<RuleSoftwareQualityAttribute> lstRuleSoftwareQualityAttribute = List.of(RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;

    public HyphensRule(boolean isActive) {
        this.isActive = isActive;
    }
    /**
     *
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     *
     */
    @Override
    public RuleCategory getCategory() {
        return HyphensRule.ruleCategory;

    }

    /**
     *
     */
    @Override
    public RuleSeverity getSeverityType() {
        return ruleSeverity;
    }

    /**
     *
     */
    @Override
    public RuleType getRuleType() {
        return HyphensRule.ruleType;
    }

    /**
     *
     */
    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return lstRuleSoftwareQualityAttribute;
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
     * Rule to check if the path segments could contains more than one word, if so there is a violation.
     * @param openAPI
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) throws IOException {
        List<Violation> violations = new ArrayList<>();

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();

        if(paths.isEmpty()) return violations;
        // Loop through the paths
        for (String path: paths) {
            if(path.trim().equals("")) continue;
            // Get the path without the curly braces
            String[] pathSegments = path.split("/");
            for (String pathSegment:pathSegments) {
                if(pathSegment.isEmpty()) continue;
                String[] pathWithoutParameters = pathSegment.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
                if(pathWithoutParameters.length > 1){
                    violations.add(new Violation(0, "", "", "Error at:" + path));
                    break;
                }
                // Need to implement this part -> https://stackoverflow.com/questions/8870261/how-to-split-text-without-spaces-into-list-of-words/11642687#11642687

            }

        }
        List<String> testString = splitContiguousWords("smallpets");
        String[] splitString = testString.get(0).split(" ");
        String streTest = splitString[0];
        boolean tstCheck = violations.isEmpty();

        return violations;
    }

    public List<String> splitContiguousWords(String sentence) throws IOException {

        String splitRegex = "[^a-zA-Z0-9']+";
        Map<String, Number> wordCost = new HashMap<>();
        // List<String> dictionaryWords = IOUtils.linesFromFile("ninja_words.txt", StandardCharsets.UTF_8.name());
        List<String> dictionaryWords = new ArrayList<>();
        Stream<String> lines = Files.lines(Paths.get("wordninja_words.txt"), Charset.defaultCharset());
        dictionaryWords = lines.collect(Collectors.toList());


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
        // log.info("Split word for the sentence: {}", splitWords);
        return splitWords;
    }

    private String split(String partSentence, Map<String, Number> wordCost, int maxWordLength) {
        List<ImmutablePair<Number, Number>> cost = new ArrayList<>();
        cost.add(new ImmutablePair<>(Integer.valueOf(0), Integer.valueOf(0)));
        for (int index = 1; index < partSentence.length() + 1; index++) {
            cost.add(bestMatch(partSentence, cost, index, wordCost, maxWordLength));
        }
        int idx = partSentence.length();
        List<String> output = new ArrayList<>();
        while (idx > 0) {
            ImmutablePair<Number, Number> candidate = bestMatch(partSentence, cost, idx, wordCost, maxWordLength);
            Number candidateCost = candidate.getKey();
            Number candidateIndexValue = candidate.getValue();
            if (candidateCost.doubleValue() != cost.get(idx).getKey().doubleValue()) {
                throw new RuntimeException("Candidate cost unmatched; This should not be the case!");
            }
            boolean newToken = true;
            String token = partSentence.substring(idx - candidateIndexValue.intValue(), idx);
            if (token != "\'" && output.size() > 0) {
                String lastWord = output.get(output.size() - 1);
                if (lastWord.equalsIgnoreCase("\'s") ||
                        (Character.isDigit(partSentence.charAt(idx - 1)) && Character.isDigit(lastWord.charAt(0)))) {
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


    private ImmutablePair<Number, Number> bestMatch(String partSentence, List<ImmutablePair<Number, Number>> cost, int index,
                                           Map<String, Number> wordCost, int maxWordLength) {
        List<ImmutablePair<Number, Number>> candidates = Lists.reverse(cost.subList(Math.max(0, index - maxWordLength), index));
        int enumerateIdx = 0;
        ImmutablePair<Number, Number> minPair = new ImmutablePair<>(Integer.MAX_VALUE, Integer.valueOf(enumerateIdx));
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
