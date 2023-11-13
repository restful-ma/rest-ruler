package cli.rule.rules;

import cli.rule.constants.*;
import io.swagger.v3.oas.models.OpenAPI;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.utility.Output;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cli.analyzer.RestAnalyzer.locMapper;

public class LowercaseRule implements IRestRule {

    private static final String TITLE = "Lowercase letters should be preferred in URI paths";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST = List
            .of(RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;

    public LowercaseRule(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public RuleCategory getCategory() {
        return LowercaseRule.RULE_CATEGORY;

    }

    @Override
    public RuleSeverity getSeverityType() {
        return RULE_SEVERITY;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST;
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
     * Rule to check if the URI path contains only lowercase letters. If not, the
     * rule is violated.
     *
     * @param openAPI
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violations = new ArrayList<>();

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();

        if (paths.isEmpty())
            return violations;

        int curPath = 1;
        int totalPaths = paths.size();
        // Loop through the paths
        for (String path : paths) {
            Output.progressPercentage(curPath, totalPaths);
            curPath++;
            if (path.trim().equals(""))
                continue;
            // Get the path without the curly braces
            String pathWithoutParameters = path.replaceAll("\\{" + ".*" + "\\}", "");
            // Get the path in lowercase
            String pathWithoutParametersLowerCase = pathWithoutParameters.toLowerCase();
            // Check if the path contains only lowercase letters
            if (!pathWithoutParametersLowerCase.equals(pathWithoutParameters)) {
                violations.add(new Violation(this, locMapper.getLOCOfPath(path), ImprovementSuggestion.LOWERCASE, path,
                        ErrorMessage.LOWERCASE));
            }

        }

        return violations;
    }
}
