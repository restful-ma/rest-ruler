package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.report.Report;

public interface IRestRule {

    public String title();
    public String description();
    public RuleCategory category();
    public RuleSeverity severityType();
    public RuleType ruleType();
    public boolean isActive();
    public Report checkViolation(OpenAPI openAPI);
}
