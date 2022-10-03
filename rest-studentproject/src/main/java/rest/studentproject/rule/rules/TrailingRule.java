package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;

public class TrailingRule implements IRestRule {


    private static final String TITLE = "GET and POST must not be used to tunnel other request methods";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.HTTP;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.CRITICAL;
    private static final RuleType RULE_TYPE = RuleType.STATIC;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List.of(RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;

    public TrailingRule (boolean isActive){
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
        return isActive;
    }

    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        Set<String> paths = openAPI.getPaths().keySet();

        return checkForTrailingSlashes(paths);
    }

    /**
     * searches a Set of path segments for trailing forward slashes '/'
     * @param paths set of path strings
     * @return list of Violations for this rule
     */
    private List<Violation> checkForTrailingSlashes (Set<String> paths){
        List<Violation> violations = new ArrayList<>();
        for (String path: paths) {
            if (path.endsWith("/")){
                violations.add(new Violation(this,locMapper.getLOCOfPath(path), ImprovementSuggestion.TRAILINGSLASH,path, ErrorMessage.TRAILINGSLASH));
            }
        }
        return violations;
    }
}
