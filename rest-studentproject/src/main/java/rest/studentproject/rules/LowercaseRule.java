package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LowercaseRule implements IRestRule {

    private static final String title = "Lowercase letters should be preferred in URI paths";
    private static final RuleCategory ruleCategory = RuleCategory.URIS;
    private static final RuleSeverity ruleSeverity = RuleSeverity.ERROR;
    private static final RuleType ruleType = RuleType.STATIC;
    private static final List<RuleSoftwareQualityAttribute> lstRuleSoftwareQualityAttribute = List.of(RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;

    public LowercaseRule(boolean isActive) {
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
        return LowercaseRule.ruleCategory;

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
        return LowercaseRule.ruleType;
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
                violations.add(new Violation(0, "", "", "Error at:" + path));
            }
                
        }
        
        return violations;
    }
}
