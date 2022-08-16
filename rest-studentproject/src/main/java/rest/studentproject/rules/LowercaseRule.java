package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.rules.constants.RuleCategory;
import rest.studentproject.rules.constants.RuleSeverity;
import rest.studentproject.rules.constants.RuleSoftwareQualityAttribute;
import rest.studentproject.rules.constants.RuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;

public class LowercaseRule implements IRestRule {

    private static final String TITLE = "Lowercase letters should be preferred in URI paths";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final RuleType RULE_TYPE = RuleType.STATIC;
    private static final List<RuleSoftwareQualityAttribute> RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST = List.of(RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;

    public LowercaseRule(boolean isActive) {
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
        return LowercaseRule.RULE_CATEGORY;

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
        return LowercaseRule.RULE_TYPE;
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
     * Rule to check if the URI path contains only lowercase letters. If not, the rule is violated. 
     * @param openAPI
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violations = new ArrayList<>();

        // Define the regex to match the curly braces
        String start = "\\{";
        String end = "\\}";

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();
        
        if(paths.isEmpty()) return violations;
        // Loop through the paths
        for (String path: paths) {
            if(path.trim().equals("")) continue;
            // Get the path without the curly braces
            String pathWithoutParameters = path.replaceAll(start + ".*" + end, "");
            // Get the path in lowercase
            String pathWithoutParametersLowerCase = pathWithoutParameters.toLowerCase();
            // Check if the path contains only lowercase letters
            if(!pathWithoutParametersLowerCase.equals(pathWithoutParameters)){
                violations.add(new Violation(locMapper.getLOCOfPath(path), "", "", "Error at:" + path));
            }
                
        }
        
        return violations;
    }
}
