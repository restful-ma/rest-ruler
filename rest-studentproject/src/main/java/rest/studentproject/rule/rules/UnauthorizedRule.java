package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;
import static rest.studentproject.analyzer.RestAnalyzer.securitySchemas;

/**
 * Implementation of the rule: 401 ("Unauthorized") must be used when there is a problem with the client's credentials
 */
public class UnauthorizedRule implements IRestRule {

    private static final String TITLE = "401 (\"Unauthorized\") must be used when there is a problem with the " +
            "client's credentials";
    private static final RuleCategory CATEGORY = RuleCategory.HTTP;
    private static final RuleType TYPE = RuleType.STATIC;
    private static final RuleSeverity SEVERITY = RuleSeverity.CRITICAL;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE =
            Arrays.asList(RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY,
                    RuleSoftwareQualityAttribute.USABILITY);
    private static final List<String> OPERATION_METHOD_NAMES = List.of("getGet", "getPut", "getPost", "getDelete",
            "getPatch", "getHead", "getOptions", "getTrace");
    private static final String OPERATION_METHOD_SECURITY = "getSecurity";
    private final List<Violation> violationList = new ArrayList<>();
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean isActive;

    public UnauthorizedRule(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getTitle() {
        return TITLE;
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
    public RuleType getRuleType() {
        return TYPE;
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
     * Checks if there is a violation against the 401 (unauthorized) rule. If the security is defined globally, the
     * 401 response must be defined for each request. If security is defined locally, the 401 response must be
     * defined only for the local request.
     *
     * @param openAPI the definition that will be checked against the rule.
     * @return the list of violations.
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<SecurityRequirement> security = openAPI.getSecurity();
        boolean globalSec = security != null && !security.isEmpty();

        for (Map.Entry<String, PathItem> path : openAPI.getPaths().entrySet()) {
            Map<String, Operation> operations = getPathOperationsWithSec(path.getValue(), globalSec);

            for (Map.Entry<String, Operation> operation : operations.entrySet()) {
                if (operation.getValue().getResponses().containsKey("401")) continue;

                this.violationList.add(new Violation(this, locMapper.getLOCOfPath(path.getKey()),
                        "Provide the 401 " + "response in the " + "definition of the path in the operation (here: " + operation.getKey() + ")", path.getKey(), ErrorMessage.UNAUTHORIZED));
            }
        }
        return this.violationList;
    }

    /**
     * Gives for the path only the operations which have a security defined.
     *
     * @param pathItem  the path item (swagger) with operations and their security, etc.
     * @param globalSec if the security is globally defined.
     * @return the list of operations for the specified path for which security has been defined.
     */
    private Map<String, Operation> getPathOperationsWithSec(PathItem pathItem, boolean globalSec) {
        Map<String, Operation> operations = new HashMap<>();

        for (String method : OPERATION_METHOD_NAMES) {
            try {
                Method operationMethod = PathItem.class.getMethod(method);
                Method securityMethod = Operation.class.getMethod(OPERATION_METHOD_SECURITY);

                Operation curOperation = (Operation) operationMethod.invoke(pathItem);

                if (curOperation != null && (globalSec || securityMethod.invoke(curOperation) != null)) {
                    operations.put(method.replace("get", "").toUpperCase(), curOperation);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                logger.severe("Exception when trying to get method to get the operations from a path: " + e.getMessage());
            }
        }

        return operations;
    }
}
