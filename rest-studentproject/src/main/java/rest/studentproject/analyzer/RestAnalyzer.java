package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.report.Report;
import rest.studentproject.rules.LowercaseRule;

import java.util.ArrayList;
import java.util.List;

public class RestAnalyzer {


    public Report runAnalyse(String url){
        // List<Report> reportList = new ArrayList<>();
        Report finalReport = new Report();
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(url, null, null);


        // foreach ( ) { }
        LowercaseRule lowercaseRule = new LowercaseRule();
        Report report = lowercaseRule.checkViolation(swaggerParseResult);
        // reportList.add(report);
        // report.id report.description ...  x49



        // finalReport.generateReport(reportList);


        return report;
    }
}
