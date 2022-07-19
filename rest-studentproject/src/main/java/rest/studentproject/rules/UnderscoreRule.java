package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UnderscoreRule implements IRestRule {
    private boolean isActive = false;

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

    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violationList = new ArrayList<>();
        Set<String> paths = openAPI.getPaths().keySet();
        List<Server> servers = openAPI.getServers();

        for (String path : paths) {
            checkUnderscore(path, violationList);
        }

        for (Server server : servers) {
            checkUnderscore(server.getUrl(), violationList);
        }
        return violationList;
    }

    private void checkUnderscore(String path, List<Violation> violationList) {
        String pathWithoutVariable = path.replaceAll("\\{" + ".*" + "\\}", "");
        if (pathWithoutVariable.contains("_")) {
            Violation violation = new Violation();
            violation.setKeyViolation(path);
            violation.setLineViolation(0);
            violation.setErrorMessage("");
            violation.setImprovementSuggestion("");
            violationList.add(violation);
        }
    }
}
