package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;

/**
 * Implementation of the rule: 401 ("Unauthorized") must be used when there is a problem with the client's credentials
 */
public class UnauthorizedRule implements IRestRule {

    private static final String TITLE = "401 (\"Unauthorized\") must be used when there is a problem with the " +
            "client's credentials";
    private static final RuleCategory CATEGORY = RuleCategory.HTTP;
    private static final RuleType TYPE = RuleType.STATIC;
    private static final RuleSeverity SEVERITY = RuleSeverity.CRITICAL;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE =
            Arrays.asList(RuleSoftwareQualityAttribute.COMPATIBILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY,
                    RuleSoftwareQualityAttribute.USABILITY);
    private final List<Violation> violationList = new ArrayList<>();
    private boolean isActive;

    public UnauthorizedRule(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public RuleCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public RuleSeverity getSeverityType() {
        return SEVERITY;
    }

    @Override
    public RuleType getRuleType() {
        return TYPE;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return SOFTWARE_QUALITY_ATTRIBUTE;
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
     * Checks if there is a violation against the 401 (unauthorized) rule. If the security is defined globally, the
     * 401 response must be defined for each request. If security is defined locally, the 401 response must be
     * defined only for the local request.
     *
     * @param openAPI the definition that will be checked against the rule.
     * @return the list of violations.
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<SecurityRequirement> security = openAPI.getSecurity();
        boolean globalSec = security != null && !security.isEmpty();

        for (Map.Entry<String, PathItem> path : openAPI.getPaths().entrySet()) {
            List<Operation> operations = getPathOperationsWithSec(path.getValue(), globalSec);

            for (Operation operation : operations) {
                if (operation.getResponses().containsKey("401")) continue;

                this.violationList.add(new Violation(this, locMapper.getLOCOfPath(path.getKey()),
                        ImprovementSuggestion.UNAUTHERIZED, path.getKey(), ErrorMessage.UNAUTHERIZED));
            }
        }
        return this.violationList;
    }

    /**
     * Gives for the path only the operations which have a security defined.
     *
     * @param pathItem  the path item (swagger) with operations and their security, etc.
     * @param globalSec if the security is globally defined.
     * @return the list of operations for the specified path for which security has been defined.
     */
    private List<Operation> getPathOperationsWithSec(PathItem pathItem, boolean globalSec) {
        List<Operation> operations = new ArrayList<>();
        if (pathItem.getGet() != null && (globalSec || pathItem.getGet().getSecurity() != null))
            operations.add(pathItem.getGet());
        else if (pathItem.getPut() != null && (globalSec || pathItem.getPut().getSecurity() != null))
            operations.add(pathItem.getPut());
        else if (pathItem.getPost() != null && (globalSec || pathItem.getPost().getSecurity() != null))
            operations.add(pathItem.getPost());
        else if (pathItem.getDelete() != null && (globalSec || pathItem.getDelete().getSecurity() != null))
            operations.add(pathItem.getDelete());

        return operations;
    }
}
