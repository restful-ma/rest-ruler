package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.report.Report;
import rest.studentproject.rules.HyphensRule;
import rest.studentproject.rules.LowercaseRule;
import rest.studentproject.rules.UnderscoreRule;

import java.io.IOException;

public class RestAnalyzer {


    public Report runAnalyse(String url) throws IOException {
        // List<Report> reportList = new ArrayList<>();
        Report finalReport = new Report();

        String dir = System.getProperty("user.dir");

        // directory from where the program was launched
        // e.g /home/mkyong/projects/core-java/java-io
        System.out.println(dir);
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation("rest-studentproject/bin/test/rest/studentproject/rules/hyphensTests/hyphensInvalid2ViolationsLowercase.json", null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();

//        LOCMapper locMapper = new LOCMapper(url, openAPI);

        // Iterates over all active rules
        // UnderscoreRule underscoreRule = new UnderscoreRule(true);
        // underscoreRule.checkViolation(openAPI);
        HyphensRule hyphensRule = new HyphensRule(true);
        hyphensRule.checkViolation(openAPI);



        // finalReport.generateReport(reportList);


        return finalReport;
    }
}
