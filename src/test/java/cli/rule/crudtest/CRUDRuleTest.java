package cli.rule.crudtest;

import cli.analyzer.RestAnalyzer;
import cli.rule.Violation;
import cli.rule.rules.CRUDRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CRUDRuleTest {
    RestAnalyzer restAnalyzer;
    CRUDRule crudRule;

    @Test
    @DisplayName("Test that checks if no crud rule violation is detected when there is a correct OpenAPI definition.")
    void validFile() {
        String path = "src/test/java/cli/validopenapi/validOpenAPI.json";

        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(0, violations.size(), "There should be no rule violation for the valid openAPI definition.");
    }

    @Test
    @DisplayName("Test that checks if the 13 crud rule violations are detected.")
    void invalidFile() {
        String path = "src/test/java/cli/rule/crudtest/InvalidOpenAPICRUDRule.json";

        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(13, violations.size(), "There should be 13 rule violations.");
    }

    private List<Violation> runMethodUnderTest(String url) {

        this.restAnalyzer = new RestAnalyzer(url);
        this.crudRule = new CRUDRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.crudRule), false);
    }
}