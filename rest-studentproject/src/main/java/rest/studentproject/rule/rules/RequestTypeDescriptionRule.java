package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;
import rest.studentproject.weka.RequestMethodsWekaClassifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;

public class RequestTypeDescriptionRule implements IRestRule {
    static final String TITLE = "Description of request should match with the type of the request.";
    static final RuleCategory RULE_CATEGORY = RuleCategory.META;
    static final RuleSeverity RULE_SEVERITY = RuleSeverity.WARNING;
    static final RuleType RULE_TYPE = RuleType.STATIC;
    static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List.of(RuleSoftwareQualityAttribute.MAINTAINABILITY);
    boolean isActive;
    final String MODEL = "models/request_model.dat";

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
    public RuleType getRuleType() {
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
        RequestMethodsWekaClassifier wt = new RequestMethodsWekaClassifier();
        wt.loadModel(MODEL);
        List<Violation> violations = new ArrayList<>();

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();
        Paths pathsTest = openAPI.getPaths();

        if (paths.isEmpty()) return violations;
        // Loop through the paths
        for(int i = 0; i < pathsTest.size(); i++){
            String pathKey = pathsTest.keySet().toArray()[i].toString();
            // Need to access the description of the path
            // Give the description to the weka classifier as parameter using the wk.predict(parameter) method
            // Compare the given request type with the predicted request type
            // If there is a mismatch, create a violation
            // For example the request is of type GET but the description is "Create a new user", with the prediction from weka
            // we should get post as tag and so we have a mismatch between the request type and the description. This is a violation of
            // GET must be used to retrieve a representation of a resource, here should be use POST.
            String testToPredictUsingWeka = wt.predict("get cat food");
            //String description = pathsTest.get(pathKey).getGet().getDescription();
            boolean test = false;
        }
        violations.add(new Violation(this, 0, "TODO", "TODO", "TODO"));

        return violations;
    }
}
