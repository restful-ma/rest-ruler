package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.report.Report;
import rest.studentproject.rules.SeparatorRule;
import rest.studentproject.rules.UnderscoreRule;
import rest.studentproject.rules.Violation;

import java.util.List;

public class RestAnalyzer {


    public Report runAnalyse(String url) {
        Report finalReport = new Report();
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(url, null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();


        // Iterates over all active rules
        UnderscoreRule underscoreRule = new UnderscoreRule(true);
        // TODO: Handle returned violation list
        underscoreRule.checkViolation(openAPI);

        SeparatorRule separatorRule = new SeparatorRule(true);
        List<Violation> violations = separatorRule.checkViolation(openAPI);

        for (Violation v: violations) {
            System.out.println(v.getErrorMessage());
        }

        return finalReport;
    }
}
