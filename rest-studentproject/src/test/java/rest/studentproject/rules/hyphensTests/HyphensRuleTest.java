package rest.studentproject.rules.hyphensTests;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.HyphensRule;
import rest.studentproject.rules.UnderscoreRule;
import rest.studentproject.rules.Violation;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HyphensRuleTest {
    HyphensRule hyphensRuleTest;
    RestAnalyzer restAnalyzer;


    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 6 paths contain a violation.")
    void checkViolationOnInvalidRESTFile2Violations() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/rules/hyphensTests/hyphensInvalid2Violations.json";

        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(2, violationToTest.size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 2 paths contain a violation.")
    void checkViolationOnInvalidRESTFile2ViolationsLowercase() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/rules/hyphensTests/hyphensInvalid2ViolationsLowercase.json";

        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(2, violationToTest.size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 9 paths contain a violation.")
    void checkViolationOnInvalidRESTFile6Violations() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/rules/hyphensTests/hyphensInvalid9ViolationsSpecialCharactersLowercase.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(9, violationToTest.size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 9 paths contain a violation.")
    void checkViolationOnValidRESTFile() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(0, violationToTest.size(),
                "Detection of violations should work.");
    }

    private List<Violation> runMethodUnderTest(String url) throws MalformedURLException {

        this.restAnalyzer = new RestAnalyzer(url);
        this.hyphensRuleTest = new HyphensRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.hyphensRuleTest), false);
    }
}
