package rest.studentproject.rules.singularDocumentNameTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.rules.SingularDocumentNameRule;


import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SingularDocumentNameTest {
    SingularDocumentNameRule singularDocumentNameRuleTest;
    RestAnalyzer restAnalyzer;


    @Test
    @DisplayName("Detect if a path segment contains a violation regarding the document name")
    void checkViolationOnInvalidRESTFile2Violations() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/rule/singularDocumentNameTest/singularDocumentName2Violations.json";

        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(2, violationToTest.size(),
                "Detection of violations should work.");
    }

    @Test
    @DisplayName("Test a valid api. No error should be detected.")
    void checkViolationOnValidRESTFile() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";
        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(0, violationToTest.size(),
                "Detection of violations should work.");
    }

    private List<Violation> runMethodUnderTest(String url) throws MalformedURLException {

        this.restAnalyzer = new RestAnalyzer(url);
        this.singularDocumentNameRuleTest = new SingularDocumentNameRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.singularDocumentNameRuleTest), false);
    }
}

