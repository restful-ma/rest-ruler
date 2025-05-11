package cli.rule.rules;

import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import org.apache.commons.lang3.tuple.ImmutablePair;
import cli.weka.RequestMethodsWekaClassifier;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RequestTypeDescriptionRule implements IRestRule {
    static final String TITLE = "Description of request should match with the type of the request.";
    static final RuleCategory RULE_CATEGORY = RuleCategory.META;
    static final RuleSeverity RULE_SEVERITY = RuleSeverity.WARNING;
    static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List
            .of(RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private static final String IMPROVEMNT_SUB_STRING = " The request should be of type: ";
    private boolean isActive;
    final String MODEL = "/models/request_model.dat";
    private final String systemMessage = """
            You are a REST API design validator. Your task is to determine whether HTTP method tunneling is present in a given endpoint.

            An endpoint is considered to be tunneling if it uses a certain HTTP method to perform actions that are more appropriately represented by another HTTP method.

            A brief summary (not exhaustive) of some HTTP methods for this task:
            - POST must be used to create a new resource in a collection or to execute controllers and not for other purposes
            - GET must be used to retrieve a representation of a resource and not for other purposes
            - DELETE should be used to delete a resource and not for other purposes
            - PUT must be used to both insert and update a stored resource and not for other purposes

            If the actual HTTP method does not align with the conventional RESTful method implied by the endpoint's summary or description, classify it as tunneling.

            Be strict. If a different HTTP method would be a better fit for the described operation, answer with the name of that HTTP method. If you have the slightest doubt about the current method, answer with what you think the correct method should be.

            Do not explain. Only respond with a one-word valid method name (one of POST, GET, DELETE or PUT) and nothing else. If you think there is no violation, respond only by saying "No", and nothing else.
                        """;
    private final String modelNoResponse = "no";

    public RequestTypeDescriptionRule(boolean isActive) {
        this.isActive = isActive;
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
        RequestMethodsWekaClassifier wt = new RequestMethodsWekaClassifier();
        wt.loadModel(MODEL);
        List<Violation> violations = new ArrayList<>();

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();
        Paths pathsTest = openAPI.getPaths();

        if (paths.isEmpty())
            return violations;
        pathsTest.values().forEach(pathItem -> {
            String keyPath = pathsTest.keySet().stream().filter(key -> pathsTest.get(key).equals(pathItem)).findFirst()
                    .get();
            if (pathItem.getGet() != null) {
                String description = pathItem.getGet().getDescription();
                String summary = pathItem.getGet().getSummary();
                // if (summary != null && !summary.isEmpty()) {
                // getViolationGetRequest(wt, keyPath, summary,
                // ErrorMessage.REQUESTTYPETUNNELINGGET, "get",
                // ImprovementSuggestion.REQUESTTYPEGET, true, violations);
                // } else if (description != null && !description.isEmpty()) {
                // getViolationGetRequest(wt, keyPath, description,
                // ErrorMessage.REQUESTTYPETUNNELINGGET, "get",
                // ImprovementSuggestion.REQUESTTYPEGET, true, violations);
                // }

                this.getViolationGetRequestWithGPT(keyPath, description, summary, "get",
                        ImprovementSuggestion.REQUESTTYPEGET, violations);

            }
            if (pathItem.getPost() != null) {
                String description = pathItem.getPost().getDescription();
                String summary = pathItem.getPost().getSummary();
                // if (summary != null && !summary.isEmpty()) {
                // getViolationGetRequest(wt, keyPath, summary,
                // ErrorMessage.REQUESTTYPETUNNELINGPOST, "post",
                // ImprovementSuggestion.REQUESTTYPEPOST, true, violations);
                // } else if (description != null && !description.isEmpty()) {
                // getViolationGetRequest(wt, keyPath, description,
                // ErrorMessage.REQUESTTYPETUNNELINGPOST, "post",
                // ImprovementSuggestion.REQUESTTYPEPOST, true, violations);
                // }

                this.getViolationGetRequestWithGPT(keyPath, description, summary, "post",
                        ImprovementSuggestion.REQUESTTYPEPOST, violations);
            }
            if (pathItem.getPut() != null) {
                String description = pathItem.getPut().getDescription();
                String summary = pathItem.getPut().getSummary();
                // if (summary != null && !summary.isEmpty()) {
                // getViolationGetRequest(wt, keyPath, summary, "", "put",
                // ImprovementSuggestion.REQUESTTYPEPUT, false,
                // violations);
                // } else if (description != null && !description.isEmpty()) {
                // getViolationGetRequest(wt, keyPath, description, "", "put",
                // ImprovementSuggestion.REQUESTTYPEPUT,
                // false, violations);
                // }

                this.getViolationGetRequestWithGPT(keyPath, description, summary, "put",
                        ImprovementSuggestion.REQUESTTYPEPUT, violations);
            }
            if (pathItem.getDelete() != null) {
                String description = pathItem.getDelete().getDescription();
                String summary = pathItem.getDelete().getSummary();
                // if (summary != null && !summary.isEmpty()) {
                // getViolationGetRequest(wt, keyPath, summary, "", "delete",
                // ImprovementSuggestion.REQUESTTYPEDELETE,
                // false, violations);
                // } else if (description != null && !description.isEmpty()) {
                // getViolationGetRequest(wt, keyPath, description, "", "delete",
                // ImprovementSuggestion.REQUESTTYPEDELETE, false, violations);
                // }

                this.getViolationGetRequestWithGPT(keyPath, description, summary, "delete",
                        ImprovementSuggestion.REQUESTTYPEDELETE, violations);
            }
        });

        return violations;
    }

    private void getViolationGetRequest(RequestMethodsWekaClassifier wt, String keyPath, String description,
            String requestTypeTunnelingType, String requestType, String requestTypeMessage, boolean switchRequestType,
            List<Violation> violations) {
        ImmutablePair<String, Double> predictionValues = wt.predict(description);
        if ((predictionValues != null && predictionValues.right != null && predictionValues.left != null)
                && predictionValues.left.equals("invalid") && (predictionValues.right >= 0.75) && switchRequestType) {
            violations.add(new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(keyPath),
                    ImprovementSuggestion.REQUESTTYPETUNELING, keyPath, requestTypeTunnelingType));
        } else if ((predictionValues != null && predictionValues.right != null && predictionValues.left != null)
                && !predictionValues.left.equals(requestType) && (predictionValues.right >= 0.75)) {
            violations.add(new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(keyPath),
                    requestTypeMessage + IMPROVEMNT_SUB_STRING + predictionValues.left.toUpperCase(), keyPath,
                    ErrorMessage.REQUESTTYPE));
        }
    }

    private void getViolationGetRequestWithGPT(String keyPath, String description, String summary,
            String requestType, String requestTypeMessage, List<Violation> violations) {
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();
        String input = this.constructInput(description, summary, requestType);

        ResponseCreateParams params = ResponseCreateParams.builder()
                .temperature(0)
                .instructions(systemMessage)
                .input(input)
                .model(ChatModel.GPT_4_1_MINI)
                .build();
        Response response = client.responses().create(params);

        System.out.println("REQUEST: " + requestType + " DESCRIPTION: " + description + " SUMMARY: " + summary);
        response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .forEach(outputText -> {
                    System.out.println("GPT OUTPUT: " + outputText.text());
                    String output = outputText.text();

                    if (!output.toLowerCase().equals(this.modelNoResponse)) {
                        violations.add(new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(keyPath),
                                requestTypeMessage + IMPROVEMNT_SUB_STRING + output,
                                keyPath,
                                ErrorMessage.REQUESTTYPE));
                    }
                });
    }

    private String constructInput(String description, String summary, String requestType) {
        String res = String.format("Method: %s", requestType);

        if (description != null && !description.isEmpty()) {
            res += String.format("\nDescription: %s", description);
        }

        if (summary != null && !summary.isEmpty()) {
            res += String.format("\nSummary: %s", summary);
        }

        return res;
    }

}
