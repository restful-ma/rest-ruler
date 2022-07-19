package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import rest.studentproject.report.Report;

import java.util.List;
import java.util.Set;

public class UnderscoreRule implements IRestRule {
    @Override
    public String title() {
        return "Underscores (_) should not be used in URI";
    }

    @Override
    public String description() {
        // TODO
        return null;
    }

    @Override
    public RuleCategory category() {
        return RuleCategory.URIS;
    }

    @Override
    public RuleSeverity severityType() {
        return RuleSeverity.ERROR;
    }

    @Override
    public RuleType ruleType() {
        return RuleType.STATIC;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public Report checkViolation(OpenAPI openAPI) {
        Set<String> paths = openAPI.getPaths().keySet();
        List<Server> servers = openAPI.getServers();

        for (String path : paths) {
            path = path.replaceAll("\\{" + ".*" + "\\}", "");
            if (path.contains("_")) {
                System.out.println("Path contains _: ");
                System.out.println(path);
            } else {
                System.out.println("Path does not contain _");
                System.out.println(path);
            }
            // TODO: Handle base URL
        }
        return null;
    }
}
