package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.rules.IRestRule;
import rest.studentproject.rules.Violation;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class RestAnalyzer {
    public static LOCMapper locMapper = null;
    private OpenAPI openAPI;

    public RestAnalyzer(String url) throws MalformedURLException {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(url, null, null);
        this.openAPI = swaggerParseResult.getOpenAPI();
        this.locMapper = new LOCMapper(openAPI, url);
    }

    public List<List<Violation>> runAnalyse(List<IRestRule> activeRules) {
        List<List<Violation>> violations = new ArrayList<>();
        for (IRestRule rule : activeRules){
            if (!rule.getIsActive()) continue;
            violations.add(rule.checkViolation(this.openAPI));
        }

        return violations;
    }
}
