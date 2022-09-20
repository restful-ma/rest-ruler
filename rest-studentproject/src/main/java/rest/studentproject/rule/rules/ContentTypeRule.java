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

    private void examineResponses(ApiResponses responses, String operation) {
        String improvementSuggestion = "Specify content type in response (here: " + operation + "), because clients and servers rely on the value of this header to know how to process the sequence of bytes in the message body.";
        Violation violation = new Violation(this, locMapper.getLOCOfPath(this.pathName), improvementSuggestion, this.pathName, ErrorMessage.CONTENT_TYPE);
        if (responses == null) {
            this.violationList.add(violation);
            return;
        }

        for (ApiResponse response : responses.values()) {
            if (response.getContent() == null && response.get$ref() == null)
                this.violationList.add(violation);
            else if (response.getContent() == null && response.get$ref() != null) {
                String ref = response.get$ref();
                String refLastIndex = ref.substring(ref.lastIndexOf("/") + 1);
                // Check if in responses defined
                improvementSuggestion = "Define content in refs in /components/responses/" + refLastIndex + " or " +
                        "directly in the response (here: " + operation + ").";
                violation = new Violation(this, locMapper.getLOCOfPath(this.pathName), improvementSuggestion,
                        this.pathName, ErrorMessage.CONTENT_TYPE);
                if (!ref.endsWith("/components/responses/" + refLastIndex)) {
                    this.violationList.add(violation);
                    continue;
                }
                Map<String, ApiResponse> compResponses = this.openAPI.getComponents().getResponses();
                if (compResponses != null) {
                    for (Map.Entry<String, ApiResponse> compResponse : compResponses.entrySet()) {
                        if (compResponse.getKey().equals(refLastIndex) && (compResponse.getValue() == null || compResponse.getValue().getContent() == null)) {
                            this.violationList.add(violation);
                        }
                    }
                } else
                    this.violationList.add(violation);
            }
        }
    }

    private void examineRequestBody(RequestBody requestBodys, String operation) {
        String improvementSuggestion = "Specify content type in response (here: " + operation + "), because clients and servers rely on the value of this header to know how to process the sequence of bytes in the message body.";
        Violation violation = new Violation(this, locMapper.getLOCOfPath(this.pathName), improvementSuggestion, this.pathName, ErrorMessage.CONTENT_TYPE);
        if (requestBodys == null) {
            this.violationList.add(violation);
            return;
        }

        if (requestBodys.getContent() == null && requestBodys.get$ref() == null) {
            this.violationList.add(violation);
        } else if (requestBodys.getContent() == null && requestBodys.get$ref() != null) {
            // check where defined
        }
    }
}
