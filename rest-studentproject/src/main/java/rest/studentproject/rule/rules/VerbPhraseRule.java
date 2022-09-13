package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.RuleCategory;
import rest.studentproject.rule.constants.RuleSeverity;
import rest.studentproject.rule.constants.RuleSoftwareQualityAttribute;
import rest.studentproject.rule.constants.RuleType;

import java.util.List;

public class VerbPhraseRule implements IRestRule {

    private static final String TITLE = "A verb or verb phrase should be used for controller names";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final RuleType RULE_TYPE = RuleType.STATIC;
    private static final List<RuleSoftwareQualityAttribute> RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST = List.of(RuleSoftwareQualityAttribute.USABILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;

    public VerbPhraseRule(boolean isActive) {
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
        return null;
    }
}
