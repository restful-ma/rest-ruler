package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;

import java.util.*;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;

/**
 * Implementation of the rule: Content-Type must be used.
 */
public class ContentTypeRule implements IRestRule {
    private static final String TITLE = "Content-Type must be used";
    private static final RuleCategory CATEGORY = RuleCategory.META;
    private static final RuleType TYPE = RuleType.STATIC;
    private static final RuleSeverity SEVERITY = RuleSeverity.CRITICAL;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE =
            Arrays.asList(RuleSoftwareQualityAttribute.USABILITY, RuleSoftwareQualityAttribute.COMPATIBILITY);
    private static final String GET_OPERATION = "GET";
    private static final String POST_OPERATION = "POST";
    private static final String PUT_OPERATION = "PUT";
    private static final String PATCH_OPERATION = "PATCH";
    private static final String DELETE_OPERATION = "DELETE";

    private final List<Violation> violationList = new ArrayList<>();
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
     * Method used to check for any violations of the implemented rule
     *
     * @param openAPI structured Object containing a representation of a OpenAPI specification
     * @return List of Violations of the executing Rule
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        this.openAPI = openAPI;
        Paths paths = openAPI.getPaths();

        for (Map.Entry<String, PathItem> path : paths.entrySet()) {
            this.pathName = path.getKey();
            checkContentType(path.getValue());
        }


        return this.violationList;
    }

    /**
     * Checks for operations defined for the path. GET and DELETE should not have request bodies --> only responses
     * need to be checked if content type is defined. POST, PUT and PATCH should have request bodies --> request body
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

        if (getOp != null) {
            responses = getOp.getResponses();
            examineResponses(responses, GET_OPERATION);
        }
        if (deleteOp != null) {
            responses = deleteOp.getResponses();
            examineResponses(responses, DELETE_OPERATION);
        }

        if (postOp != null) {
            responses = postOp.getResponses();
            examineResponses(responses, POST_OPERATION);
            requestBody = postOp.getRequestBody();
            examineRequestBody(requestBody, POST_OPERATION);
        }

        if (putOp != null) {
            responses = putOp.getResponses();
            examineResponses(responses, PUT_OPERATION);
            requestBody = putOp.getRequestBody();
            examineRequestBody(requestBody, PUT_OPERATION);
        }

        if (patchOp != null) {
            responses = patchOp.getResponses();
            examineResponses(responses, PATCH_OPERATION);
            requestBody = patchOp.getRequestBody();
            examineRequestBody(requestBody, PATCH_OPERATION);
        }
    }

    /**
     * Checks for an operation if each response has the content type defined.
     *
     * @param responses the responses from the operation
     * @param operation the operation that is checked
     */
    private void examineResponses(ApiResponses responses, String operation) {
        String improvementSuggestion =
                "Specify content type in response (here: " + operation + "), because clients " + "and servers rely " + "on" + " the value of this header to know how to process the sequence of bytes in the " + "message " + "body.";
        Violation violation = new Violation(this, locMapper.getLOCOfPath(this.pathName), improvementSuggestion,
                this.pathName, ErrorMessage.CONTENT_TYPE);

        if (responses == null) {
            this.violationList.add(violation);
            return;
        }

        for (ApiResponse response : responses.values()) {

            // No content and no reference to components defined
            if (response.getContent() == null && response.get$ref() == null) this.violationList.add(violation);
                // No content but ref to components
            else if (response.getContent() == null && response.get$ref() != null) {
                // Ref to content type
                String ref = response.get$ref();
                String refLastIndex = ref.substring(ref.lastIndexOf("/") + 1);

                improvementSuggestion = "Define content in refs in /components/responses/" + refLastIndex + " or " +
                        "directly in the response (here: " + operation + ").";
                violation = new Violation(this, locMapper.getLOCOfPath(this.pathName), improvementSuggestion,
                        this.pathName, ErrorMessage.CONTENT_TYPE);

                // Check if in responses defined (needs this structure)
                if (!ref.endsWith("/components/responses/" + refLastIndex)) {
                    this.violationList.add(violation);
                    continue;
                }

                // Checks if ref has content type defined. If again ref to another component --> violation
                Map<String, ApiResponse> compResponses = this.openAPI.getComponents().getResponses();
                if (compResponses != null) {
                    for (Map.Entry<String, ApiResponse> compResponse : compResponses.entrySet()) {
                        if (compResponse.getKey().equals(refLastIndex) && (compResponse.getValue() == null || compResponse.getValue().getContent() == null)) {
                            this.violationList.add(violation);
                        }
                    }
                } else this.violationList.add(violation);
            }
        }
    }

    /**
     * Checks for an operation (only PATCH, POST, PUT) if there is a response body with a content type defined
     *
     * @param requestBody request body of operation
     * @param operation   the operation that is checked
     */
    private void examineRequestBody(RequestBody requestBody, String operation) {
        String improvementSuggestion = "Specify content type in request body (here: " + operation + "), because " +
                "clients and servers rely on the value of this header to know how to process the sequence of bytes " + "in" + " the message body.";
        Violation violation = new Violation(this, locMapper.getLOCOfPath(this.pathName), improvementSuggestion,
                this.pathName, ErrorMessage.CONTENT_TYPE);

        if (requestBody == null) {
            this.violationList.add(violation);
            return;
        }

        // No content type defined in response body and no ref to components
        if (requestBody.getContent() == null && requestBody.get$ref() == null)
            this.violationList.add(violation);
        // No content but ref to components
        else if (requestBody.getContent() == null && requestBody.get$ref() != null) {
            String ref = requestBody.get$ref();
            String refLastIndex = ref.substring(ref.lastIndexOf("/") + 1);

            improvementSuggestion = "Define content in refs in /components/requestBodies/" + refLastIndex + " or " +
                    "directly in the request body (here: " + operation + ").";
            violation = new Violation(this, locMapper.getLOCOfPath(this.pathName), improvementSuggestion,
                    this.pathName, ErrorMessage.CONTENT_TYPE);

            // Check if in request bodies defined (needs this structure)
            if (!ref.endsWith("/components/requestBodies/" + refLastIndex)) {
                this.violationList.add(violation);
                return;
            }

            // Check if content type defined in components
            Map<String, RequestBody> compRequestBodies = this.openAPI.getComponents().getRequestBodies();
            if (compRequestBodies != null) {
                for (Map.Entry<String, RequestBody> compRequestBody : compRequestBodies.entrySet()) {
                    if (compRequestBody.getKey().equals(refLastIndex) && (compRequestBody.getValue() == null || compRequestBody.getValue().getContent() == null)) {
                        this.violationList.add(violation);
                    }
                }
            }
        }
    }
}
