package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import rest.studentproject.analyzer.LOCMapper;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.constants.RuleCategory;
import rest.studentproject.rules.constants.RuleSeverity;
import rest.studentproject.rules.constants.RuleSoftwareQualityAttribute;
import rest.studentproject.rules.constants.RuleType;

import java.util.*;

public class CRUDRule implements IRestRule {
    private static final String TITLE = "";
    private static final RuleCategory CATEGORY = RuleCategory.URIS;
    private static final RuleType TYPE = RuleType.STATIC;
    private static final RuleSeverity SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE = Arrays.asList(RuleSoftwareQualityAttribute.USABILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private static final String[] CRUDOPERATIONS = {"get", "post", "delete", "put"};
    private static LOCMapper locMapper;
    private final List<Violation> violationList = new ArrayList<>();
    private boolean isActive;

    public CRUDRule(boolean isActive) {
        this.isActive = isActive;
        locMapper = RestAnalyzer.locMapper;
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
     * @param openAPI
     * @return
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

        // Violate the underscore rule for the path list
        for (String path : paths) {
            if (path.trim().isEmpty()) continue;

            for (String crudOperation : CRUDOPERATIONS) {
                path = path.replaceAll("\\{" + ".*" + "\\}", "");
                if (!path.toLowerCase().contains(crudOperation)) continue;

                violationList.add(new Violation(locMapper.getLOCOfPath(path), "", path, ""));
            }
        }
        return violationList;
    }
}