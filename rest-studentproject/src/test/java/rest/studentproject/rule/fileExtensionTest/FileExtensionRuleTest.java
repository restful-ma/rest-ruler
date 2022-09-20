package rest.studentproject.rule.fileExtensionTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.rules.FileExtensionRule;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileExtensionRuleTest {

    RestAnalyzer restAnalyzer;
    FileExtensionRule fileExtensionRule;

    @Test
    @DisplayName("Test that checks if no crud rule violation is detected when there is a correct OpenAPI definition.")
    void validFile() {
        String path = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";

        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(0, violations.size(), "There should be no rule violation for the valid openAPI definition.");
    }

    @Test
    @DisplayName("Test that checks if the 13 crud rule violations are detected.")
    void invalidFile() {
        String path = "src/test/java/rest/studentproject/rule/fileExtensionTest/InvalidOpenAPIFileExtensionRule.json";

        List<Violation> violations = runMethodUnderTest(path);
        assertEquals(4, violations.size(), "There should be 4 rule violations.");
    }

    private List<Violation> runMethodUnderTest(String url) {

        this.restAnalyzer = new RestAnalyzer(url);
        this.fileExtensionRule = new FileExtensionRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.fileExtensionRule), false);
    }

}