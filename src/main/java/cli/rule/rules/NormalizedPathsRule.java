package cli.rule.rules;

import cli.rule.constants.*;
import io.swagger.v3.oas.models.OpenAPI;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.utility.Output;
import cli.rule.constants.ErrorMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cli.analyzer.RestAnalyzer.locMapper;

/**
 * RULE: Must use normalized paths without empty path segments
 */
public class NormalizedPathsRule implements IRestRule {
    private static final String TITLE = "Must use normalized paths without empty path segments";
    private static final RuleIdentifier RULE_IDENTIFIER = RuleIdentifier.NORMALIZED_PATHS;
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List
            .of(RuleSoftwareQualityAttribute.MAINTAINABILITY, RuleSoftwareQualityAttribute.COMPATIBILITY);
    private boolean isActive;

    public NormalizedPathsRule(boolean isActive) {
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
        List<Violation> violations = new ArrayList<>();
        Set<String> paths = openAPI.getPaths().keySet();

        if (paths.isEmpty()) {
            return violations;
        }

        int curPath = 1;
        int totalPaths = paths.size();

        for (String path : paths) {
            Output.progressPercentage(curPath, totalPaths);
            curPath++;

            // check for empty path segments (//)
            if (path.contains("//")) {
                violations.add(new Violation(this, locMapper.getLOCOfPath(path),
                        "Path contains empty segments. Normalize the path by removing empty segments.",
                        path, ErrorMessage.EMPTYPATHSEGMENT));
            }
        }

        return violations;
    }
}
