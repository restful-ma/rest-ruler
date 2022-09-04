package rest.studentproject.rule.unauthorizedTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.rules.UnauthorizedRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UnauthorizedRuleTest {
    final static String PATH_GLOBAL_SEC = "src/test/java/rest/studentproject/rule/unauthorizedTest" +
            "/InvalidOpernAPIUnauthorizedRuleGlobalSec.json";
    final static String PATH_LOCAL_SEC = "src/test/java/rest/studentproject/rule/unauthorizedTest" +
            "/InvalidOpenAPIUnauthorizedRuleLocalSec.json";
    final static String PATH_TO_VALIDOPEAPI = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";
    final static List<String> PATHS_TO_OPENAPI = List.of(PATH_GLOBAL_SEC, PATH_LOCAL_SEC);

    RestAnalyzer restAnalyzer;
    UnauthorizedRule unauthorizedRule;

    @Test
    @DisplayName("Test that checks if the rule violation is detected for globally and locally defined security.")
    void checkGlobalAndLocalSec() {
        for (String path : PATHS_TO_OPENAPI) {
            List<Violation> violations = runMethodUnderTest(path);
            assertEquals(3, violations.size());
        }
    }

    @Test
    @DisplayName("Test that checks if there is no rule violation for the valid OpenAPI definition.")
    void checkValidOpenAPI() {
        List<Violation> violations = runMethodUnderTest(PATH_TO_VALIDOPEAPI);
        assertTrue(violations.isEmpty());
    }

    private List<Violation> runMethodUnderTest(String path) {
        this.restAnalyzer = new RestAnalyzer(path);
        this.unauthorizedRule = new UnauthorizedRule(true);
        return new ArrayList<>(this.restAnalyzer.runAnalyse(List.of(this.unauthorizedRule), false));
    }
}
