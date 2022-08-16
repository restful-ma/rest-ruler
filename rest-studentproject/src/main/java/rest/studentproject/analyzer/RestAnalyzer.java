package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import rest.studentproject.report.Report;
import rest.studentproject.rules.IRestRule;
import rest.studentproject.rules.Violation;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;

public class RestAnalyzer {
    public static LOCMapper locMapper = null;
    private final OpenAPI openAPI;
    //Singleton
    private static final Report report = Report.getInstance();


    public RestAnalyzer(String url) throws MalformedURLException {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(url, null, null);
        this.openAPI = swaggerParseResult.getOpenAPI();
        locMapper = new LOCMapper(openAPI, url);
        locMapper.mapOpenAPIKeysToLOC();

    }


    public List<Violation> runAnalyse(List<IRestRule> activeRules, boolean generateReport) {
        List<Violation> violations = new ArrayList<>();
        for (IRestRule rule : activeRules) {
            if (!rule.getIsActive()) continue;
            violations.addAll(rule.checkViolation(this.openAPI));
        }
        //generates Report
        if (generateReport) {
            report.generateReport(violations);
        }


        return violations;
    }
}
