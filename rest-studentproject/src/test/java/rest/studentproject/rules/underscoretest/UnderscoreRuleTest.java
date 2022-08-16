package rest.studentproject.rules.underscoretest;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.LowercaseRule;
import rest.studentproject.rules.UnderscoreRule;
import rest.studentproject.rules.Violation;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnderscoreRuleTest {

    RestAnalyzer restAnalyzer;
    UnderscoreRule underscore;

    @Test
    @DisplayName("Test that checks if no violation is detected when there is a correct OpenAPI definition.")
    void validFile() throws MalformedURLException {


        String url = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";
        List<Violation> violations = runMethodUnderTest(url);
        assertEquals(0, violations.size(), "There should be no rule violation for the valid openAPI definition.");
    }


    @Test
    void invalidFile() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/rules/underscoretest/InvalidOpenAPI.json";

        List<Violation> violations = runMethodUnderTest(url);
        assertEquals(3, violations.size(), "There should be three rule violations. Three of the paths are valid.");
    }

    private List<Violation> runMethodUnderTest(String url) throws MalformedURLException {

        this.restAnalyzer = new RestAnalyzer(url);
        this.underscore = new UnderscoreRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.underscore),false);
    }
}