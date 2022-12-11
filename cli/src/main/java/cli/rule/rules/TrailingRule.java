package cli.rule.rules;

import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.*;
import cli.utility.Output;
import io.swagger.v3.oas.models.OpenAPI;
import cli.rule.constants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TrailingRule implements IRestRule {

    private static final String TITLE = "A trailing forward slash (/) should not be included in URIs";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleType> RULE_TYPE = List.of(RuleType.STATIC);
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List
            .of(RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;

    public TrailingRule(boolean isActive) {
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
     * 
     * @param paths set of path strings
     * @return list of Violations for this rule
     */
    private List<Violation> checkForTrailingSlashes(Set<String> paths) {
        List<Violation> violations = new ArrayList<>();
        int curPath = 1;
        int totalPaths = paths.size();
        for (String path : paths) {
            Output.progressPercentage(curPath, totalPaths);
            curPath++;
            if (path.endsWith("/")) {
                violations.add(new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path), ImprovementSuggestion.TRAILING_SLASH,
                        path, ErrorMessage.TRAILINGSLASH));
            }
        }
        return violations;
    }
}
