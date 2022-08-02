package rest.studentproject.rules.underscoretest;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.UnderscoreRule;
import rest.studentproject.rules.Violation;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UnderscoreRuleTest {

    RestAnalyzer restAnalyzer;
    UnderscoreRule underscore;

    @Test
    @DisplayName("Test that checks if no violation is detected when there is a correct OpenAPI definition.")
    void validFile() throws MalformedURLException {
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/validopenapi/validOpenAPI.json");
        this.underscore = new UnderscoreRule(true);
        List<List<Violation>> violations = this.restAnalyzer.runAnalyse(List.of(this.underscore));
        assertEquals(null, violations.get(0), "There should be no rule violation for the valid openAPI definition.");
    }

    @Test
    void invalidFile() throws MalformedURLException {
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/rules/underscoretest/InvalidOpenAPI.json");
        this.underscore = new UnderscoreRule(true);
        List<List<Violation>> violations = this.restAnalyzer.runAnalyse(List.of(this.underscore));
        assertEquals(3, violations.get(0).size(), "There should be three rule violations. Three of the paths are valid.");

    }


}