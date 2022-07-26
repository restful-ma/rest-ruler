package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.rules.attributes.RuleCategory;
import rest.studentproject.rules.attributes.RuleSeverity;
import rest.studentproject.rules.attributes.RuleSoftwareQualityAttribute;
import rest.studentproject.rules.attributes.RuleType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RULE: Forward slash separator (/) must be used to indicate a hierarchical relationship
 */
public class SeparatorChecker implements IRestRule {

    String title;
    boolean isActive;
    RuleCategory ruleCategory;
    RuleSeverity ruleSeverity;
    RuleType ruleType;
    List<RuleSoftwareQualityAttribute> qualityAttributes;

    private final static String ERROR_MESSAGE = "A forward slash '/' has to be used as a separator";
    private static char[] separators = { '-', '.', ':', ';', ',', '\\','#', '/'};
    //TODO:
    //http://regexr.com/?37i6s

    public SeparatorChecker(){
        title = "Forward slash separator (/) must be used to indicate a hierarchical relationship";
        isActive = true;
        ruleCategory = RuleCategory.URIS;
        ruleSeverity = RuleSeverity.CRITICAL;
        ruleType = RuleType.STATIC;
        qualityAttributes = new ArrayList<>();
        qualityAttributes.add(RuleSoftwareQualityAttribute.MAINTAINABILITY);
    }
    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public RuleCategory getCategory() {
        return this.ruleCategory;
    }

    @Override
    public RuleSeverity getSeverityType() {
        return this.ruleSeverity;
    }

    @Override
    public RuleType getRuleType() {
        return this.ruleType;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return this.qualityAttributes;
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
        //TODO: Open API
        Set<String> paths = openAPI.getPaths().keySet();
        return checkSeparator(paths);
    }

    /**
     * checks a given path for potential rule violations
     * @param pathList
     * @return
     */
    public List<Violation> checkSeparator(Set<String> pathList){

        List<Violation> violationList = new ArrayList<>();
        //expected Pattern
        Pattern expectedPattern = Pattern.compile("^(\\/((\\{[^{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@:%_\\+.~#?&=]+))+$");

        for (String path: pathList) {

            Matcher matcher = expectedPattern.matcher(path);
            if (matcher.find()){
                //check....
            }else {
                //check if a different character is used as a separator
                Map<Character, Long> appearances = countPossibleSeparators(path);
                long ForwardSlashAppearances = appearances.get('/');

                for (char c: appearances.keySet()) {
                    if (appearances.get(c) >= ForwardSlashAppearances && c != '/'){
                        boolean isSeparator = checkForSeparator(c, path);
                        if (isSeparator){
                            String suggestion = "replace '" + c + "' with a forward slash '/' to indicate a hierarchical relationship";
                            //TODO: lineViolation placeholder
                            violationList.add(new Violation(0,suggestion,path,ERROR_MESSAGE));
                        }
                    }
                }
                //return false;
            }
        }
        return violationList;
    }

    /**
     * checks a String for possible separators
     * @param separator separator candidate
     * @param path path under test
     * @return true or fals deoending on if it is a separator
     */
    private static boolean checkForSeparator(char separator, String path){

        Pattern pattern;

        //escape regex operation characters
        switch (separator){
            case '.':
                pattern = Pattern.compile("^(" + "\\." + "((\\{[^{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@:%_\\+.~#?&=]+))+$");
                break;
            case '\\':
                pattern = Pattern.compile("^(" + "\\\\" + "((\\{[^{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@:%_\\+.~#?&=]+))+$");
                break;
            default:
                pattern = Pattern.compile("^(" + separator + "((\\{[^{}\\(\\)\\[\\]]+\\})|[-a-zA-Z0-9@:%_\\+.~#?&=]+))+$");
        }

        Matcher matcher = pattern.matcher(path);

        return matcher.find();
    }

    /**
     * counts all appearances of possible separator characters
     * @param url
     * @return
     */
    private static Map<Character, Long> countPossibleSeparators(String url){

        HashMap<Character, Long> appearances = new HashMap<>();
        //count appearance
        for (int i=0; i< separators.length; i++) {
            char separator = separators[i];
            long num = url.chars().filter(ch -> ch == separator).count();
            appearances.put(separator,num);
        }
        return appearances;
    }

}
