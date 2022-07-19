package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.rules.attributes.RuleCategory;
import rest.studentproject.rules.attributes.RuleSeverity;
import rest.studentproject.rules.attributes.RuleSoftwareQualityAttribute;
import rest.studentproject.rules.attributes.RuleType;

import java.util.ArrayList;
import java.util.List;

public class HyphensRule implements IRestRule {

    private boolean isActive = false;
    private final List<Violation> violationList = new ArrayList<>();

    @Override
    public String getTitle() {
        return "Hyphens (-) should be used to improve the readability of URIs";
    }

    @Override
    public RuleCategory getCategory() {
        return RuleCategory.URIS;
    }

    @Override
    public RuleSeverity getSeverityType() {
        return RuleSeverity.ERROR;
    }

    @Override
    public RuleType getRuleType() {
        return RuleType.STATIC;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        List<RuleSoftwareQualityAttribute> softwareQualityAttributes = new ArrayList<>();
        softwareQualityAttributes.add(RuleSoftwareQualityAttribute.MAINTAINABILITY);
        return softwareQualityAttributes;
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
