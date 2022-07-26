package rest.studentproject.rules.lowercaseTests;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.rules.LowercaseRule;
import rest.studentproject.rules.Violation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LowercaseRuleTest {
    LowercaseRule lowercaseRule;

    @BeforeEach
    void setUp() {
        lowercaseRule = new LowercaseRule(true);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 6 violations.")
    void checkViolationOnInvalidRESTFile6Violations() {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation("bin/test/rest/studentproject/rules/lowercaseTests/lowercaseInvalid6Violations.json", null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();
        List<Violation> violationToTest = lowercaseRule.checkViolation(openAPI);
        assertEquals(6, violationToTest.size(),
                "Detection of violations should work and detect 6 violations.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 3 violations.")
    void checkViolationOnInvalidRESTFile3Violations() {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation("bin/test/rest/studentproject/rules/lowercaseTests/lowercaseInvalid3Violations.json", null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();
        List<Violation> violationToTest = lowercaseRule.checkViolation(openAPI);
        assertEquals(3, violationToTest.size(),
                "Detection of violations should work and detect 3 violations.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 0 violations and return an empty list.")
    void checkViolationOnInvalidRESTFileEmpty() {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation("bin/test/rest/studentproject/rules/lowercaseTests/lowercaseInvalidEmptyPaths.json", null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();
        List<Violation> violationToTest = lowercaseRule.checkViolation(openAPI);
        assertEquals(0, violationToTest.size(),
                "Detection of violations should work and return an empty list.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 2 violations.")
    void checkViolationOnInvalidRESTFileEmptyURI() {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation("bin/test/rest/studentproject/rules/lowercaseTests/lowercaseInvalidEmptyURI.json", null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();
        List<Violation> violationToTest = lowercaseRule.checkViolation(openAPI);
        assertEquals(2, violationToTest.size(),
                "Detection of violations should work and detect only 2 violations given 3 paths-uri.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 0 violations.")
    void checkViolationOnValidFile() {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation("bin/test/rest/studentproject/rules/lowercaseTests/lowercaseValidFile.json", null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();
        List<Violation> violationToTest = lowercaseRule.checkViolation(openAPI);
        assertEquals(0, violationToTest.size(),
                "Detection of violations should work and return an empty list.");
    }
}
