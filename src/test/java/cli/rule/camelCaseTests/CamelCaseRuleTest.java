package cli.rule.camelCaseTests;

import cli.analyzer.RestAnalyzer;
import cli.rule.Violation;
import cli.rule.rules.CamelCaseRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CamelCaseRuleTest {
    CamelCaseRule camelCaseRule;
    RestAnalyzer restAnalyzer;

    @Test
    @DisplayName("Test that checks if no camelCase rule violation is detected when there is a correct OpenAPI definition.")
    void validFile() {
        String url = "src/test/java/cli/validopenapi/validOpenAPI.json";
        List<Violation> violations = runMethodUnderTest(url);
        assertEquals(0, violations.size(), "There should be no rule violation for the valid openAPI definition.");
    }

    @Test
    @DisplayName("Test that checks if camelCase rule violations are detected for paths with separators and incorrect casing.")
    void invalidFile() {
        String url = "src/test/java/cli/rule/camelCaseTests/camelCaseInvalid.json";
        List<Violation> violations = runMethodUnderTest(url);
        
        // Expected violations:
        // 1. /user-profile (contains hyphen)
        // 2. /user_profile (contains underscore)
        // 3. /UserProfile (starts with uppercase)
        // 4. /userprofile (no camelCase for multi-word)
        // 5. /book-author (contains hyphen)
        // 6. /book_author (contains underscore)
        assertEquals(6, violations.size(), "There should be 6 rule violations for paths with incorrect camelCase formatting.");
    }

    private List<Violation> runMethodUnderTest(String url) {
        this.restAnalyzer = new RestAnalyzer(url);
        this.camelCaseRule = new CamelCaseRule(true);
        return this.restAnalyzer.runAnalyse(List.of(this.camelCaseRule), false);
    }
} 