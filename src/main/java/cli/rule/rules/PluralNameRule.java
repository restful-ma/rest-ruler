package cli.rule.rules;

import cli.rule.constants.*;
import io.swagger.v3.oas.models.OpenAPI;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.utility.Output;

import java.util.*;

import static cli.analyzer.RestAnalyzer.locMapper;
import static cli.rule.Utility.*;

public class PluralNameRule implements IRestRule {

    public static final String WITH_PATH_SEGMENT = " With pathSegment: ";
    private static final String TITLE = "A plural noun should be used for collection or store names";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST = List
            .of(RuleSoftwareQualityAttribute.USABILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;

    public PluralNameRule(boolean isActive) {
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

    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violations = new ArrayList<>();

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();

        if (paths.isEmpty())
            return violations;
        // Loop through the paths
        return getLstViolations(violations, paths);
    }

    private List<Violation> getLstViolations(List<Violation> violations, Set<String> paths) {
        int curPath = 1;
        int totalPaths = paths.size();
        for (String path : paths) {
            Output.progressPercentage(curPath, totalPaths);
            if (path.trim().equals(""))
                continue;
            // Get the path without the curly braces
            String[] pathSegments = path.split("/");
            // Extract path segments based on / char and check if there are violations
            Violation violation = getLstViolationsFromPathSegments(path, pathSegments);
            if (violation != null)
                violations.add(violation);

            curPath++;

        }
        return violations;
    }

    private Violation getLstViolationsFromPathSegments(String path, String[] pathSegments) {
        String firstPathSegment = "";
        List<String> listPathSegments = new ArrayList<>(List.of(pathSegments));
        listPathSegments.removeAll(Arrays.asList("", null));
        listPathSegments.removeAll(Arrays.asList(" ", null));
        // Check if a path is starting with a plural or singular word.
        if (!listPathSegments.isEmpty())
            firstPathSegment = listPathSegments.get(0).trim().toLowerCase();
        // Set the switch based on the firstPathSegment. We need to see if a path has
        // the form singular/plural/singular.. or plural/singular/plural.. based on the
        // firstPathSegment
        String initialToken = getTokenNLP(firstPathSegment);
        if (initialToken == null)
            return null;
        String switchPathSegment = getTokenFromWord(initialToken);

        for (String pathSegment : listPathSegments) {
            if (listPathSegments.get(0).equals(pathSegment))
                continue;
            if (pathSegment.isEmpty())
                continue;
            // If a pathSegment contains a curly brace, it is a parameter, and we don't need
            // to check it. But we know that such a pathSegment is automatically singular.
            // In this case is important to see if the previous switchPathSegment is plural
            // or singular. If was singular,and we have a pathSegment with curly braces,
            // then we have a violation because singular/singular path.
            if (pathSegment.contains("{") && switchPathSegment.equals(SingularDocumentNameRule.SINGULAR)) {
                return new Violation(this, locMapper.getLOCOfPath(path), ImprovementSuggestion.SINGULAR_DOCUMENT_NAME,
                        path, ErrorMessage.PLURAL_NAME + WITH_PATH_SEGMENT + pathSegment);
            } else if (pathSegment.contains("{") && switchPathSegment.equals(SingularDocumentNameRule.PLURAL)) {
                // Switch to plural because the curly brace pathSegment is singular and the
                // previous pathSegment was
                // plural.
                switchPathSegment = SingularDocumentNameRule.SINGULAR;
                continue;
            }
            // Get singular or plural based on the token
            String token = getTokenNLP(pathSegment);
            String currentSwitchPathSegment = getTokenFromWord(token);
            // If the word is singular but the current switchPathSegment is plural, then we
            // have a violation.
            if (switchPathSegment.equals(SingularDocumentNameRule.SINGULAR) && currentSwitchPathSegment.equals(SingularDocumentNameRule.SINGULAR)) {
                return new Violation(this, locMapper.getLOCOfPath(path), ImprovementSuggestion.PLURAL_NAME, path,
                        ErrorMessage.PLURAL_NAME + WITH_PATH_SEGMENT + pathSegment);
            }

            switchPathSegment = getControlPathSegmentForRule(switchPathSegment.equals(SingularDocumentNameRule.PLURAL));
        }
        return null;
    }

}
