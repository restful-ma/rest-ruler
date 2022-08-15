package rest.studentproject.rules.lowercaseTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.LowercaseRule;
import rest.studentproject.rules.Violation;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LowercaseRuleTest {
    LowercaseRule lowercaseRule;
    RestAnalyzer restAnalyzer;

    @BeforeEach
    void setUp() {
        lowercaseRule = new LowercaseRule(true);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 6 violations.")
    void checkViolationOnInvalidRESTFile6Violations() throws MalformedURLException {
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/rules/lowercaseTests/lowercaseInvalid6Violations.json");
        List<List<Violation>> violationToTest = this.restAnalyzer.runAnalyse(List.of(this.lowercaseRule));
        assertEquals(6, violationToTest.get(0).size(),
                "Detection of violations should work and detect 6 violations.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 3 violations.")
    void checkViolationOnInvalidRESTFile3Violations() throws MalformedURLException{
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/rules/lowercaseTests/lowercaseInvalid3Violations.json");
        List<List<Violation>> violationToTest = this.restAnalyzer.runAnalyse(List.of(this.lowercaseRule));
        assertEquals(3, violationToTest.get(0).size(),
                "Detection of violations should work and detect 3 violations.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 0 violations and return an empty list.")
    void checkViolationOnInvalidRESTFileEmpty() throws MalformedURLException{
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/rules/lowercaseTests/lowercaseInvalidEmptyPaths.json");
        List<List<Violation>> violationToTest = this.restAnalyzer.runAnalyse(List.of(this.lowercaseRule));
        assertEquals(0, violationToTest.get(0).size(),
                "Detection of violations should work and return an empty list.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 2 violations.")
    void checkViolationOnInvalidRESTFileEmptyURI() throws MalformedURLException{
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/rules/lowercaseTests/lowercaseInvalidEmptyURI.json");
        List<List<Violation>> violationToTest = this.restAnalyzer.runAnalyse(List.of(this.lowercaseRule));
        assertEquals(2, violationToTest.get(0).size(),
                "Detection of violations should work and detect only 2 violations given 3 paths-uri.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 0 violations.")
    void checkViolationOnValidFile() throws MalformedURLException{
        this.restAnalyzer = new RestAnalyzer("src/test/java/rest/studentproject/validopenapi/validOpenAPI.json");
        List<List<Violation>> violationToTest = this.restAnalyzer.runAnalyse(List.of(this.lowercaseRule));
        assertEquals(0, violationToTest.get(0).size(),
                "Detection of violations should work and return an empty list.");
    }
}