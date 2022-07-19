package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.rules.attributes.RuleCategory;
import rest.studentproject.rules.attributes.RuleSeverity;
import rest.studentproject.rules.attributes.RuleSoftwareQualityAttribute;
import rest.studentproject.rules.attributes.RuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LowercaseRule implements IRestRule {

    private String title = "Lowercase letters should be preferred in URI paths";
    private RuleCategory ruleCategory = RuleCategory.URIS;
    private RuleSeverity ruleSeverity = RuleSeverity.ERROR;
    private RuleType ruleType = RuleType.STATIC;
    private List<RuleSoftwareQualityAttribute> lstRuleSoftwareQualityAttribute = List.of(RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;

    public LowercaseRule(boolean isActive) {
        this.isActive = isActive;
    }


    /**
     *
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     *
     */
    @Override
    public RuleCategory getCategory() {
        return this.ruleCategory;

    }

    /**
     *
     */
    @Override
    public RuleSeverity getSeverityType() {
        return this.ruleSeverity;
    }

    /**
     *
     */
    @Override
    public RuleType getRuleType() {
        return this.ruleType;
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
     *
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violations = new ArrayList<>();
        String start = "\\{";
        String end = "\\}";
        Set<String> paths = openAPI.getPaths().keySet();

        for (String path: paths) {
            String updatedUrl = path.replaceAll(start + ".*" + end, "");
            String updateUrlLowerCase = updatedUrl.toLowerCase();

            if(!updateUrlLowerCase.equals(updatedUrl)){
                violations.add(new Violation(0, "", "", "Error at:" + path));
            }
                
        }
        

        return violations;

    }
}
