package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.report.Report;
import rest.studentproject.rules.UnderscoreRule;
import rest.studentproject.rules.Violation;

import java.net.MalformedURLException;

public class RestAnalyzer {

    public Report runAnalyse(String url) throws MalformedURLException {
        Report finalReport = new Report();
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(url, null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();

        LOCMapper locMapper = new LOCMapper(openAPI, url);

        // Iterates over all active rules
        UnderscoreRule underscoreRule = new UnderscoreRule(true, locMapper);
        // TODO: Handle returned violation list
        for (Violation violation : underscoreRule.checkViolation(openAPI)){
            System.out.println(violation.getLineViolation());
            System.out.println(violation.getKeyViolation());
            System.out.println(violation.getErrorMessage());
            System.out.println(violation.getImprovementSuggestion());
        }


        return finalReport;
    }
}
