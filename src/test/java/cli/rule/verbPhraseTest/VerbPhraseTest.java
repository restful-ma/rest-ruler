package cli.rule.verbPhraseTest;

import cli.rule.Violation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import cli.analyzer.RestAnalyzer;
import cli.rule.rules.VerbPhraseRule;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerbPhraseTest {
    VerbPhraseRule verbPhraseRuleTest;
    RestAnalyzer restAnalyzer;


    @Test
    @DisplayName("Detect if a path segment contains a violation regarding the store or collection name")
    void checkViolationOnInvalidRESTFile2Violations() throws MalformedURLException {

        String url = "src/test/java/cli/rule/verbPhraseTest/verbPhrase2Violations.json";

        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(2, violationToTest.size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Test a valid api. No error should be detected.")
    void checkViolationOnValidRESTFile() throws MalformedURLException {

        String url = "src/test/java/cli/validopenapi/validOpenAPI.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(0, violationToTest.size(),
                "Detection of violations should work.");
    }

    private List<Violation> runMethodUnderTest(String url) throws MalformedURLException {

        this.restAnalyzer = new RestAnalyzer(url);
        this.verbPhraseRuleTest = new VerbPhraseRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.verbPhraseRuleTest), false);
    }
}
