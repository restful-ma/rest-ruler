package rest.studentproject.rules.crudtest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.CRUDRule;
import rest.studentproject.rules.UnderscoreRule;
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
        String path = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";

        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(0, violations.size(), "There should be no rule violation for the valid openAPI definition.");
    }

    @Test
    @DisplayName("Test that checks if the 13 crud rule violations are detected.")
    void invalidFile() throws MalformedURLException {
        String path = "src/test/java/rest/studentproject/rules/crudtest/InvalidOpenAPICRUDRule.json";

        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(13, violations.size(), "There should be 13 rule violations.");
    }

    private List<Violation> runMethodUnderTest(String url) throws MalformedURLException {

        this.restAnalyzer = new RestAnalyzer(url);
        this.crudRule = new CRUDRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.crudRule), false);
    }
}