package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import rest.studentproject.report.Report;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;

import java.util.ArrayList;
import java.util.List;

public class RestAnalyzer {
    public static LOCMapper locMapper;
    private final OpenAPI openAPI;
    //Singleton
    private static final Report report = Report.getInstance();


    /**
     * Constructor
     * @param url location of the OpenAPI file (link or file path)
     */
    public RestAnalyzer(String url) {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(url, null, null);
        this.openAPI = swaggerParseResult.getOpenAPI();
        locMapper = new LOCMapper(openAPI, url);
        locMapper.mapOpenAPIKeysToLOC();
    }


    /**
     * executes rule checking analysis for provided list of rules. Optionally generates a report file.
     * returns a list of all violations found
     * @param activeRules all rules to be checked for
     * @param generateReport generates report if boolean value true
     * @return list of all violation objects
     */
    public List<Violation> runAnalyse(List<IRestRule> activeRules, boolean generateReport) {
        List<Violation> violations = runRuleViolationChecks(activeRules);

        //generates Report
        if (generateReport) {
            report.generateReport(violations);
        }
        return violations;
    }

    /**
     * executes rule checking analysis for provided list of rules. Generates a report file with a specified title.
     * returns a list of all violations found.
     * @param activeRules all rules to be checked for
     * @param title name for the report file
     * @return list of all violation objects
     */
    public List<Violation> runAnalyse(List<IRestRule> activeRules, String title){

        //execute all active Rule Checks
        List<Violation> violations = runRuleViolationChecks(activeRules);

        //generate Report with custom title
        report.generateReport(violations,title);

        return violations;
    }

    /**
     * executes a Rule check of a provided list of rules
     * @param activeRules all Rules to be executed
     * @return list of all Violations for the set of rules
     */
    private List<Violation> runRuleViolationChecks (List<IRestRule> activeRules){
        List<Violation> violations = new ArrayList<>();
        for (IRestRule rule : activeRules) {
            if (!rule.getIsActive()) continue;
            violations.addAll(rule.checkViolation(this.openAPI));
        }
        return violations;
    }

}
