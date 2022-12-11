package cli.rule.trailingSlashTests;

import cli.analyzer.RestAnalyzer;
import cli.rule.Violation;
import cli.rule.rules.TrailingRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrailingRuleTest {

    RestAnalyzer restAnalyzer;
    TrailingRule trailingRule;

    @Test
    void checkViolation() {
        String path = "src/test/java/cli/rule/tunnelingTests/InvalidOpenAPITrailingRule.json";

        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(2, violations.size(), "There should be 2 rule violations.");
    }

    private List<Violation> runMethodUnderTest(String url) {

        this.restAnalyzer = new RestAnalyzer(url);
        this.trailingRule = new TrailingRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.trailingRule), false);
    }
}