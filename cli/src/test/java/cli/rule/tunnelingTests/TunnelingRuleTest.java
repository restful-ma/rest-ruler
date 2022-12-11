package cli.rule.tunnelingTests;


import cli.rule.Violation;
import org.junit.jupiter.api.Test;
import cli.analyzer.RestAnalyzer;
import cli.rule.rules.TunnelingRule;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TunnelingRuleTest {

    RestAnalyzer restAnalyzer;
    TunnelingRule tunnelingRule;

    @Test
    void checkViolation() {

        //uses Test JSON for CRUD violations
        String path = "src/test/java/cli/rule/crudtest/InvalidOpenAPICRUDRule.json";

        List<Violation> violations = runMethodUnderTest(path);

        assertFalse(violations.isEmpty());
        assertEquals(9, violations.size());
    }

    private List<Violation> runMethodUnderTest(String url) {

        this.restAnalyzer = new RestAnalyzer(url);
        this.tunnelingRule = new TunnelingRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.tunnelingRule), false);
    }
}