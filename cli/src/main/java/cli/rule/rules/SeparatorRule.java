package cli.rule.rules;

import cli.rule.constants.*;
import io.swagger.v3.oas.models.OpenAPI;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.utility.Output;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cli.analyzer.RestAnalyzer.locMapper;

/**
 * RULE: Forward slash separator (/) must be used to indicate a hierarchical
 * relationship
 */
public class SeparatorRule implements IRestRule {

    static final String TITLE = "Forward slash separator (/) must be used to indicate a hierarchical relationship";
    static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    static final RuleSeverity RULE_SEVERITY = RuleSeverity.CRITICAL;
    static final List<RuleType> RULE_TYPE = List.of(RuleType.STATIC);
    static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List
            .of(RuleSoftwareQualityAttribute.MAINTAINABILITY);

    boolean isActive;
    private static char[] separators = { '.', ':', ';', ',', '\\', '#', '/', '-', '?', '=' };

    public SeparatorRule(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getTitle() {
        return TITLE;
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
    public List<RuleType> getRuleType() {
        return RULE_TYPE;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return SOFTWARE_QUALITY_ATTRIBUTES;
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
        Set<String> paths = openAPI.getPaths().keySet();
        return checkSeparator(paths);
    }

    /**
     * checks a given path for potential rule violations
     *
     * @param pathList
     * @return
     */
    public List<Violation> checkSeparator(Set<String> pathList) {

        List<Violation> violationList = new ArrayList<>();
        // expected Pattern
        Pattern expectedPattern = Pattern.compile("^(\\/((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+))+\\/?$");

        int curPath = 1;
        int totalPaths = pathList.size();
        for (String path : pathList) {
            Output.progressPercentage(curPath, totalPaths);
            curPath++;

            Matcher matcher = expectedPattern.matcher(path);

            int currentSize = violationList.size();
            // check if path has expected format
            if (!matcher.find()) {

                // find illegal separators
                violationList.addAll(findInvalidSeparators(path));

                // unknown case:
                Violation unknownCase = catchUnknownCase(currentSize, violationList.size(), path);
                if (unknownCase != null) {
                    violationList.add(unknownCase);
                }

            }
        }
        return violationList;
    }

    /**
     * checks a String for possible separators
     *
     * @param separator separator candidate
     * @param path      path under test
     * @return true or false depending on if it is a separator
     */
    private static boolean checkForSeparator(char separator, String path) {

        List<String> patterns = new ArrayList<>();

        // '=' and '-' are unique as they are valid characters in Paths and URLS
        switch (separator) {
            case '.':
                // escape regex operation characters, otherwise identical to default case
                patterns.add("^((\\/|\\.)((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+))+(\\.|\\/)?$");

                break;
            case '\\':
                // escape regex operation characters, otherwise identical to default case
                patterns.add("^((\\/|\\\\)((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+))+(\\\\|\\/)?$");

                break;
            case '?':
                // escape regex operation characters, otherwise identical to default case
                patterns.add("^((\\/|\\?)((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+))+(\\?|\\/)?$");

                break;
            case '=':
                // starts with '=' but afterwards follows expected pattern
                patterns.add("^=(((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&]+)\\/?)+$");
                // case: path includes path variables that allow detection of '=' as a separator
                patterns.add("=(\\{[^\\/{}\\(\\)\\[\\]]+\\})");
                patterns.add("^((\\/|" + separator + ")((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&]+))+("
                        + separator + "|\\/)?$");

                break;
            case '-':
                // starts with '-' but afterwards follows expected pattern
                patterns.add("^-(((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+)\\/?)+$");
                // case: path includes path variables that allow detection of '-' as a separator
                patterns.add("-(\\{[^\\/{}\\(\\)\\[\\]]+\\})");

                break;

            default:
                patterns.add("^((\\/|" + separator + ")((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+))+("
                        + separator + "|\\/)?$");

        }

        for (String p : patterns) {
            Pattern stringPattern = Pattern.compile(p);
            Matcher matcher = stringPattern.matcher(path);
            if (matcher.find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * searches a path for a set of separators
     *
     * @param path
     * @return list of violations
     */
    private List<Violation> findInvalidSeparators(String path) {
        List<Violation> violationList = new ArrayList<>();

        for (char c : separators) {
            if (c != '/') {
                boolean isSeparator = checkForSeparator(c, path);
                if (isSeparator) {
                    String suggestion = "replace '" + c
                            + "' with a forward slash '/' to indicate a hierarchical relationship";

                    violationList.add(new Violation(this, locMapper.getLOCOfPath(path), suggestion, path,
                            ErrorMessage.SEPARATOR));

                }
            }
        }
        return violationList;
    }

    /**
     * catches all Rule violations that arent handled with custom error messages
     *
     * @param currentSize
     * @param violationListSize
     * @param path
     * @return a violation
     */
    private Violation catchUnknownCase(int currentSize, int violationListSize, String path) {

        if (violationListSize == currentSize) {
            // check for '?' and '#' as they are illegal in paths
            if (path.contains("#") || path.contains("?")) {
                return new Violation(this, locMapper.getLOCOfPath(path), ImprovementSuggestion.SEPARATOR, path,
                        ErrorMessage.SEPARATOR);
            } else {
                return new Violation(this, locMapper.getLOCOfPath(path), ImprovementSuggestion.SEPARATOR_UNKNOWN, path,
                        ErrorMessage.SEPARATOR);
            }

        }
        return null;
    }

}
