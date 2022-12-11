package cli.rule.rules;

import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.*;
import cli.utility.Output;
import cli.rule.constants.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the rule: Content-Type must be used.
 */
public class ContentTypeRule implements IRestRule {

    private static final String TITLE = "Content-Type must be used";
    private static final RuleCategory CATEGORY = RuleCategory.META;
    private static final List<RuleType> TYPE = Arrays.asList(RuleType.STATIC);
    private static final RuleSeverity SEVERITY = RuleSeverity.CRITICAL;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE = Arrays
            .asList(RuleSoftwareQualityAttribute.USABILITY, RuleSoftwareQualityAttribute.COMPATIBILITY);
    private static final String GET_OPERATION = "GET";
    private static final String POST_OPERATION = "POST";
    private static final String PUT_OPERATION = "PUT";
    private static final String PATCH_OPERATION = "PATCH";
    private static final String DELETE_OPERATION = "DELETE";

    private final List<Violation> violationList = new ArrayList<>();
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private OpenAPI openAPI;
    private String pathName;
    private boolean isActive;

    public ContentTypeRule(boolean isActive) {
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
     * Method used to check for any violations of the implemented rule
     *
     * @param openAPI structured Object containing a representation of a OpenAPI
     *                specification
     * @return List of Violations of the executing Rule
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        this.openAPI = openAPI;
        Paths paths = openAPI.getPaths();

        int curPath = 1;
        int totalPaths = paths.size();
        for (Map.Entry<String, PathItem> path : paths.entrySet()) {
            this.pathName = path.getKey();
            Output.progressPercentage(curPath, totalPaths);
            curPath++;
            List<Parameter> parameters = path.getValue().getParameters();
            checkParameter(parameters, "path");
            checkContentType(path.getValue());
        }
        return this.violationList;
    }

    /**
     * Checks for operations defined for the path. GET and DELETE should not have
     * request bodies --> only responses
     * need to be checked if content type is defined. POST, PUT and PATCH should
     * have request bodies --> request body
     * and responses need the content type defined
     *
     * @param path current path to check
     */
    private void checkContentType(PathItem path) {
        Operation getOp = path.getGet();
        Operation deleteOp = path.getDelete();
        Operation postOp = path.getPost();
        Operation putOp = path.getPut();
        Operation patchOp = path.getPatch();

        ApiResponses responses;
        RequestBody requestBody;
        List<Parameter> parameters;

        if (getOp != null) {
            responses = getOp.getResponses();
            parameters = getOp.getParameters();
            checkParameter(parameters, GET_OPERATION + "-operation");
            examineResponses(responses, GET_OPERATION);
        }

        if (deleteOp != null) {
            parameters = deleteOp.getParameters();
            checkParameter(parameters, DELETE_OPERATION + "-operation");
            responses = deleteOp.getResponses();
            examineResponses(responses, DELETE_OPERATION);
        }

        if (postOp != null) {
            parameters = postOp.getParameters();
            checkParameter(parameters, POST_OPERATION + "-operation");
            responses = postOp.getResponses();
            examineResponses(responses, POST_OPERATION);
            requestBody = postOp.getRequestBody();
            examineRequestBody(requestBody, POST_OPERATION);
        }

        if (putOp != null) {
            parameters = putOp.getParameters();
            checkParameter(parameters, PUT_OPERATION + "-operation");
            responses = putOp.getResponses();
            examineResponses(responses, PUT_OPERATION);
            requestBody = putOp.getRequestBody();
            examineRequestBody(requestBody, PUT_OPERATION);
        }

        if (patchOp != null) {
            parameters = patchOp.getParameters();
            checkParameter(parameters, PATCH_OPERATION + "-operation");
            responses = patchOp.getResponses();
            examineResponses(responses, PATCH_OPERATION);
            requestBody = patchOp.getRequestBody();
            examineRequestBody(requestBody, PATCH_OPERATION);
        }
    }

    /**
     * Checks for the operation if each response has the content type defined (refs
     * are checked too).
     *
     * @param responses the responses from the operation
     * @param operation the operation that is checked
     */
    private void examineResponses(ApiResponses responses, String operation) {
        if (responses == null)
            return;

        for (Entry<String, ApiResponse> response : responses.entrySet()) {
            if (response.getKey().equals("204")) {
                continue;
            }

            boolean emptyContent = (response.getValue().getContent() == null
                    || response.getValue().getContent().isEmpty());

            // No content and no reference to components defined
            if (emptyContent && response.getValue().get$ref() == null) {
                this.violationList.add(getResponseContentTypeViolation(response.getKey(), operation));
            }
            // No content but ref to components
            else if (emptyContent && response.getValue().get$ref() != null) {
                // Ref to content type
                String ref = response.getValue().get$ref();
                String refLastIndex = ref.substring(ref.lastIndexOf("/") + 1);

                // Check if in responses defined (needs this structure)
                if (!ref.endsWith("/components/responses/" + refLastIndex)) {
                    this.violationList
                            .add(getResponseContentTypeRefViolation(refLastIndex, response.getKey(), operation));
                    continue;
                }

                // Checks if ref has content type defined. If again ref to another component -->
                // violation
                Map<String, ApiResponse> compResponses = this.openAPI.getComponents().getResponses();

                if (compResponses != null)
                    checkContentTypeInRefs(compResponses, refLastIndex,
                            getResponseContentTypeRefViolation(refLastIndex, response.getKey(), operation));
                else
                    this.violationList
                            .add(getResponseContentTypeRefViolation(refLastIndex, response.getKey(), operation));
            }
        }
    }

    /**
     * Checks for an operation (only PATCH, POST, PUT) if there is a response body
     * with a content type defined (refs are checked too).
     *
     * @param requestBody request body of operation
     * @param operation   the operation that is checked
     */
    private void examineRequestBody(RequestBody requestBody, String operation) {

        if (requestBody == null)
            return;

        boolean emptyContent = (requestBody.getContent() == null || requestBody.getContent().isEmpty());

        // No content type defined in response body and no ref to components
        if (emptyContent && requestBody.get$ref() == null)
            this.violationList.add(getRequestBodyContentTypeViolation(operation));
        // No content but ref to components
        else if (emptyContent && requestBody.get$ref() != null) {
            // Ref to content type
            String ref = requestBody.get$ref();
            String refLastIndex = ref.substring(ref.lastIndexOf("/") + 1);

            // Check if in request bodies defined (needs this structure)
            if (!ref.endsWith("/components/requestBodies/" + refLastIndex)) {
                this.violationList.add(getRequestBodyContentTypeRefViolation(refLastIndex, operation));
                return;
            }

            // Check if content type defined in components (ref exists)
            Map<String, RequestBody> compRequestBodies = this.openAPI.getComponents().getRequestBodies();
            if (!compRequestBodies.isEmpty())
                checkContentTypeInRefs(compRequestBodies, refLastIndex,
                        getRequestBodyContentTypeRefViolation(refLastIndex, operation));
            else
                this.violationList.add(getRequestBodyContentTypeRefViolation(refLastIndex, operation));
        }
    }

    private void checkParameter(List<Parameter> paramters, String pathLevel) {
        if (paramters == null)
            return;
        for (Parameter parameter : paramters) {
            if (parameter == null)
                return;

            boolean emptyContent = (parameter.getSchema() == null);

            if (emptyContent && parameter.get$ref() == null) {
                this.violationList.add(getParameterContentTypeViolation(pathLevel));
            } else if (emptyContent && parameter.get$ref() != null) {
                // Ref to content type
                String ref = parameter.get$ref();
                String refLastIndex = ref.substring(ref.lastIndexOf("/") + 1);

                // Check if in request bodies defined (needs this structure)
                if (!ref.endsWith("/parameters/" + refLastIndex)) {
                    System.out.println("ref err: " + ref);
                    this.violationList.add(getParameterContentTypeRefViolation(refLastIndex,
                            pathLevel));
                    return;
                }

                // Check if content type defined in components (ref exists)
                Map<String, Parameter> compParameters = this.openAPI.getComponents().getParameters();
                if (!compParameters.isEmpty())
                    checkSchemaInRefs(compParameters, refLastIndex,
                            getParameterContentTypeRefViolation(refLastIndex, pathLevel));
                else {
                    System.out.println("ref err ende: " + ref);
                    this.violationList.add(getParameterContentTypeRefViolation(refLastIndex,
                            pathLevel));
                }
            }
        }
    }

    /**
     * Checks if the ref has a content type defined.
     *
     * @param component        ApiResponse or RequestBody component of openAPI
     *                         definition
     * @param refLastIndex     the last index of the ref (../xyz)
     * @param violationContent the violation that is added if no content type is
     *                         defined
     */
    private void checkContentTypeInRefs(Map<String, ?> component, String refLastIndex, Violation violation) {
        boolean refFound = false;
        for (Entry<String, ?> comp : component.entrySet()) {
            // Ref found --> further checks if content type defined
            if (comp.getKey().equals(refLastIndex))
                refFound = true;
            else
                continue;

            Object content;
            Object value = comp.getValue();
            try {
                // Call comp.getValue().getContent()
                content = value.getClass().getMethod("getContent").invoke(value);

                // Check comp.getValue().getContent() is null or empty (second term)
                if (content == null || (boolean) content.getClass().getMethod("isEmpty").invoke(content)) {
                    System.out.println("ref err ganz ende: " + refLastIndex);
                    System.out.println("ref value: " + comp.getValue());
                    this.violationList.add(violation);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException e) {
                logger.log(Level.SEVERE, "Exception when accessing the content in refs: {0}", e.getMessage());
            }
        }
        // Ref not found --> Invalid ref path defined in response or request body
        if (!refFound) {
            this.violationList.add(violation);
        }
    }

    /**
     * Checks if the ref has a content type defined.
     *
     * @param component        ApiResponse or RequestBody component of openAPI
     *                         definition
     * @param refLastIndex     the last index of the ref (../xyz)
     * @param violationContent the violation that is added if no content type is
     *                         defined
     */
    private void checkSchemaInRefs(Map<String, ?> component, String refLastIndex, Violation violation) {
        boolean refFound = false;
        for (Entry<String, ?> comp : component.entrySet()) {
            // Ref found --> further checks if content type defined
            if (comp.getKey().equals(refLastIndex))
                refFound = true;
            else
                continue;

            Object schema;
            Object value = comp.getValue();
            try {
                // Call comp.getValue().getContent()
                schema = value.getClass().getMethod("getSchema").invoke(value);

                // Check comp.getValue().getContent() is null or empty (second term)
                if (schema == null) {
                    System.out.println("ref err ganz ende: " + refLastIndex);
                    System.out.println("ref value: " + comp.getValue());
                    this.violationList.add(violation);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException e) {
                logger.log(Level.SEVERE, "Exception when accessing the content in refs: {0}", e.getMessage());
            }
        }
        // Ref not found --> Invalid ref path defined in response or request body
        if (!refFound) {
            this.violationList.add(violation);
        }
    }

    private Violation getParameterContentTypeViolation(String pathLevel) {
        String improvementSuggestion = String.format(
                "Specify content type of parameter in the %s, because clients and servers rely on the value of this header to know how to process the sequence of bytes in the message body.",
                pathLevel);
        return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(this.pathName), improvementSuggestion, this.pathName,
                ErrorMessage.CONTENT_TYPE);
    }

    private Violation getParameterContentTypeRefViolation(String refLastIndex, String pathLevel) {
        String improvementSuggestion = String.format(
                "Define content of path parameters in refs in /parameters/%s or directly in the path in the %s operation.",
                refLastIndex, pathLevel);
        return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(this.pathName), improvementSuggestion, this.pathName,
                ErrorMessage.CONTENT_TYPE);
    }

    private Violation getRequestBodyContentTypeViolation(String operation) {
        String improvementSuggestion = String.format(
                "Specify content type in request body in the %s operation, because clients and servers rely on the value of this header to know how to process the sequence of bytes in the message body.",
                operation);
        return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(this.pathName), improvementSuggestion,
                this.pathName, ErrorMessage.CONTENT_TYPE);
    }

    private Violation getRequestBodyContentTypeRefViolation(String refLastIndex, String operation) {
        String improvementSuggestion = String.format(
                "Define content of request bodies in refs in /requestBodies/%s or directly in the request body in the %s operation.",
                refLastIndex, operation);
        return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(this.pathName), improvementSuggestion, this.pathName,
                ErrorMessage.CONTENT_TYPE);
    }

    private Violation getResponseContentTypeViolation(String statusCode, String operation) {
        String improvementSuggestion = String.format(
                "Specify content type in %s response in %s operation, because clients and servers rely on the value of this header to know how to process the sequence of bytes in the message body.",
                statusCode, operation);
        return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(this.pathName), improvementSuggestion,
                this.pathName, ErrorMessage.CONTENT_TYPE);
    }

    private Violation getResponseContentTypeRefViolation(String refLastIndex, String statusCode, String operation) {
        String improvementSuggestion = String.format(
                "Define content of responses in refs in /responses/%s or directly in the %s response in %s operation.",
                refLastIndex, statusCode, operation);
        return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(this.pathName), improvementSuggestion,
                this.pathName, ErrorMessage.CONTENT_TYPE);
    }
}
