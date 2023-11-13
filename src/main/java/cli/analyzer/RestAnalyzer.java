package cli.analyzer;

import cli.rule.IRestRule;
import cli.rule.Violation;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import cli.report.Report;
import cli.rule.constants.SecuritySchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestAnalyzer {
    // Singleton
    private static final Report report = Report.getInstance();
    public static LOCMapper locMapper;
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public final OpenAPI openAPI;

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
        }else{
            report.displayReport(violations);
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
        return violations;
    }

}
