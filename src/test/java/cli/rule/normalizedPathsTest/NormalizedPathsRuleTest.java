package cli.rule.normalizedPathsTest;

import cli.analyzer.RestAnalyzer;
import cli.rule.Violation;
import cli.rule.rules.NormalizedPathsRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NormalizedPathsRuleTest {
    RestAnalyzer restAnalyzer;
    NormalizedPathsRule normalizedPathsRule;

    @Test
    @DisplayName("Test that checks if no violation is detected when there is a correct OpenAPI definition.")
    void validFile() {
        List<Violation> violations = runMethodUnderTest("src/test/java/cli/validopenapi/validOpenAPI.json");
        assertEquals(0, violations.size(), "There should be no rule violation for the valid openAPI definition.");
    }

    @Test
    @DisplayName("Test that checks if the normalized paths rule violations are detected.")
    void invalidFile() {
        List<Violation> violations = runMethodUnderTest("src/test/java/cli/rule/normalizedPathsTest/InvalidOpenAPINormalizedPathsRule.json");
        assertEquals(3, violations.size(), "There should be 3 rule violations for paths containing empty segments.");
    }

    private List<Violation> runMethodUnderTest(String url) {
        this.restAnalyzer = new RestAnalyzer(url);
        this.normalizedPathsRule = new NormalizedPathsRule(true);
        return this.restAnalyzer.runAnalyse(List.of(this.normalizedPathsRule), false);
    }
}
