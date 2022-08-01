package rest.studentproject.rules.hyphensTests;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.rules.HyphensRule;
import rest.studentproject.rules.LowercaseRule;
import rest.studentproject.rules.Violation;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HyphensRuleTest {
    HyphensRule hyphensRuleTest;

    @BeforeEach
    void setUp() {
        hyphensRuleTest = new HyphensRule(true);

    }
    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 6 paths contain a violation.")
    void checkViolationOnInvalidRESTFile2Violations() {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation("src/test/java/rest/studentproject/rules/hyphensTests/hyphensInvalid2Violations.json", null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();
        List<Violation> violationToTest = null;
        try {
            violationToTest = hyphensRuleTest.checkViolation(openAPI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(2, violationToTest.size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 2 paths contain a violation.")
    void checkViolationOnInvalidRESTFile2ViolationsLowercase() {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation("src/test/java/rest/studentproject/rules/hyphensTests/hyphensInvalid2ViolationsLowercase.json", null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();
        List<Violation> violationToTest = null;
        try {
            violationToTest = hyphensRuleTest.checkViolation(openAPI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(2, violationToTest.size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Detect if a path segment contains more than a word. Here 9 paths contain a violation.")
    void checkViolationOnInvalidRESTFile6Violations() {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation("src/test/java/rest/studentproject/rules/hyphensTests/hyphensInvalid9ViolationsSpecialCharactersLowercase.json", null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();
        List<Violation> violationToTest = null;
        try {
            violationToTest = hyphensRuleTest.checkViolation(openAPI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(9, violationToTest.size(),
                "Detection of violations should work.");
    }
}
