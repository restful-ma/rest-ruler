package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Request;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.RuleCategory;
import rest.studentproject.rule.constants.RuleSeverity;
import rest.studentproject.rule.constants.RuleSoftwareQualityAttribute;
import rest.studentproject.rule.constants.RuleType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;

public class TunnelingDynamicRule implements IRestRule {
    private static final String TITLE = "GET and POST must not be used to tunnel other request methods";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.HTTP;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.CRITICAL;
    private static final List<RuleType> RULE_TYPE = List.of(RuleType.DYNAMIC);
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List.of(RuleSoftwareQualityAttribute.MAINTAINABILITY, RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.FUNCTIONAL_SUITABILITY, RuleSoftwareQualityAttribute.USABILITY);
    private boolean isActive;

    private HttpClient client;

    public TunnelingDynamicRule(boolean isActive) {

        this.isActive = isActive;
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public String getTitle() {
        return TITLE;
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
    public List<RuleType> getRuleType() {
        return RULE_TYPE;
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
        List<Violation> violations = new ArrayList<>();


        //TODO: read config file with inputs -> generate requests

        //TODO: dynamic rule execution
        //violations.addAll(checkGetRequests());
        //violations.addAll(checkPostRequests());


        return violations;
    }

    private List<Violation> checkGetRequests(List<Request> requests) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest httpRequest;
        HttpResponse response;
        HttpResponse response2;

        List<Violation> violations = new ArrayList<>();

        for (Request request : requests) {

            httpRequest = HttpRequest.newBuilder().GET().headers(request.getHeaders()).uri(new URI(request.getUrl())).build();

            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // response codes for "created" and "no body"
            if (response.statusCode() == 201 || response.statusCode() == 204) {
                violations.add(new Violation(this, locMapper.getLOCOfPath(request.getPath()), "Tunneling Suggestion: Use GET Requests only to retrieve a representation of a resource", request.getPath(), "Tunneling Errormessage: GET Request may not be used to tunnel other request types"));
            }


            //duplicate request to make sure data is not deleted -> deletion tunneling
            response2 = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response2.statusCode() == 404 || !response2.body().equals(response.body())) {
                violations.add(new Violation(this, locMapper.getLOCOfPath(request.getPath()), "Tunneling Suggestion: Use DELETE Requests for deletion operations", request.getPath(), "Tunneling Errormessage: GET Request used for deletion operation"));
            }

        }

        return violations;
    }

    private List<Violation> checkPostRequests(List<Request> requests) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest httpRequest;
        HttpResponse response;
        HttpResponse response2;
        List<Violation> violations = new ArrayList<>();

        for (Request request: requests) {
            httpRequest = HttpRequest.newBuilder().POST(request.getBody()).headers(request.getHeaders()).uri(new URI(request.getUrl())).build();
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (!(response.statusCode() == 201 || response.statusCode() == 404)){
                violations.add(new Violation(this, locMapper.getLOCOfPath(request.getPath()), "Tunneling Suggestion: Use POST Requests to create a resource", request.getPath(), "Tunneling Errormessage: POST Request may not be used to tunnel other request types"));
            }

            //duplicate request to make sure data is not deleted -> deletion tunneling
            response2 = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response2.statusCode() == 404 || !response2.body().equals(response.body())) {
                violations.add(new Violation(this, locMapper.getLOCOfPath(request.getPath()), "Tunneling Suggestion: Use DELETE Requests for deletion operations", request.getPath(), "Tunneling Errormessage: GET Request used for deletion operation"));
            }
        }
        //tunnel GET request --> request body and response body differ

        return violations;
    }

}

