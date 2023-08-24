package cli.rule.lowercaseTests;

import cli.analyzer.RestAnalyzer;
import cli.rule.Violation;
import cli.rule.rules.LowercaseRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LowercaseRuleTest {
    LowercaseRule lowercaseRule;
    RestAnalyzer restAnalyzer;


    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 6 violations.")
    void checkViolationOnInvalidRESTFile6Violations() {

        String url = "src/test/java/cli/rule/lowercaseTests/lowercaseInvalid6Violations.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(6, violationToTest.size(),
                "Detection of violations should work and detect 6 violations.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 3 violations.")
    void checkViolationOnInvalidRESTFile3Violations() {

        String url = "src/test/java/cli/rule/lowercaseTests/lowercaseInvalid3Violations.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(3, violationToTest.size(),
                "Detection of violations should work and detect 3 violations.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 0 violations and return an empty list.")
    void checkViolationOnInvalidRESTFileEmpty() {

        String url = "src/test/java/cli/rule/lowercaseTests/lowercaseInvalidEmptyPaths.json";
        List<Violation> violationToTest = runMethodUnderTest(url);


        assertEquals(0, violationToTest.size(),
                "Detection of violations should work and return an empty list.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 2 violations.")
    void checkViolationOnInvalidRESTFileEmptyURI() {

        String url = "src/test/java/cli/rule/lowercaseTests/lowercaseInvalidEmptyURI.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(2, violationToTest.size(),
                "Detection of violations should work and detect only 2 violations given 3 paths-uri.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 0 violations.")
    void checkViolationOnValidFile() {

        String url = "src/test/java/cli/validopenapi/validOpenAPI.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(0, violationToTest.size(),
                "Detection of violations should work and return an empty list.");
    }

    private List<Violation> runMethodUnderTest(String url) {

        this.restAnalyzer = new RestAnalyzer(url);
        this.lowercaseRule = new LowercaseRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.lowercaseRule), false);
    }
}
