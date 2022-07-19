package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

public interface IRestRule {

    public String getTitle();
    public RuleCategory getCategory();
    public RuleSeverity getSeverityType();
    public RuleType getRuleType();
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute();
    public boolean getIsActive();
    public void setIsActive(boolean isActive);
    public List<Violation> checkViolation(OpenAPI openAPI);
}
