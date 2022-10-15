package rest.studentproject.rule.requestTypeDescriptionTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.rules.RequestTypeDescriptionRule;
import rest.studentproject.rule.rules.VerbPhraseRule;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestTypeDescriptionTest {
    RequestTypeDescriptionRule requestTypeDescriptionRuleTest;
    RestAnalyzer restAnalyzer;

    //TODO: Tests need to be changed with false request type and mismatching description
    @Test
    @DisplayName("Detect if a path segment contains a violation regarding the store or collection name")
    void checkViolationOnInvalidRESTFile2Violations() throws MalformedURLException {

        String url = "src/test/java/rest/studentproject/rule/requestTypeDescriptionTest/requestTypeDescription2Violations.json";

        List<Violation> violationToTest = runMethodUnderTest(url);

        assertEquals(6, violationToTest.size(),
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
        this.requestTypeDescriptionRuleTest = new RequestTypeDescriptionRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.requestTypeDescriptionRuleTest), false);
    }
}
