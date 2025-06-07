package cli.rule.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.ErrorMessage;
import cli.rule.constants.RuleCategory;
import cli.rule.constants.RuleIdentifier;
import cli.rule.constants.RuleSeverity;
import cli.rule.constants.RuleSoftwareQualityAttribute;
import cli.utility.Output;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.Paths;


/**
 * Implementation of the rule: Must use official HTTP status codes.
 */
public class HTTPStatusRule implements IRestRule {
    private static final String TITLE = "Must use official HTTP status codes";
    private static final RuleIdentifier RULE_IDENTIFIER = RuleIdentifier.OFFICIAL_HTTP_STATUS_CODES;
    private static final RuleCategory CATEGORY = RuleCategory.HTTP;
    private static final RuleSeverity SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE = Arrays.asList(
            RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY,
            RuleSoftwareQualityAttribute.USABILITY);
    // source of truth for the codes here is: https://www.iana.org/assignments/http-status-codes/http-status-codes.xhtml
    private static final Set<Integer> OFFICIAL_HTTP_STATUS_CODES = new HashSet<>(Arrays.asList(
            100, 101, 102, 103, 104,
            200, 201, 202, 203, 204, 205, 206, 207, 208, 226,
            300, 301, 302, 303, 304, 305, 307, 308,
            400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417,
            421, 422, 423, 424, 425, 426, 428, 429, 431, 451,
            500, 501, 502, 503, 504, 505, 506, 507, 508, 510, 511
    ));
    
    private boolean isActive;

    public HTTPStatusRule(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public RuleIdentifier getIdentifier() {
        return RULE_IDENTIFIER;
    }

    @Override
    public RuleCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public RuleSeverity getSeverityType() {
        return SEVERITY;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return SOFTWARE_QUALITY_ATTRIBUTE;
    }

    @Override
    public boolean getIsActive() {
        return this.isActive;
    }

    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violationList = new ArrayList<>();
        Paths paths = openAPI.getPaths();

        if (paths == null) {
            return violationList;
        }

        int curPath = 1;
        int totalPaths = paths.keySet().size();

        for (Map.Entry<String, PathItem> path : paths.entrySet()) {
            Output.progressPercentage(curPath, totalPaths);
            curPath++;

            // get all operations for this path
            for (Operation operation : path.getValue().readOperations()) {
                if (operation.getResponses() == null) {
                    continue;
                }

                for (Map.Entry<String, ApiResponse> response : operation.getResponses().entrySet()) {
                    String statusCode = response.getKey();
                    
                    // skip 'default' responses as they are valid
                    if (statusCode.equals("default")) {
                        continue;
                    }

                    // check if the status code is numeric, if not it is a violation
                    if (!statusCode.matches("\\d+")) {
                        String message = String.format(ErrorMessage.HTTP_STATUS_CODE_NOT_NUMERIC, statusCode);
                        violationList.add(new Violation(this,
                            RestAnalyzer.locMapper.getLOCOfPath(path.getKey()),
                            "Use a numeric HTTP status code from the IANA registry",
                            path.getKey(),
                            message));
                        continue;
                    }

                    // now we know it's numeric, so we can safely parse it
                    int code = Integer.parseInt(statusCode);
                    if (!OFFICIAL_HTTP_STATUS_CODES.contains(code)) {
                        String message = String.format(ErrorMessage.HTTP_STATUS_CODE_NOT_OFFICIAL,
                                code, operation.getOperationId(), path.getKey());
                        violationList.add(new Violation(this, 
                            RestAnalyzer.locMapper.getLOCOfPath(path.getKey()),
                            "Use an official HTTP status code from the IANA registry",
                            path.getKey(),
                            message));
                    }
                }
            }
        }

        return violationList;
    }
}
