package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.report.Report;

public class LowercaseRule implements IRestRule {

    @Override
    public String title() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public RuleCategory category() {

        return RuleCategory.URIS;
    }

    @Override
    public RuleSeverity severityType() {
        return null;
    }

    @Override
    public RuleType ruleType() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public Report checkViolation(OpenAPI openAPI) {
        Report report = new Report();
        report.descriptionReport = "";
        return report;
    }
}
