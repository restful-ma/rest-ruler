package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Request;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;
import static rest.studentproject.analyzer.RestAnalyzer.securitySchemas;
import static rest.studentproject.analyzer.RestAnalyzer.dynamicAnalysis;

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
    private static final List<String> OPERATION_METHOD_NAMES = List.of("getGet", "getPut", "getPost", "getDelete");
    private static final String OPERATION_METHOD_SECURITY = "getSecurity";
    private final List<Violation> violationList = new ArrayList<>();
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
        this.openAPI = openAPI;
        staticAnalysis();
        if (dynamicAnalysis && !securitySchemas.isEmpty()) dynamicAnalysis();

        return this.violationList;
    }

    private void dynamicAnalysis() {
        Request request;

        List<Server> servers = this.openAPI.getServers();
        boolean violationFound = false;
        for (Map.Entry<String, PathItem> path : this.openAPI.getPaths().entrySet()) {
            for (Violation violation : this.violationList) {
                System.out.println("violation.getKeyViolation()");
                System.out.println(violation.getKeyViolation());
                System.out.println("path.getKey()");
                System.out.println(path.getKey());
                violationFound = violation.getKeyViolation().equals(path.getKey());
                if (violationFound) break;
            }

            if (violationFound) continue;

            Map<String, Operation> operations = getPathOperations(path.getValue(), false, false);

            for (Map.Entry<String, Operation> operation : operations.entrySet()) {
                if (operation.getValue().getResponses().containsKey("401")) continue;

                if (!operation.getKey().equalsIgnoreCase("GET")) continue;

                for (Server server : servers) {
                    // path.getKey????
                    // request for every server???
                    request = new Request(path.getKey(), server.getUrl(),
                            RequestType.valueOf(operation.getKey().toUpperCase()));

                    System.out.println(operation.getKey().toUpperCase() + " Path: " + request.getUrl() + path.getKey());
                    try {

                        for (Map.Entry<SecuritySchema, String> sec : securitySchemas.entrySet()) {
                            URL url;
                            if (sec.getKey() == SecuritySchema.APIKEY)
                                url = new URL(request.getUrl() + path.getKey() + "?api_key=" + sec.getValue().substring(0, sec.getValue().length() - 1));
                            else url = new URL(request.getUrl() + path.getKey());

                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setRequestMethod(operation.getKey().toUpperCase());
                            con.setConnectTimeout(5000);
                            con.setReadTimeout(5000);
                            con.setDoOutput(true);
                            con.setInstanceFollowRedirects(false);

                            switch (sec.getKey()) {
                                case BASIC:
                                    String encoding =
                                            Base64.getEncoder().encodeToString((sec.getValue()).getBytes(StandardCharsets.UTF_8));
                                    // Token with one char missing to trigger 401
                                    con.setRequestProperty("Authorization", "Basic " + encoding.substring(0,
                                            encoding.length() - 1));
                                    break;
                                case BEARER:
                                    // Token with one char missing to trigger 401
                                    con.setRequestProperty("Authorization", "Bearer " + sec.getValue().substring(0,
                                            sec.getValue().length() - 1));
                                    break;
                                case APIKEY:
                                    break;
                                default:
                                    logger.severe("This request type (" + request.getRequestType() + ") is currently "
                                            + "not " + "supported");

                            }
                            int status = con.getResponseCode();
                            System.out.println("Response Code: " + status);
                            if (status != 401)
                                violationList.add(new Violation(this, locMapper.getLOCOfPath(path.getKey()), "Provide"
                                        + " the 401 " + "response in the " + "definition of the path in the operation" +
                                        " " + "(here: " + operation.getKey() + ") --> Found dynamic", path.getKey(),
                                        ErrorMessage.UNAUTHORIZED));
                        }


                    } catch (IOException e) {
                        logger.severe("Exception on trying to request: " + e.getMessage());
                    }

                    // TODO


                }


//                this.violationList.add(new Violation(this, locMapper.getLOCOfPath(path.getKey()),
//                        "Provide the 401 " + "response in the " + "definition of the path in the operation (here: "
//                        + operation.getKey() + ")", path.getKey(), ErrorMessage.UNAUTHORIZED));
            }
        }

    }

    private void staticAnalysis() {
        List<SecurityRequirement> security = this.openAPI.getSecurity();
        boolean globalSec = security != null && !security.isEmpty();

        for (Map.Entry<String, PathItem> path : this.openAPI.getPaths().entrySet()) {
            System.out.println(path.getKey());
            Map<String, Operation> operations = getPathOperations(path.getValue(), globalSec, true);

            for (Map.Entry<String, Operation> operation : operations.entrySet()) {
                if (operation.getValue().getResponses().containsKey("401")) continue;

                this.violationList.add(new Violation(this, locMapper.getLOCOfPath(path.getKey()),
                        "Provide the 401 " + "response in the " + "definition of the path in the operation (here: " + operation.getKey() + ")", path.getKey(), ErrorMessage.UNAUTHORIZED));
            }
        }
    }

    /**
     * Gives for the path all the operations defined.
     *
     * @param pathItem    the path item (swagger) with operations and their security, etc.
     * @param globalSec   if the security is globally defined.
     * @param onlyWithSec true when operations do have security defined --> false every operation needed
     * @return the list of operations for the specified path
     */
    private Map<String, Operation> getPathOperations(PathItem pathItem, boolean globalSec, boolean onlyWithSec) {
        Map<String, Operation> operations = new HashMap<>();

        for (String method : OPERATION_METHOD_NAMES) {
            try {
                Method operationMethod = PathItem.class.getMethod(method);
                Method securityMethod = Operation.class.getMethod(OPERATION_METHOD_SECURITY);

                Operation curOperation = (Operation) operationMethod.invoke(pathItem);

                if (curOperation == null) continue;

                Object secOb = securityMethod.invoke(curOperation);

                if (!onlyWithSec || (globalSec && secOb == null) || (secOb != null && !secOb.toString().equals("[]"))) {
                    operations.put(method.replace("get", "").toUpperCase(), curOperation);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                logger.severe("Exception when trying to get method to get the operations from a path: " + e.getMessage());
            }
        }

        return operations;
    }
}
