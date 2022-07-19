package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import rest.studentproject.rules.attributes.RuleCategory;
import rest.studentproject.rules.attributes.RuleSeverity;
import rest.studentproject.rules.attributes.RuleSoftwareQualityAttribute;
import rest.studentproject.rules.attributes.RuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the rule: Underscores (_) should not be used in URI.
 */
public class UnderscoreRule implements IRestRule {
    private boolean isActive;
    private final List<Violation> violationList = new ArrayList<>();

    public UnderscoreRule(boolean isActive) {
        setIsActive(isActive);
    }

    @Override
    public String getTitle() {
        return "Underscores (_) should not be used in URI";
    }

    @Override
    public RuleCategory getCategory() {
        return RuleCategory.URIS;
    }

    @Override
    public RuleSeverity getSeverityType() {
        return RuleSeverity.ERROR;
    }

    @Override
    public RuleType getRuleType() {
        return RuleType.STATIC;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        List<RuleSoftwareQualityAttribute> softwareQualityAttributes = new ArrayList<>();
        softwareQualityAttributes.add(RuleSoftwareQualityAttribute.MAINTAINABILITY);
        return softwareQualityAttributes;
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
        return this.violationList.size() == 0 ? null : this.violationList;
    }

    /**
     * This method checks if the given path contains an underscore. If there is a parameter within the path, it will be deleted.
     *
     * @param path the path to check if it contains an underscore.
     */
    private void checkUnderscore(String path) {
        String pathWithoutVariable = path.replaceAll("\\{" + ".*" + "\\}", "");
        if (!pathWithoutVariable.contains("_")) return;
        this.violationList.add(new Violation(0, "", path, ""));

    }
}
