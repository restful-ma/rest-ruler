package cli.rule.rules;

import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.*;
import cli.utility.Output;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class implements the Rule "GET must be used to retrieve a representation of a resource" for GET Requests
 */
public class GetResourceRule implements IRestRule {

    //Rule Attribute Definitions
    private static final String TITLE = "GET must be used to retrieve a representation of a resource";
    private static final RuleIdentifier RULE_IDENTIFIER = RuleIdentifier.GET_RESOURCE;
    private static final RuleCategory RULE_CATEGORY = RuleCategory.HTTP;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.CRITICAL;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES =
            List.of(RuleSoftwareQualityAttribute.MAINTAINABILITY, RuleSoftwareQualityAttribute.COMPATIBILITY,
                    RuleSoftwareQualityAttribute.FUNCTIONAL_SUITABILITY, RuleSoftwareQualityAttribute.USABILITY);
    //Successful HTTP Response
    private static final String HTTP_OK = "200";
    private boolean isActive;

    public GetResourceRule(boolean isActive) {
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
        return RULE_CATEGORY;
    }

    @Override
    public RuleSeverity getSeverityType() {
        return RULE_SEVERITY;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return SOFTWARE_QUALITY_ATTRIBUTES;
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

        //collect necessary data
        Paths paths = openAPI.getPaths();

        int curPath = 1;
        int totalPaths = paths.keySet().size();
        for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
            Output.progressPercentage(curPath, totalPaths);
            curPath++;
            
            String path = entry.getKey();
            PathItem item = entry.getValue();

            //checks if request type is GET
            Operation getRequest = item.getGet();

            if (getRequest == null) continue;

            //check request for request body
            Violation violation = checkForRequestBody(getRequest, path);
            if (violation != null) violationList.add(violation);

            //check request for missing response representation
            violation = checkForValidResponse(getRequest, path);
            if (violation != null) violationList.add(violation);

        }

        return violationList;
    }

    /**
     * checks if a given GET requests contains a request body and return a Violation Object if it contains a request
     * body.
     *
     * @param getRequest GET Request to be analyzed
     * @param path       Path of the Request
     * @return Violation Object if a request body is found and 'null' if not
     */
    private Violation checkForRequestBody(Operation getRequest, String path) {
        if (getRequest.getRequestBody() == null) return null;
        //Get requests should not have a request body
        return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path), ImprovementSuggestion.GET_RESOURCE_REQUEST_BODY,
                path, ErrorMessage.GET_RESOURCE_REQUEST_BODY);
    }

    /**
     * checks if a GET requests contains a definition of the content of the response
     *
     * @param getRequest GET request to be analyzed
     * @param path       Path of the Request
     * @return Violation Object if valid response is missing entirely or if a valid content definition is missing
     */
    private Violation checkForValidResponse(Operation getRequest, String path) {
        ApiResponses responses = getRequest.getResponses();

        // HTTP Code 200 - 'OK'
        ApiResponse okResponse = responses.get(HTTP_OK);
        ApiResponse defaultResponse = responses.getDefault();

        //Checks if a HTTP 200 response or a default response definition exists
        if (okResponse != null) {
            if ((okResponse.getContent() == null || okResponse.getContent().isEmpty()) && okResponse.get$ref() == null ) {
                return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path), ImprovementSuggestion.GET_RESOURCE, path,
                        ErrorMessage.GET_RESOURCE);
            }
        } else if (defaultResponse != null) {
            if ((defaultResponse.getContent() == null || defaultResponse.getContent().isEmpty()) && defaultResponse.get$ref() == null) {
                return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path), ImprovementSuggestion.GET_RESOURCE, path,
                        ErrorMessage.GET_RESOURCE);
            }
        } else {
            //if there is no response for HTTP 200 or default case
            return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path),
                    ImprovementSuggestion.GET_RESOURCE_MISSING_RESPONSE, path,
                    ErrorMessage.GET_RESOURCE_MISSING_RESPONSE);
        }
        //No violation was found
        return null;
    }
}
