package rest.studentproject.rule.contentTypeTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.rules.ContentTypeRule;
import rest.studentproject.rule.Violation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ContentTypeRuleTest {

    private static final String PATH_INVALID_OPENAPI = "src/test/java/rest/studentproject/rule/contentTypeTest/InvalidOpenAPIContentTypeRule.json";
    private static final String VALID_OPENAPI = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";
    RestAnalyzer restAnalyzer;
    ContentTypeRule contentTypeRule;

    @Test
    @DisplayName("Test that checks if no content type rule violation is detected when there is a correct OpenAPI definition.")
    void validFile() {
        List<Violation> violations = runMethodUnderTest(VALID_OPENAPI);
        assertEquals(0, violations.size(), "There should be no rule violation for the valid openAPI definition.");
    }

    @Test
    @DisplayName("Test that checks if the 20 content type rule violations are detected. Tested are the path parameters, request bodies and responses, if there is the content type defined.")
    void invalidFile() {
        List<Violation> violations = runMethodUnderTest(PATH_INVALID_OPENAPI);
        for (Violation violation : violations) {
            System.out.println(violation);
        }
        assertEquals(20, violations.size(), "There should be 20 rule violations.");
    }

    private List<Violation> runMethodUnderTest(String url) {

        this.restAnalyzer = new RestAnalyzer(url);
        this.contentTypeRule = new ContentTypeRule(true);
        return this.restAnalyzer.runAnalyse(List.of(this.contentTypeRule), false);
    }
}
