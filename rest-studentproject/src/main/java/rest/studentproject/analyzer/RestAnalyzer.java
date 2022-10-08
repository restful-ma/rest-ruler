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
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestAnalyzer {
    // Singleton
    private static final Report report = Report.getInstance();
    public static LOCMapper locMapper;
    public static Map<SecuritySchema, String> securitySchemas;
    public static boolean dynamicAnalysis = true;

    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public final OpenAPI openAPI;

    public RestAnalyzer(String path) {
        this.openAPI = Utility.getOpenAPI(path);
        locMapper = new LOCMapper(openAPI, path);
        locMapper.mapOpenAPIKeysToLOC();
    }

    public List<Violation> runAnalyse(List<IRestRule> activeRules, boolean generateReport) {
        List<Violation> violations = new ArrayList<>();
        int curRule = 1;
        for (IRestRule rule : activeRules) {
            if (!rule.getIsActive())
                continue;
            String info = String.format("Rule %d of %d is now checked:%n%s", curRule, activeRules.size(),
                    rule.getTitle());
            logger.log(Level.INFO, info);
            List<Violation> test = rule.checkViolation(this.openAPI);
            curRule++;
            violations.addAll(test);
        }
        // generates Report
        if (generateReport) {
            report.generateReport(violations);
        }
        return violations;
    }
}
