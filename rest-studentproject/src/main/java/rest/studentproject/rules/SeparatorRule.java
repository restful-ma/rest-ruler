package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.rules.constants.RuleCategory;
import rest.studentproject.rules.constants.RuleSeverity;
import rest.studentproject.rules.constants.RuleSoftwareQualityAttribute;
import rest.studentproject.rules.constants.RuleType;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RULE: Forward slash separator (/) must be used to indicate a hierarchical relationship
 */
public class SeparatorRule implements IRestRule {

    static final String TITLE = "Forward slash separator (/) must be used to indicate a hierarchical relationship";
    static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    static final RuleSeverity RULE_SEVERITY = RuleSeverity.CRITICAL;
    static final RuleType RULE_TYPE = RuleType.STATIC;
    static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List.of(RuleSoftwareQualityAttribute.MAINTAINABILITY);

    private static final String ERROR_MESSAGE = "A forward slash '/' has to be used as a separator";
    boolean isActive;
    private static char[] separators = {'.', ':', ';', ',', '\\', '#', '/', '-', '?', '='};



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
    public RuleType getRuleType() {
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
        //expected Pattern
        Pattern expectedPattern = Pattern.compile("^(\\/((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+))+\\/?$");

        for (String path : pathList) {

            Matcher matcher = expectedPattern.matcher(path);

            int currentSize = violationList.size();
            //check if path has expected format
            if (!matcher.find()) {

                for (char c : separators) {
                    if ( c != '/') {
                        boolean isSeparator = checkForSeparator(c, path);
                        if (isSeparator) {
                            String suggestion = "replace '" + c + "' with a forward slash '/' to indicate a hierarchical relationship";
                            //lineViolation placeholder set as 0
                            violationList.add(new Violation(0, suggestion, path, ERROR_MESSAGE));

                        }
                    }
                }

                //unknown case:
                if (violationList.size() == currentSize){
                    //check for '?' and '#' as they are illegal in paths
                    if (path.contains("#") | path.contains("?")){
                        violationList.add(new Violation(0, "remove any '#' and '?' from the path", path, ERROR_MESSAGE));
                    }else {
                        violationList.add(new Violation(0, "-", path, ERROR_MESSAGE));
                    }

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
     * @return true or fals deoending on if it is a separator
     */
    private static boolean checkForSeparator(char separator, String path) {

        List<String> patterns = new ArrayList<>();

        // '=' and '-' are unique as they are valid characters in Paths and URLS
        switch (separator) {
            case '.':
                //escape regex operation characters, otherwise identical to default case
                patterns.add("^((\\/|\\.)((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+))*(\\.|\\/)?$");

                break;
            case '\\':
                //escape regex operation characters, otherwise identical to default case
                patterns.add("^((\\/|\\\\)((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+))*(\\\\|\\/)?$");

                break;
            case '?':
                //escape regex operation characters, otherwise identical to default case
                patterns.add("^((\\/|\\?)((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+))*(\\?|\\/)?$");

                break;
            case '=':
                // starts with '=' but afterwards follows expected pattern
                patterns.add("^=(((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&]+)\\/?)+$");
                //case: path includes path variables that allow detection of '=' as a separator
                patterns.add("=(\\{[^\\/{}\\(\\)\\[\\]]+\\})");
                patterns.add("^((\\/|" + separator + ")((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&]+))*(" + separator + "|\\/)?$");


                break;
            case '-':
                // starts with '-' but afterwards follows expected pattern
                patterns.add("^-(((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+)\\/?)+$");
                //case: path includes path variables that allow detection of '-' as a separator
                patterns.add("-(\\{[^\\/{}\\(\\)\\[\\]]+\\})");

                break;

            default:
                patterns.add("^((\\/|" + separator + ")((\\{[^\\/{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@%_\\+~&=]+))*(" + separator + "|\\/)?$");

        }

        for (String p: patterns) {
            Pattern stringPattern = Pattern.compile(p);
            Matcher matcher = stringPattern.matcher(path);
            if (matcher.find()){
                return true;
            }
        }

        return false;
    }

}
