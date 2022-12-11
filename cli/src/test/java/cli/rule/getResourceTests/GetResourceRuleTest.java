package cli.rule.getResourceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import cli.analyzer.RestAnalyzer;
import cli.rule.rules.GetResourceRule;
import cli.rule.Violation;
import cli.rule.constants.ErrorMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetResourceRuleTest {

    RestAnalyzer restAnalyzer;
    GetResourceRule getResourceRule;


    @BeforeEach
    void setUp() {
        getResourceRule = new GetResourceRule(true);
    }

    @Test
    @DisplayName("Get Requests have no request body")
    void checkRequestBody() {

        String path = "src/test/java/cli/rule/getResourceTests/requestBody_test.json";

        this.restAnalyzer = new RestAnalyzer(path);

        List<Violation> violationList = restAnalyzer.runAnalyse(List.of(this.getResourceRule), false);

        assertFalse(violationList.isEmpty());
        assertEquals(1, violationList.size());

        assertEquals(ErrorMessage.GET_RESOURCE_REQUEST_BODY, violationList.get(0).getErrorMessage());
    }

    @Test
    @DisplayName("Get Requests contain a representation of the response content")
    void checkResponseContent() {
        String path = "src/test/java/cli/rule/getResourceTests/requestResponse_test.json";

        this.restAnalyzer = new RestAnalyzer(path);

        List<Violation> violationList = restAnalyzer.runAnalyse(List.of(this.getResourceRule), false);

        assertFalse(violationList.isEmpty());
        assertEquals(4, violationList.size());

        //breakdown on different violations caught
        long num_missing_content =
                violationList.stream().filter(v -> v.getErrorMessage().equals(ErrorMessage.GET_RESOURCE)).count();
        long num_missing_response =
                violationList.stream().filter(v -> v.getErrorMessage().equals(ErrorMessage.GET_RESOURCE_MISSING_RESPONSE)).count();

        assertEquals(3, num_missing_content, "Number of incomplete/missing response content definition");
        assertEquals(1, num_missing_response, "Number of missing responses");
    }

    @Test
    @DisplayName("Valid Requests")
    void checkValidContent() {

        String path = "src/test/java/cli/rule/getResourceTests/valid_requests_test.json";

        this.restAnalyzer = new RestAnalyzer(path);

        List<Violation> violationList = restAnalyzer.runAnalyse(List.of(this.getResourceRule), false);

        assertTrue(violationList.isEmpty());
    }
}