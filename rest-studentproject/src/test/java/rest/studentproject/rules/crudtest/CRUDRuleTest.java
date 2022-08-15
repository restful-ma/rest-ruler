package rest.studentproject.rules.crudtest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.CRUDRule;
import rest.studentproject.rules.Violation;


import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CRUDRuleTest {
    RestAnalyzer restAnalyzer;
    CRUDRule crudRule;

    @Test
    @DisplayName("Test that checks if no crud rule violation is detected when there is a correct OpenAPI definition.")
    void validFile() throws MalformedURLException {
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/validopenapi/validOpenAPI.json");
        this.crudRule = new CRUDRule(true);
        List<List<Violation>> violations = this.restAnalyzer.runAnalyse(List.of(this.crudRule));
        assertEquals(0, violations.get(0).size(), "There should be no rule violation for the valid openAPI definition.");
    }

    @Test
    @DisplayName("Test that checks if the 11 crud rule violations are detected.")
    void invalidFile() throws MalformedURLException {
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/rules/crudtest/InvalidOpenAPICRUDRule.json");
        this.crudRule = new CRUDRule(true);
        List<List<Violation>> violations = this.restAnalyzer.runAnalyse(List.of(this.crudRule));
        assertEquals(11, violations.get(0).size(), "There should be 11 rule violations. 11 of the paths are valid.");
    }
}