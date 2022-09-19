package rest.studentproject.rule.rules;


import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.lang3.tuple.ImmutablePair;

import org.atteo.evo.inflector.English;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;

import java.util.*;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;
import static rest.studentproject.rule.Utility.getControlPathSegmentForRule;
import static rest.studentproject.rule.Utility.getSwitchPathSegment;

public class SingularDocumentNameRule implements IRestRule {

    private static final String TITLE = "A singular noun should be used for document names";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.ERROR;
    private static final RuleType RULE_TYPE = RuleType.STATIC;
    private static final List<RuleSoftwareQualityAttribute> RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST = List.of(RuleSoftwareQualityAttribute.USABILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private boolean isActive;
    public static final String WITH_PATH_SEGMENT = " With pathSegment: ";
    public static final String PLURAL = "plural";
    public static final String SINGULAR = "singular";

    public SingularDocumentNameRule(boolean isActive) {
        this.isActive = isActive;
    }


    /**
     *
     */
    @Override
    public String getTitle() {
        return TITLE;
    }

    /**
     *
     */
    @Override
    public RuleCategory getCategory() { return RULE_CATEGORY; }

    /**
     *
     */
    @Override
    public RuleSeverity getSeverityType() {
        return RULE_SEVERITY;
    }

    /**
     *
     */
    @Override
    public RuleType getRuleType() {
        return RULE_TYPE;
    }

    /**
     *
     */
    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return RULE_SOFTWARE_QUALITY_ATTRIBUTE_LIST;
    }

    /**
     *
     */
    @Override
    public boolean getIsActive() {
        return this.isActive;
    }

    /**
     *
     */
    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Rule to check if the path segments could contain more than one word, if so there is a violation.
     *
     * @param openAPI
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI)  {
        List<Violation> violations = new ArrayList<>();

        // Get the paths from the OpenAPI object
        Set<String> paths = openAPI.getPaths().keySet();

        if (paths.isEmpty()) return violations;
        // Loop through the paths
        return getLstViolations(violations, paths);
    }

    private List<Violation> getLstViolations(List<Violation> violations, Set<String> paths) {
        for (String path : paths) {
            if (path.trim().equals("")) continue;
            // Get the path without the curly braces
            String[] pathSegments = path.split("/");
            // Extract path segments based on / char and check if there are violations
            Violation violation = getLstViolationsFromPathSegments(path, pathSegments);
            if (violation != null) violations.add(violation);


        }
        return violations;
    }

    private Violation getLstViolationsFromPathSegments(String path, String[] pathSegments){
        String switchPathSegment = "";
        String firstPathSegment = "";
        boolean skipFirstSwitchPathSegmentAssign = true;
        List<String> listPathSegments = new ArrayList<>(List.of(pathSegments));
        listPathSegments.removeAll(Arrays.asList("", null));
        // Check if a path is starting with a plural or singular word.
        if(!listPathSegments.isEmpty()) firstPathSegment = listPathSegments.get(0).trim().toLowerCase();
        // Set the switch based on the firstPathSegment. We need to see if a path has the form singular/plural/singular.. or plural/singular/plural.. based on the firstPathSegment
        switchPathSegment = getSwitchPathSegment(pathSegments, switchPathSegment, firstPathSegment);

        for (String pathSegment : listPathSegments) {
            // If a pathSegment contains a curly brace, it is a parameter, and we don't need to check it. But we know that such a pathSegment is automatically singular.
            if (pathSegment.contains("{")) {
                // Switch to plural because the curly brace pathSegment is singular
                switchPathSegment = SINGULAR;
                continue;
            }


            // If the word is not contained in the jsonDictionary, we need to check if the word is present in the OxfordDictionaryAPI
            // We get a true if the word is singular otherwise a false if it is plural
            boolean isSingular = !pathSegment.equals(English.plural(pathSegment.trim().toLowerCase(), 1));
            // If the word is plural but the current switchPathSegment is singular, then we have a violation.
            if(!isSingular && switchPathSegment.equals(PLURAL) && !listPathSegments.get(0).equals(pathSegment)) {
                return new Violation(this, locMapper.getLOCOfPath(path), ImprovementSuggestion.SINGULARDOCUMENTNAME, path, ErrorMessage.SINGULARDOCUMENTNAME + WITH_PATH_SEGMENT + pathSegment);
            }
            // Change the switchPathSegment based on the current form.
            switchPathSegment = skipFirstSwitchPathSegmentAssign ? switchPathSegment : getControlPathSegmentForRule(switchPathSegment.equals(PLURAL));
            skipFirstSwitchPathSegmentAssign = false;

        }
        return null;
    }



}
