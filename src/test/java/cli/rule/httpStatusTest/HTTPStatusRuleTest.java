package cli.rule.httpStatusTest;

import cli.analyzer.RestAnalyzer;
import cli.rule.Violation;
import cli.rule.rules.HTTPStatusRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPStatusRuleTest {
    private RestAnalyzer restAnalyzer;
    private HTTPStatusRule httpStatusRule;

    @Test
    @DisplayName("Test that checks if no violations are detected when using official HTTP status codes")
    void validFile() {
        String path = "src/test/java/cli/validopenapi/validOpenAPI.json";
        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(0, violations.size(), "There should be no rule violations for valid HTTP status codes");
    }

    @Test
    @DisplayName("Test that checks if violations are detected when using non-official HTTP status codes")
    void invalidNonOfficialStatusCodes() {
        String path = "src/test/java/cli/rule/httpStatusTest/InvalidOpenAPIHTTPStatusNonOfficial.json";
        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(3, violations.size(), "There should be 3 violations for non-official HTTP status codes");
    }

    @Test
    @DisplayName("Test that checks if violations are detected when using non-numeric status codes")
    void invalidNonNumericStatusCodes() {
        String path = "src/test/java/cli/rule/httpStatusTest/InvalidOpenAPIHTTPStatusNonNumeric.json";
        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(2, violations.size(), "There should be 2 violations for non-numeric HTTP status codes");
    }

    private List<Violation> runMethodUnderTest(String url) {
        this.restAnalyzer = new RestAnalyzer(url);
        this.httpStatusRule = new HTTPStatusRule(true);
        return this.restAnalyzer.runAnalyse(List.of(this.httpStatusRule), false);
    }
} 