package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.report.Report;
import rest.studentproject.rules.SeparatorChecker;
import rest.studentproject.rules.UnderscoreRule;
import rest.studentproject.rules.Violation;

import java.util.List;

public class RestAnalyzer {


    public Report runAnalyse(String url){
        // List<Report> reportList = new ArrayList<>();
        Report finalReport = new Report();
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(url, null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();

//        LOCMapper locMapper = new LOCMapper(url, openAPI);

        // Iterates over all active rules
        //UnderscoreRule underscoreRule = new UnderscoreRule(true);
        //underscoreRule.checkViolation(openAPI);

        SeparatorChecker separatorChecker = new SeparatorChecker();
        List<Violation> violations = separatorChecker.checkViolation(openAPI);

        for (Violation v: violations) {
            System.out.println(v.getErrorMessage());
        }

        // finalReport.generateReport(reportList);


        return finalReport;
    }
}
