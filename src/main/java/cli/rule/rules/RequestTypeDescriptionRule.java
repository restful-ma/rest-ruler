package cli.rule.rules;

import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import org.apache.commons.lang3.tuple.ImmutablePair;
import cli.weka.RequestMethodsWekaClassifier;
import cli.ai.gpt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RequestTypeDescriptionRule implements IRestRule {
    static final String TITLE = "Description of request should match with the type of the request.";
    private static final RuleIdentifier RULE_IDENTIFIER = RuleIdentifier.REQUEST_TYPE_DESCRIPTION;
    static final RuleCategory RULE_CATEGORY = RuleCategory.META;
    static final RuleSeverity RULE_SEVERITY = RuleSeverity.WARNING;
    static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List
            .of(RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private static final String IMPROVEMNT_SUB_STRING = " The request should be of type: ";
    private boolean isActive;
    final String MODEL = "/models/request_model.dat";
    private gpt gptClient;
    private boolean enableLLM = false;

    public RequestTypeDescriptionRule(boolean isActive) {
        this.isActive = isActive;
        this.gptClient = new gpt();
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
    public void setEnableLLM(boolean enableLLM) {
        this.enableLLM = enableLLM;
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

                if (this.enableLLM) {
                    this.gptClient.tunnelingViolationAI(this, keyPath, description, summary, "get", ImprovementSuggestion.REQUESTTYPEGET, IMPROVEMNT_SUB_STRING, violations);
                } else {
                    if (summary != null && !summary.isEmpty()) {
                        getViolationGetRequest(wt, keyPath, summary,
                        ErrorMessage.REQUESTTYPETUNNELINGGET, "get",
                        ImprovementSuggestion.REQUESTTYPEGET, true, violations);
                    } else if (description != null && !description.isEmpty()) {
                        getViolationGetRequest(wt, keyPath, description,
                        ErrorMessage.REQUESTTYPETUNNELINGGET, "get",
                        ImprovementSuggestion.REQUESTTYPEGET, true, violations);
                    }
                }


            }
            if (pathItem.getPost() != null) {
                String description = pathItem.getPost().getDescription();
                String summary = pathItem.getPost().getSummary();

                if (this.enableLLM) {
                    this.gptClient.tunnelingViolationAI(this, keyPath, description, summary, "post", ImprovementSuggestion.REQUESTTYPEPOST, IMPROVEMNT_SUB_STRING, violations);
                } else {
                    if (summary != null && !summary.isEmpty()) {
                        getViolationGetRequest(wt, keyPath, summary,
                        ErrorMessage.REQUESTTYPETUNNELINGPOST, "post",
                        ImprovementSuggestion.REQUESTTYPEPOST, true, violations);
                    } else if (description != null && !description.isEmpty()) {
                        getViolationGetRequest(wt, keyPath, description,
                        ErrorMessage.REQUESTTYPETUNNELINGPOST, "post",
                        ImprovementSuggestion.REQUESTTYPEPOST, true, violations);
                    }
                }
            }
            if (pathItem.getPut() != null) {
                String description = pathItem.getPut().getDescription();
                String summary = pathItem.getPut().getSummary();

                if (this.enableLLM) {
                    this.gptClient.tunnelingViolationAI(this, keyPath, description, summary, "put", ImprovementSuggestion.REQUESTTYPEPUT, IMPROVEMNT_SUB_STRING, violations);
                } else {
                    if (summary != null && !summary.isEmpty()) {
                        getViolationGetRequest(wt, keyPath, summary, "", "put",
                        ImprovementSuggestion.REQUESTTYPEPUT, false,
                        violations);
                    } else if (description != null && !description.isEmpty()) {
                        getViolationGetRequest(wt, keyPath, description, "", "put",
                        ImprovementSuggestion.REQUESTTYPEPUT,
                        false, violations);
                    }
                }
            }
            if (pathItem.getDelete() != null) {
                String description = pathItem.getDelete().getDescription();
                String summary = pathItem.getDelete().getSummary();

                if (this.enableLLM) {
                    this.gptClient.tunnelingViolationAI(this, keyPath, description, summary, "delete",
                    ImprovementSuggestion.REQUESTTYPEDELETE, IMPROVEMNT_SUB_STRING, violations);
                } else {
                    if (summary != null && !summary.isEmpty()) {
                        getViolationGetRequest(wt, keyPath, summary, "", "delete",
                        ImprovementSuggestion.REQUESTTYPEDELETE,
                        false, violations);
                    } else if (description != null && !description.isEmpty()) {
                        getViolationGetRequest(wt, keyPath, description, "", "delete",
                        ImprovementSuggestion.REQUESTTYPEDELETE, false, violations);
                    }
                }
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

}
