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
            examineResponses(responses);
        }
        if (deleteOp != null) {
            responses = deleteOp.getResponses();
            examineResponses(responses);
        }

        if (postOp != null) {
            responses = postOp.getResponses();
            examineResponses(responses);
            requestBody = postOp.getRequestBody();
            examineRequestBody(requestBody);
        }

        if (putOp != null) {
            responses = putOp.getResponses();
            examineResponses(responses);
            requestBody = putOp.getRequestBody();
            examineRequestBody(requestBody);
        }

        if (patchOp != null) {
            responses = patchOp.getResponses();
            examineResponses(responses);
            requestBody = patchOp.getRequestBody();
            examineRequestBody(requestBody);
        }
    }

    private void examineResponses(ApiResponses responses) {
        if (responses == null) {
            this.violationList.add(new Violation(this, locMapper.getLOCOfPath(this.pathName),
                    ImprovementSuggestion.CONTENT_TYPE, this.pathName, ErrorMessage.CONTENT_TYPE));
            return;
        }

        for (ApiResponse response : responses.values()) {
            if (response.getContent() == null && response.get$ref() == null)
                this.violationList.add(new Violation(this, locMapper.getLOCOfPath(this.pathName),
                        ImprovementSuggestion.CONTENT_TYPE, this.pathName, ErrorMessage.CONTENT_TYPE));
            else if (response.getContent() == null && response.get$ref() != null) {
                String ref = response.get$ref();
                // Check if in responses defined
//                if (ref.startsWith(""))
                ref = ref.substring(ref.lastIndexOf("/") + 1);
                Map<String, ApiResponse> compResponses = this.openAPI.getComponents().getResponses();
                if (compResponses != null) {
                    for (Map.Entry<String, ApiResponse> compResponse : compResponses.entrySet()) {
                        if (compResponse.getKey().equals(ref) && (compResponse.getValue() == null || compResponse.getValue().getContent() == null)) {
                            this.violationList.add(new Violation(this, locMapper.getLOCOfPath(this.pathName),
                                    ImprovementSuggestion.CONTENT_TYPE, this.pathName, ErrorMessage.CONTENT_TYPE));
                        }
                    }
                } else
                    this.violationList.add(new Violation(this, locMapper.getLOCOfPath(this.pathName),
                            ImprovementSuggestion.CONTENT_TYPE, this.pathName, ErrorMessage.CONTENT_TYPE));
            }
        }
    }

    private void examineRequestBody(RequestBody requestBodys) {
        if (requestBodys == null) {
            this.violationList.add(new Violation(this, locMapper.getLOCOfPath(this.pathName),
                    ImprovementSuggestion.CONTENT_TYPE, this.pathName, ErrorMessage.CONTENT_TYPE));
            return;
        }

        if (requestBodys.getContent() == null && requestBodys.get$ref() == null){
            this.violationList.add(new Violation(this, locMapper.getLOCOfPath(this.pathName),
                    ImprovementSuggestion.CONTENT_TYPE, this.pathName, ErrorMessage.CONTENT_TYPE));
        } else if (requestBodys.getContent() == null && requestBodys.get$ref() != null) {
            // check where defined
        }
    }
}
