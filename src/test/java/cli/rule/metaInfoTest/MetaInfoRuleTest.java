package cli.rule.metaInfoTest;

import cli.analyzer.RestAnalyzer;
import cli.rule.Violation;
import cli.rule.rules.MetaInfoRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetaInfoRuleTest {
    private static final String PATH_INVALID_OPENAPI = "src/test/java/cli/rule/metaInfoTest/InvalidOpenAPIMetaInfoRule.json";
    private static final String VALID_OPENAPI = "src/test/java/cli/validopenapi/validOpenAPI.json";
    RestAnalyzer restAnalyzer;
    MetaInfoRule metaInfoRule;

    @Test
    @DisplayName("Test that checks if no meta info rule violation is detected when there is a correct OpenAPI definition.")
    void validFile() {
        List<Violation> violations = runMethodUnderTest(VALID_OPENAPI);
        assertEquals(0, violations.size(), "There should be no rule violation for the valid openAPI definition.");
    }

    @Test
    @DisplayName("Test that checks if meta info rule violations are detected when required meta information is missing.")
    void invalidFile() {
        List<Violation> violations = runMethodUnderTest(PATH_INVALID_OPENAPI);
        assertEquals(5, violations.size(), "There should be 5 rule violations for missing meta information.");
    }

    private List<Violation> runMethodUnderTest(String url) {
        this.restAnalyzer = new RestAnalyzer(url);
        this.metaInfoRule = new MetaInfoRule(true);
        return this.restAnalyzer.runAnalyse(List.of(this.metaInfoRule), false);
    }
} 