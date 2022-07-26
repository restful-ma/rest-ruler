package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.report.Report;
import rest.studentproject.rules.SeparatorChecker;
import rest.studentproject.rules.UnderscoreRule;
import rest.studentproject.rules.Violation;

import java.util.ArrayList;
import java.util.List;

public class RestAnalyzer {


    public void runAnalyse(String url) {
        Report report = Report.getInstance();
        List<Violation> violations = new ArrayList<>();

        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(url, null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();


        // Iterates over all active rules
        UnderscoreRule underscoreRule = new UnderscoreRule(true);
        // TODO: Handle returned violation list
        List<Violation> underscoreViolations = underscoreRule.checkViolation(openAPI);
        violations.addAll(underscoreViolations);

        SeparatorChecker separatorChecker = new SeparatorChecker();
        List<Violation> separatorViolations = separatorChecker.checkViolation(openAPI);
        violations.addAll(separatorViolations);

        report.generateReport(violations);

    }
}
