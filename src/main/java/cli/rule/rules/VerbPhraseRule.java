package cli.rule.rules;

import static cli.rule.Utility.getTokenNLP;
import static cli.rule.Utility.splitContiguousWords;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.ErrorMessage;
import cli.rule.constants.ImprovementSuggestion;
import cli.rule.constants.RuleCategory;
import cli.rule.constants.RuleIdentifier;
import cli.rule.constants.RuleSeverity;
import cli.rule.constants.RuleSoftwareQualityAttribute;
import cli.utility.Output;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;

public class VerbPhraseRule implements IRestRule {

    private static final String TITLE = "A verb or verb phrase should be used for controller names";
    private static final RuleIdentifier RULE_IDENTIFIER = RuleIdentifier.VERB_PHRASE;
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST =
            List.of(RuleSoftwareQualityAttribute.USABILITY,
                    RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private int curPath = 1;

    public VerbPhraseRule(boolean isActive) {
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
        Paths openApiPaths = openAPI.getPaths();
        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();

        if (paths.isEmpty())
            return violations;
        // Loop through the paths
        return getLstViolations(violations, openApiPaths);
    }

    /**
     * Get list of violation for the rule 5 (Verb Phrase Rule)
     * 
     * @param violations
     * @param paths
     * @return
     */
    private List<Violation> getLstViolations(List<Violation> violations, Paths paths) {
        int totalPaths = paths.keySet().size();
        paths.forEach((path, pathItem) -> {
            Output.progressPercentage(this.curPath, totalPaths);
            this.curPath++;
            if (!path.trim().equals("")) {
                // Check if the path is of type get or post
                Operation getOperation = pathItem.getGet();
                Operation postOperation = pathItem.getPost();
                // Get the path without the curly braces
                String pathWithoutVariables = path.replaceAll("\\{" + ".*" + "\\}/", "");
                String[] pathSegments = pathWithoutVariables.split("/");
                // Extract path segments based on / char and check if there are violations
                Violation violation = getLstViolationsFromPathSegments(path, pathSegments,
                        getOperation, postOperation);
                if (violation != null)
                    violations.add(violation);
            }
        });
        return violations;
    }

    /**
     * Get a violation based on the last path segment of a path. If the last part segment is a verb
     * and the request is not of type GET or POST, then there is a violation.
     * 
     * @param path
     * @param pathSegments
     * @param getOperation
     * @param postOperation
     * @return
     */
    private Violation getLstViolationsFromPathSegments(String path, String[] pathSegments,
            Operation getOperation, Operation postOperation) {
        // Get the last pathSegment which we need to analyze
        if (pathSegments.length < 1)
            return null;
        String lastPathSegment = pathSegments[pathSegments.length - 1];
        try {
            // Get the words forming the pathSegment
            List<String> subStringFromPath = splitContiguousWords(lastPathSegment);
            List<String> pathWithoutParameterDictionaryMatching =
                    Arrays.asList(subStringFromPath.get(0).split(" "));
            // Check if the first word is a verb
            if (pathWithoutParameterDictionaryMatching.get(0).equals(""))
                return null;

            String token = getTokenNLP(pathWithoutParameterDictionaryMatching.get(0));
            // If the first word is a verb but the request is not of type get or post then
            // we have a violation.
            boolean isTokenVerb = token.equals("VBZ") || token.equals("VBP") || token.equals("VB");
            if (isTokenVerb && (getOperation == null && postOperation == null)) {
                return new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path),
                        ImprovementSuggestion.VERB_PHRASE, path, ErrorMessage.VERBPHRASE);
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error on checking substring against a dictionary{e}", e);
        }
        return null;
    }

}
