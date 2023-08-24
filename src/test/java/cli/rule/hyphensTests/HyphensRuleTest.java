package cli.rule.hyphensTests;

import cli.analyzer.RestAnalyzer;
import cli.rule.Violation;
import cli.rule.rules.HyphensRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HyphensRuleTest {
    HyphensRule hyphensRuleTest;
    RestAnalyzer restAnalyzer;


    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 6 paths contain a violation.")
    void checkViolationOnInvalidRESTFile2Violations() {

        String url = "src/test/java/cli/rule/hyphensTests/hyphensInvalid2Violations.json";

        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(2, violationToTest.size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 2 paths contain a violation.")
    void checkViolationOnInvalidRESTFile2ViolationsLowercase() {

        String url = "src/test/java/cli/rule/hyphensTests/hyphensInvalid2ViolationsLowercase.json";

        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(2, violationToTest.size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 9 paths contain a violation.")
    void checkViolationOnInvalidRESTFile6Violations() {

        String url = "src/test/java/cli/rule/hyphensTests/hyphensInvalid9ViolationsSpecialCharactersLowercase.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(9, violationToTest.size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 9 paths contain a violation.")
    void checkViolationOnValidRESTFile() {

        String url = "src/test/java/cli/validopenapi/validOpenAPI.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(0, violationToTest.size(),
                "Detection of violations should work.");
    }

    private List<Violation> runMethodUnderTest(String url) {

        this.restAnalyzer = new RestAnalyzer(url);
        this.hyphensRuleTest = new HyphensRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.hyphensRuleTest), false);
    }
}
