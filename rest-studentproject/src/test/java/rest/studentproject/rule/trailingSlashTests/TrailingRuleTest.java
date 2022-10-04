package rest.studentproject.rule.trailingSlashTests;

import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.rules.TrailingRule;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrailingRuleTest {

    RestAnalyzer restAnalyzer;
    TrailingRule trailingRule;

    @Test
    void checkViolation() {
        String path = "src/test/java/rest/studentproject/rule/tunnelingTests/InvalidOpenAPITrailingRule.json";

        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(2, violations.size(), "There should be 2 rule violations.");
    }

    private List<Violation> runMethodUnderTest(String url) {

        this.restAnalyzer = new RestAnalyzer(url);
        this.trailingRule = new TrailingRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.trailingRule), false);
    }
}