package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Utility;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;
import rest.studentproject.utility.Output;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static rest.studentproject.analyzer.RestAnalyzer.*;

/**
 * Implementation of the rule: 401 ("Unauthorized") must be used when there is a
 * problem with the client's credentials
 */
public class UnauthorizedRule implements IRestRule {

    private static final String TITLE = "401 (\"Unauthorized\") must be used when there is a problem with the " +
            "client's credentials";
    private static final RuleCategory CATEGORY = RuleCategory.HTTP;
    private static final List<RuleType> TYPE = Arrays.asList(RuleType.STATIC, RuleType.DYNAMIC);
    private static final RuleSeverity SEVERITY = RuleSeverity.CRITICAL;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE = Arrays.asList(
            RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY,
            RuleSoftwareQualityAttribute.USABILITY);
    private static final List<String> OPERATION_METHOD_NAMES = List.of("getGet", "getPut", "getPost", "getDelete");
    private static final String OPERATION_METHOD_SECURITY = "getSecurity";
    private final List<Violation> violationList = new ArrayList<>();
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean isActive;
    private OpenAPI openAPI;
    private HttpURLConnection con;
    private int totalPaths;

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
    public List<RuleType> getRuleType() {
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
        staticAnalysis();
        if (dynamicAnalysis && !securitySchemas.isEmpty())
            dynamicAnalysis();

        return this.violationList;
    }

    /**
     * This method runs the dynamic analysis of the unauthorized rule. If there is
     * no sec defined in the openAPI
     * definition a request is made with an adapted (last char missing) token to
     * check if the 401 response is
     * returned. Limitations:
     * 1. only GET request methods are included in the analysis --> If sec is
     * defined by user
     * but no sec is required for e.g. POST --> resources will be deleted/updated
     * 2. when the server returns another response code than 401 --> not checked
     * (maybe implement AI that checks
     * response message)
     */
    private void dynamicAnalysis() {
        List<Server> servers = this.openAPI.getServers();
        Paths paths = this.openAPI.getPaths();

        int curPath = paths.size() + 1;
        // Iterate over each defined path
        for (Map.Entry<String, PathItem> path : paths.entrySet()) {
            Output.progressPercentage(curPath, this.totalPaths);
            curPath++;

            // If there is already a violation from static analysis skip this path
            if (checkViolationForPath(path.getKey()))
                continue;

            // All operations defined for each path
            Map<String, Operation> operations = getPathOperations(path.getValue(), false, false);

            // Iterate over operations
            for (Map.Entry<String, Operation> operation : operations.entrySet()) {

                // When 401 is defined in operation there is no violation
                if (operation.getValue().getResponses().containsKey("401"))
                    continue;

                // Dynamic analysis is only for GET implemented because else there is the
                // possibility to update/delete some resources
                if (!operation.getKey().equalsIgnoreCase("GET"))
                    continue;

                // Requests for each defined server
                for (Server server : servers) {
                    try {
                        boolean found401 = false;

                        // Make request for each security defined by the user
                        for (Map.Entry<SecuritySchema, String> sec : securitySchemas.entrySet()) {
                            URL url = Utility.getURL(sec.getKey(), sec.getValue(), server.getUrl(), path.getKey());

                            this.con = Utility.createHttpConnection(url,
                                    RequestType.valueOf(operation.getKey().toUpperCase()));

                            // If connection fails
                            if (this.con == null) {
                                logger.severe("Error occurred when creating the http connection");
                                continue;
                            }

                            setAuthHeader(sec.getKey(), sec.getValue());

                            int status = this.con.getResponseCode();

                            if (status == 401)
                                found401 = true;
                        }

                        // If for each defined security there is no 401 --> violation
                        if (found401)
                            violationList.add(new Violation(this, locMapper.getLOCOfPath(path.getKey()), "Provide" +
                                    " the 401 " + "response in the " + "definition of the path in the " + "operation"
                                    + " " + "(here: " + operation.getKey() + ") --> Found dynamic", path.getKey(),
                                    ErrorMessage.UNAUTHORIZED));
                    } catch (IOException e) {
                        logger.severe("Exception on trying to request: " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * To check if there is already a violation for the path from the static
     * analysis.
     *
     * @param path the path to check if violation already exists for.
     * @return true if a violation exists for path --> else false
     */
    private boolean checkViolationForPath(String path) {
        for (Violation violation : this.violationList) {
            if (violation.getKeyViolation().equals(path))
                return true;
        }
        return false;
    }

    /**
     * Adds the body on the basis of the used security schema to the existing http
     * url connection.
     *
     * @param securitySchema the used security schema from the user (e.g. BASIC,
     *                       BEARER, APIKEY)
     * @param pw             the password for the used security schema
     */
    private void setAuthHeader(SecuritySchema securitySchema, String pw) {
        switch (securitySchema) {
            case BASIC:
                String encoding = Base64.getEncoder().encodeToString(pw.getBytes(StandardCharsets.UTF_8));
                // Token with one char missing to trigger 401
                this.con.setRequestProperty("Authorization", "Basic " + encoding.substring(0, encoding.length() - 1));
                break;
            case BEARER:
                // Token with one char missing to trigger 401
                this.con.setRequestProperty("Authorization", "Bearer " + pw.substring(0, pw.length() - 1));
                break;
            case APIKEY:
                break;
            default:
                logger.log(Level.SEVERE, "This security schema is currently not supported");

        }
    }

    /**
     * This method analyses the openAPI definition statically. Either the security
     * is globally defined --> each path
     * needs 401 response; or the security is locally defined --> only paths with
     * defined security need the 401 response
     */
    private void staticAnalysis() {
        List<SecurityRequirement> security = this.openAPI.getSecurity();
        boolean globalSec = security != null && !security.isEmpty();
        Paths paths = this.openAPI.getPaths();

        int curPath = 1;
        this.totalPaths = paths.keySet().size() * 2;
        for (Map.Entry<String, PathItem> path : paths.entrySet()) {
            Output.progressPercentage(curPath, this.totalPaths);
            curPath++;

            Map<String, Operation> operations = getPathOperations(path.getValue(), globalSec, true);

            for (Map.Entry<String, Operation> operation : operations.entrySet()) {
                if (operation.getValue().getResponses().containsKey("401"))
                    continue;

                this.violationList.add(new Violation(this, locMapper.getLOCOfPath(path.getKey()),
                        "Provide the 401 " + "response in the " + "definition of the path in the operation (here: "
                                + operation.getKey() + ")",
                        path.getKey(), ErrorMessage.UNAUTHORIZED));
            }
        }
    }

    /**
     * Gives for the path all the operations defined.
     *
     * @param pathItem    the path item (swagger) with operations and their
     *                    security, etc.
     * @param globalSec   if the security is globally defined.
     * @param onlyWithSec true when operations do have security defined --> false
     *                    every operation needed
     * @return the list of operations for the specified path
     */
    private Map<String, Operation> getPathOperations(PathItem pathItem, boolean globalSec, boolean onlyWithSec) {
        Map<String, Operation> operations = new HashMap<>();

        for (String method : OPERATION_METHOD_NAMES) {
            try {
                Method operationMethod = PathItem.class.getMethod(method);
                Method securityMethod = Operation.class.getMethod(OPERATION_METHOD_SECURITY);

                Operation curOperation = (Operation) operationMethod.invoke(pathItem);

                if (curOperation == null)
                    continue;

                Object secOb = securityMethod.invoke(curOperation);

                if (!onlyWithSec || (globalSec && secOb == null) || (secOb != null && !secOb.toString().equals("[]"))) {
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
