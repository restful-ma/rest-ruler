package cli.rule.unauthorizedTest;

import cli.analyzer.RestAnalyzer;
import cli.rule.Violation;
import cli.rule.rules.UnauthorizedRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnauthorizedRuleTest {
    final static String PATH_GLOBAL_SEC = "src/test/java/cli/rule/unauthorizedTest" +
            "/InvalidOpernAPIUnauthorizedRuleGlobalSec.json";
    final static String PATH_LOCAL_SEC = "src/test/java/cli/rule/unauthorizedTest" +
            "/InvalidOpenAPIUnauthorizedRuleLocalSec.json";
    final static String PATH_TO_VALIDOPEAPI = "src/test/java/cli/validopenapi/validOpenAPI.json";
    final static List<String> PATHS_TO_OPENAPI = List.of(PATH_GLOBAL_SEC, PATH_LOCAL_SEC);

    RestAnalyzer restAnalyzer;
    UnauthorizedRule unauthorizedRule;


    
    @Test
    @DisplayName("Test that checks if the rule violation is detected for globally and locally defined security.")
    void checkStaticGlobalAndLocalSec() {
        for (String path : PATHS_TO_OPENAPI) {
            List<Violation> violations = runMethodUnderTest(path, false);
            assertEquals(3, violations.size());
        }
    }

    @Test
    @DisplayName("Test that checks if the rule violation is detected in a dynamic analysis.")
    void checkUnauthorizedDynamicAnalysis(){

    }

    @Test
    @DisplayName("Test that checks if there is no rule violation for the valid OpenAPI definition.")
    void checkStaticValidOpenAPI() {
        List<Violation> violations = runMethodUnderTest(PATH_TO_VALIDOPEAPI, false);
        assertTrue(violations.isEmpty());
    }

    private List<Violation> runMethodUnderTest(String path, boolean dynamicAnalysis) {
        this.restAnalyzer = new RestAnalyzer(path);
        RestAnalyzer.dynamicAnalysis = dynamicAnalysis;
        this.unauthorizedRule = new UnauthorizedRule(true);
        return new ArrayList<>(this.restAnalyzer.runAnalyse(List.of(this.unauthorizedRule), false));
    }
}
