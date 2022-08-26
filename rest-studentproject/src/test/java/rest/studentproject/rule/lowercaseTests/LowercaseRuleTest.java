package rest.studentproject.rule.lowercaseTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.rules.LowercaseRule;
import rest.studentproject.rule.Violation;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LowercaseRuleTest {
    LowercaseRule lowercaseRule;
    RestAnalyzer restAnalyzer;


    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 6 violations.")
    void checkViolationOnInvalidRESTFile6Violations() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/rules/lowercaseTests/lowercaseInvalid6Violations.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(6, violationToTest.size(),
                "Detection of violations should work and detect 6 violations.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 3 violations.")
    void checkViolationOnInvalidRESTFile3Violations() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/rules/lowercaseTests/lowercaseInvalid3Violations.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(3, violationToTest.size(),
                "Detection of violations should work and detect 3 violations.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 0 violations and return an empty list.")
    void checkViolationOnInvalidRESTFileEmpty() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/rules/lowercaseTests/lowercaseInvalidEmptyPaths.json";
        List<Violation> violationToTest = runMethodUnderTest(url);


        assertEquals(0, violationToTest.size(),
                "Detection of violations should work and return an empty list.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 2 violations.")
    void checkViolationOnInvalidRESTFileEmptyURI() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/rules/lowercaseTests/lowercaseInvalidEmptyURI.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(2, violationToTest.size(),
                "Detection of violations should work and detect only 2 violations given 3 paths-uri.");
    }

    @Test
    @DisplayName("Detection of uppercase letters should be successful in detecting 0 violations.")
    void checkViolationOnValidFile() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(0, violationToTest.size(),
                "Detection of violations should work and return an empty list.");
    }

    private List<Violation> runMethodUnderTest(String url) throws MalformedURLException {

        this.restAnalyzer = new RestAnalyzer(url);
        this.lowercaseRule = new LowercaseRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.lowercaseRule), false);
    }
}
