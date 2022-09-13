package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.RuleCategory;
import rest.studentproject.rule.constants.RuleSeverity;
import rest.studentproject.rule.constants.RuleSoftwareQualityAttribute;
import rest.studentproject.rule.constants.RuleType;

import java.util.ArrayList;
import java.util.List;

public class TunnelingRule implements IRestRule {

    private static final String TITLE = "GET and POST must not be used to tunnel other request methods";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.HTTP;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.CRITICAL;
    private static final RuleType RULE_TYPE = RuleType.STATIC;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List.of(RuleSoftwareQualityAttribute.MAINTAINABILITY, RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.FUNCTIONAL_SUITABILITY, RuleSoftwareQualityAttribute.USABILITY);
    private boolean isActive;

    public TunnelingRule(boolean isActive) {
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

        //TODO: execute CRUD rule and check if CRUD operator matches request Type

        //TODO: check GET RULE and list each violation as own

        //TODO: dynamic rule execution

        return new ArrayList<>();
    }
}
