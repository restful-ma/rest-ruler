package rest.studentproject.rules.hyphensTests;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.HyphensRule;
import rest.studentproject.rules.LowercaseRule;
import rest.studentproject.rules.Violation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HyphensRuleTest {
    HyphensRule hyphensRuleTest;
    RestAnalyzer restAnalyzer;

    @BeforeEach
    void setUp() {
        hyphensRuleTest = new HyphensRule(true);

    }
    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 6 paths contain a violation.")
    void checkViolationOnInvalidRESTFile2Violations() throws MalformedURLException {
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/rules/hyphensTests/hyphensInvalid2Violations.json");
        List<List<Violation>> violationToTest = this.restAnalyzer.runAnalyse(List.of(this.hyphensRuleTest));
        assertEquals(2, violationToTest.get(0).size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 2 paths contain a violation.")
    void checkViolationOnInvalidRESTFile2ViolationsLowercase() throws MalformedURLException {
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/rules/hyphensTests/hyphensInvalid2ViolationsLowercase.json");
        List<List<Violation>> violationToTest = this.restAnalyzer.runAnalyse(List.of(this.hyphensRuleTest));;
        assertEquals(2, violationToTest.get(0).size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 9 paths contain a violation.")
    void checkViolationOnInvalidRESTFile6Violations() throws MalformedURLException {
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/rules/hyphensTests/hyphensInvalid9ViolationsSpecialCharactersLowercase.json");
        List<List<Violation>> violationToTest = this.restAnalyzer.runAnalyse(List.of(this.hyphensRuleTest));;
        assertEquals(9, violationToTest.get(0).size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 9 paths contain a violation.")
    void checkViolationOnValidRESTFile() throws MalformedURLException {
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/validopenapi/validOpenAPI.json");
        List<List<Violation>> violationToTest = this.restAnalyzer.runAnalyse(List.of(this.hyphensRuleTest));;
        assertEquals(0, violationToTest.get(0).size(),
                "Detection of violations should work.");
    }
}
