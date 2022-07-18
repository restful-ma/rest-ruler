package rest.studentproject.rules;

import io.swagger.v3.parser.core.models.SwaggerParseResult;
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
    public String category() {
        return null;
    }

    @Override
    public String severityType() {
        return null;
    }

    @Override
    public String ruleType() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public Report checkViolation(SwaggerParseResult swaggerParseResult) {
        Report report = new Report();
        report.descriptionReport = "";
        return report;
    }
}
