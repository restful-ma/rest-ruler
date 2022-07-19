package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.report.Report;
import rest.studentproject.rules.UnderscoreRule;

public class RestAnalyzer {


    public Report runAnalyse(String url) {
        Report finalReport = new Report();
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(url, null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();


        // Iterates over all active rules
        UnderscoreRule underscoreRule = new UnderscoreRule(true);
        // TODO: Handle returned violation list
        underscoreRule.checkViolation(openAPI);

        return finalReport;
    }
}
