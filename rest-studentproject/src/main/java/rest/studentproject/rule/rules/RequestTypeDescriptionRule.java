package rest.studentproject.rule.rules;

import io.swagger.models.Path;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
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
        pathsTest.values().forEach(pathItem -> {
            String keyPath = pathsTest.keySet().stream().filter(key -> pathsTest.get(key).equals(pathItem)).findFirst().get();
            if(pathItem.getGet() != null){
                String description = pathItem.getGet().getDescription();
                String result = wt.predict(description);
                if(result.equals("invalid")){
                    violations.add(new Violation(this, locMapper.getLOCOfPath(keyPath), ImprovementSuggestion.REQUESTTYPETUNELING, keyPath, ErrorMessage.REQUESTTYPETUNNELINGGET));
                    return;
                }
                if(!result.equals("get")){
                    violations.add(new Violation(this, locMapper.getLOCOfPath(keyPath), ImprovementSuggestion.REQUESTTYPEGET, keyPath, ErrorMessage.REQUESTTYPE + " Should be of type: " + result));
                    return;
                }
            }
            if(pathItem.getPost() != null){
                String description = pathItem.getPost().getDescription();
                String result = wt.predict(description);
                if(result.equals("invalid")){
                    violations.add(new Violation(this, locMapper.getLOCOfPath(keyPath), ImprovementSuggestion.REQUESTTYPETUNELING, keyPath, ErrorMessage.REQUESTTYPETUNNELINGPOST));
                    return;
                }
                if(!result.equals("post")){
                    violations.add(new Violation(this, locMapper.getLOCOfPath(keyPath), ImprovementSuggestion.REQUESTTYPEPOST, keyPath, ErrorMessage.REQUESTTYPE + " The request should be of type: " + result));
                    return;
                }
            }
            if(pathItem.getPut() != null){
                String description = pathItem.getPut().getDescription();
                String result = wt.predict(description);
                if(!result.equals("put")){
                    violations.add(new Violation(this, locMapper.getLOCOfPath(keyPath), ImprovementSuggestion.REQUESTTYPEPUT, keyPath, ErrorMessage.REQUESTTYPE + " The request should be of type: " + result));
                    return;
                }
            }
            if(pathItem.getDelete() != null){
                String description = pathItem.getDelete().getDescription();
                String result = wt.predict(description);
                if(!result.equals("delete")){
                    violations.add(new Violation(this,locMapper.getLOCOfPath(keyPath),  ImprovementSuggestion.REQUESTTYPEDELETE, keyPath, ErrorMessage.REQUESTTYPE + " The request should be of type: " + result));
                    return;
                }
            }
        });

        return violations;
    }
}
