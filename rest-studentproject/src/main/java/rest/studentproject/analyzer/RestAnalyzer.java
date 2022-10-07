package rest.studentproject.analyzer;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.report.Report;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Utility;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.SecuritySchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestAnalyzer {
    //Singleton
    private static final Report report = Report.getInstance();
    public static LOCMapper locMapper = null;
    public static Map<SecuritySchema, String> securitySchemas = null;
    public static boolean dynamicAnalysis = true;

    public final OpenAPI openAPI;


    public RestAnalyzer(String path) {
        this.openAPI = Utility.getOpenAPI(path);
        locMapper = new LOCMapper(openAPI, path);
        locMapper.mapOpenAPIKeysToLOC();
    }

    public List<Violation> runAnalyse(List<IRestRule> activeRules, boolean generateReport) {
        List<Violation> violations = new ArrayList<>();
        for (IRestRule rule : activeRules) {
            if (!rule.getIsActive()) continue;
            List<Violation> test = rule.checkViolation(this.openAPI);
            violations.addAll(test);
        }
        //generates Report
        if (generateReport) {
            report.generateReport(violations);
        }
        return violations;
    }
}
