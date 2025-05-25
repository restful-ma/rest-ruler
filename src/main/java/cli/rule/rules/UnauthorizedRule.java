package cli.rule.rules;

import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.*;
import cli.utility.Output;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

import static cli.analyzer.RestAnalyzer.*;

/**
 * Implementation of the rule: 401 ("Unauthorized") must be used when there is a
 * problem with the client's credentials
 */
public class UnauthorizedRule implements IRestRule {

    private static final String TITLE = "401 (\"Unauthorized\") must be used when there is a problem with the client's credentials";
    private static final RuleIdentifier RULE_IDENTIFIER = RuleIdentifier.UNAUTHORIZED;
    private static final RuleCategory CATEGORY = RuleCategory.HTTP;
    private static final RuleSeverity SEVERITY = RuleSeverity.CRITICAL;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE = Arrays.asList(
            RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY,
            RuleSoftwareQualityAttribute.USABILITY);
    private static final List<String> OPERATION_METHOD_NAMES = List.of("getGet", "getPut", "getPost", "getDelete",
            "getPatch");
    private static final String OPERATION_METHOD_SECURITY = "getSecurity";
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean isActive;
    private OpenAPI openAPI;

    public UnauthorizedRule(boolean isActive) {
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

    /**
     * Checks if there is a violation against the 401 (unauthorized) rule. If the
     * security is defined globally, the
     * 401 response must be defined for each request. If security is defined
     * locally, the 401 response must be
     * defined only for the local request.
     *
     * @param openAPI the definition that will be checked against the rule.
     * @return the list of violations.
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        this.openAPI = openAPI;

        return staticAnalysis();
    }

    /**
     * This method analyses the openAPI definition statically. Either the security
     * is globally defined --> each path
     * needs 401 response; or the security is locally defined --> only paths with
     * defined security need the 401 response
     */
    private List<Violation> staticAnalysis() {
        List<Violation> violationList = new ArrayList<>();
        List<SecurityRequirement> security = this.openAPI.getSecurity();
        boolean globalSec = security != null && !security.isEmpty();
        Paths paths = this.openAPI.getPaths();

        int curPath = 1;
        int totalPaths = paths.keySet().size();
        for (Map.Entry<String, PathItem> path : paths.entrySet()) {
            Output.progressPercentage(curPath, totalPaths);
            curPath++;

            Map<String, Operation> operations = getPathOperations(path.getValue(), globalSec);

            for (Map.Entry<String, Operation> operation : operations.entrySet()) {
                if (operation.getValue().getResponses().containsKey("401"))
                    continue;

                violationList.add(new Violation(this, locMapper.getLOCOfPath(path.getKey()),
                        "Provide the 401 " + "response in the " + "definition of the path in the operation (here: "
                                + operation.getKey() + ")",
                        path.getKey(), ErrorMessage.UNAUTHORIZED));
            }
        }
        return violationList;
    }

    /**
     * Gives for the path all the operations defined.
     *
     * @param pathItem    the path item (swagger) with operations and their
     *                    security, etc.
     * @param globalSec   if the security is globally defined.
     * @return the list of operations for the specified path
     */
    private Map<String, Operation> getPathOperations(PathItem pathItem, boolean globalSec) {
        Map<String, Operation> operations = new HashMap<>();

        for (String method : OPERATION_METHOD_NAMES) {
            try {
                Method operationMethod = PathItem.class.getMethod(method);
                Method securityMethod = Operation.class.getMethod(OPERATION_METHOD_SECURITY);

                Operation curOperation = (Operation) operationMethod.invoke(pathItem);

                if (curOperation == null)
                    continue;

                Object secOb = securityMethod.invoke(curOperation);

                if ((globalSec && secOb == null) || (secOb != null && !secOb.toString().equals("[]"))) {
                    operations.put(method.replace("get", "").toUpperCase(), curOperation);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                logger.severe(
                        "Exception when trying to get method to get the operations from a path: " + e.getMessage());
            }
        }

        return operations;
    }
}
