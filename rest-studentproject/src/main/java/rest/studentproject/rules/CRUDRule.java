package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import rest.studentproject.rules.constants.*;

import static rest.studentproject.analyzer.RestAnalyzer.locMapper;

import java.util.*;

/**
 * Implementation of the rule: Underscores (_) should not be used in URI.
 */
public class CRUDRule implements IRestRule {
    private static final String TITLE = "CRUD function names should not be used in URIs";
    private static final RuleCategory CATEGORY = RuleCategory.URIS;
    private static final RuleType TYPE = RuleType.STATIC;
    private static final RuleSeverity SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE = Arrays.asList(RuleSoftwareQualityAttribute.USABILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private static final String[] CRUDOPERATIONS = {"get", "post", "delete", "put"};
    private final List<Violation> violationList = new ArrayList<>();
    private boolean isActive;

    public CRUDRule(boolean isActive) {
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
     * Checks if there is a violation against the CRUD rule. All paths and base URLs are checked.
     *
     * @param openAPI the definition that will be checked against the rule.
     * @return the list of violations.
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        // Duplicate code --> Refactor --> Code is more often used
        Set<String> paths = new HashSet<>(openAPI.getPaths().keySet());
        List<Server> servers = openAPI.getServers();

        // Add server paths to the path list
        for (Server server : servers) {
            if (server.getUrl().trim().isEmpty()) continue;
            paths.add(server.getUrl());
        }

        // Violate the CRUD rule for the path list
        for (String path : paths) {
            if (path.trim().isEmpty()) continue;

            // Goes through every CRUD operation --> If multiple CRUD operations are found --> Multiple violations
            for (String crudOperation : CRUDOPERATIONS) {
                String pathWithoutParameters = path.replaceAll("\\{" + ".*" + "\\}", "");
                if (pathWithoutParameters.toLowerCase().contains(crudOperation)) {
                    violationList.add(new Violation(this, locMapper.getLOCOfPath(path), "URIS should not be used to indicate that a CRUD function (" + crudOperation.toUpperCase() + ") is performed, instead HTTP request methods should be used for this.", path, ErrorMessage.CRUD));
                }
            }
        }
        return violationList;
    }
}