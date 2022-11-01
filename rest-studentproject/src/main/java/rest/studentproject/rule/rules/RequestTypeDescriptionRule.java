package rest.studentproject.rule.rules;

import io.swagger.models.Path;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.apache.commons.lang3.tuple.ImmutablePair;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;
import rest.studentproject.weka.RequestMethodsWekaClassifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;

public class RequestTypeDescriptionRule implements IRestRule {
    static final String TITLE = "Description of request should match with the type of the request.";
    static final RuleCategory RULE_CATEGORY = RuleCategory.META;
    static final RuleSeverity RULE_SEVERITY = RuleSeverity.WARNING;
    static final List<RuleType> RULE_TYPE = List.of(RuleType.STATIC);
    static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List.of(RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private static final String IMPROVEMNT_SUB_STRING = " The request should be of type: ";
    private boolean isActive;
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
        RequestMethodsWekaClassifier wt = new RequestMethodsWekaClassifier();
        wt.loadModel(MODEL);
        List<Violation> violations = new ArrayList<>();

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();
        Paths pathsTest = openAPI.getPaths();

        if (paths.isEmpty()) return violations;
        pathsTest.values().forEach(pathItem -> {
            String keyPath = pathsTest.keySet().stream().filter(key -> pathsTest.get(key).equals(pathItem)).findFirst().get();
            if(pathItem.getGet() != null){
                String description = pathItem.getGet().getDescription();
                ImmutablePair<String, Double> predictionValues = wt.predict(description);
                if((predictionValues != null && predictionValues.right != null && predictionValues.left != null) && predictionValues.left.equals("invalid") && (predictionValues.right >= 0.75)){
                    violations.add(new Violation(this, locMapper.getLOCOfPath(keyPath), ImprovementSuggestion.REQUESTTYPETUNELING, keyPath, ErrorMessage.REQUESTTYPETUNNELINGGET));
                }else if((predictionValues != null && predictionValues.right != null && predictionValues.left != null) && !predictionValues.left.equals("get") && (predictionValues.right >= 0.75)){
                    violations.add(new Violation(this, locMapper.getLOCOfPath(keyPath),  ImprovementSuggestion.REQUESTTYPEGET + IMPROVEMNT_SUB_STRING + predictionValues.left.toUpperCase(), keyPath, ErrorMessage.REQUESTTYPE));
                }
            }
            if(pathItem.getPost() != null){
                String description = pathItem.getPost().getDescription();
                ImmutablePair<String, Double> predictionValues = wt.predict(description);
                if((predictionValues != null && predictionValues.right != null && predictionValues.left != null) && predictionValues.left.equals("invalid") && (predictionValues.right >= 0.75)){
                    violations.add(new Violation(this, locMapper.getLOCOfPath(keyPath), ImprovementSuggestion.REQUESTTYPETUNELING, keyPath, ErrorMessage.REQUESTTYPETUNNELINGPOST));
                }else if((predictionValues != null && predictionValues.right != null && predictionValues.left != null) && !predictionValues.left.equals("post") && (predictionValues.right >= 0.75)){
                    violations.add(new Violation(this, locMapper.getLOCOfPath(keyPath), ImprovementSuggestion.REQUESTTYPEPOST + IMPROVEMNT_SUB_STRING + predictionValues.left.toUpperCase(), keyPath, ErrorMessage.REQUESTTYPE));
                }
            }
            if(pathItem.getPut() != null){
                String description = pathItem.getPut().getDescription();
                ImmutablePair<String, Double> predictionValues = wt.predict(description);
                if((predictionValues != null && predictionValues.right != null && predictionValues.left != null) && !predictionValues.left.equals("put") && (predictionValues.right >= 0.75)){
                    violations.add(new Violation(this, locMapper.getLOCOfPath(keyPath), ImprovementSuggestion.REQUESTTYPEPUT+ IMPROVEMNT_SUB_STRING + predictionValues.left.toUpperCase(), keyPath, ErrorMessage.REQUESTTYPE ));
                }
            }
            if(pathItem.getDelete() != null){
                String description = pathItem.getDelete().getDescription();
                ImmutablePair<String, Double> predictionValues = wt.predict(description);
                if((predictionValues != null && predictionValues.right != null && predictionValues.left != null) && !predictionValues.left.equals("delete") && (predictionValues.right >= 0.75)){
                    violations.add(new Violation(this,locMapper.getLOCOfPath(keyPath),  ImprovementSuggestion.REQUESTTYPEDELETE + IMPROVEMNT_SUB_STRING + predictionValues.left.toUpperCase(), keyPath, ErrorMessage.REQUESTTYPE));
                }
            }
        });

        return violations;
    }
}
