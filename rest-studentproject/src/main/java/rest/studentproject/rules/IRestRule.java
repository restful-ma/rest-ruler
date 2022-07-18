package rest.studentproject.rules;

import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.report.Report;

public interface IRestRule {

    public String title();
    public String description();
    public String category();
    public String severityType();
    public String ruleType();
    public boolean isActive();
    public Report checkViolation(SwaggerParseResult swaggerParseResult);
}
