package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.rules.constants.RuleCategory;
import rest.studentproject.rules.constants.RuleSeverity;
import rest.studentproject.rules.constants.RuleSoftwareQualityAttribute;
import rest.studentproject.rules.constants.RuleType;

import java.util.List;

public class GetResourceRule implements IRestRule{

    static final String TITLE = "GET must be used to retrieve a representation of a resource";
    static final RuleCategory RULE_CATEGORY = RuleCategory.HTTP;
    static final RuleSeverity RULE_SEVERITY = RuleSeverity.CRITICAL;
    static final RuleType RULE_TYPE = RuleType.STATIC;
    static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List.of(RuleSoftwareQualityAttribute.MAINTAINABILITY, RuleSoftwareQualityAttribute.COMPATIBILITY,
            RuleSoftwareQualityAttribute.FUNCTIONAL_SUITABILITY, RuleSoftwareQualityAttribute.USABILITY);
    boolean isActive;

    public GetResourceRule (boolean isActive){
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
        return null;
    }
}
