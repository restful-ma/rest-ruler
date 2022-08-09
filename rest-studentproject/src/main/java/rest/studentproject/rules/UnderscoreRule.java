package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import rest.studentproject.analyzer.LOCMapper;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.constants.RuleCategory;
import rest.studentproject.rules.constants.RuleSeverity;
import rest.studentproject.rules.constants.RuleSoftwareQualityAttribute;
import rest.studentproject.rules.constants.RuleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the rule: Underscores (_) should not be used in URI.
 */
public class UnderscoreRule implements IRestRule {
    private static final String TITLE = "Underscores (_) should not be used in URI";
    private static final RuleCategory CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity SEVERITY = RuleSeverity.ERROR;
    private static final RuleType TYPE = RuleType.STATIC;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = Arrays.asList(RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private static final List<Violation> violationList = new ArrayList<>();
    private static boolean isActive;
    private static LOCMapper locMapper;

    public UnderscoreRule(boolean isActive) {
        setIsActive(isActive);
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
        return SOFTWARE_QUALITY_ATTRIBUTES;
    }

    @Override
    public boolean getIsActive() {
        return isActive;
    }

    @Override
    public void setIsActive(boolean isActive) {
        UnderscoreRule.isActive = isActive;
    }

    /**
     * Checks if there is a violation against the underscore rule. All paths and base URLs are checked.
     *
     * @param openAPI the definition that will be checked against the rule.
     * @return the list of violations.
     */
    public List<Violation> checkViolation(OpenAPI openAPI) {
        Set<String> paths = openAPI.getPaths().keySet();
        List<Server> servers = openAPI.getServers();

        for (String path : paths) {
            if (path.trim().isEmpty()) continue;
            checkUnderscore(path);
        }

        for (Server server : servers) {
            if (server.getUrl().trim().isEmpty()) continue;
            checkUnderscore(server.getUrl());
        }
        return violationList;
    }

    /**
     * Checks if the given path contains an underscore. If there is a parameter within the path, it will be deleted.
     *
     * @param path the path to check if it contains an underscore.
     */
    private void checkUnderscore(String path) {
        String pathWithoutVariable = path.replaceAll("\\{" + ".*" + "\\}", "");
        if (!pathWithoutVariable.contains("_")) return;
        violationList.add(new Violation(locMapper.getLOCOfPath(path), "", path, ""));

    }
}
