package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.report.Report;

import java.util.List;

public class LowercaseRule implements IRestRule {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public RuleCategory getCategory() {
        return null;
    }

    @Override
    public RuleSeverity getSeverityType() {
        return null;
    }

    @Override
    public RuleType getRuleType() {
        return null;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return null;
    }

    @Override
    public boolean getIsActive() {
        return false;
    }

    @Override
    public void setIsActive(boolean isActive) {

    }

    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        return null;
    }
}
