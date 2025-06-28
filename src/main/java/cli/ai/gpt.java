package cli.ai;

import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.*;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import cli.utility.Output;
import com.openai.models.responses.ResponseCreateParams;

import java.util.*;

// gpt is a class that brings together the LLM-related functionality in the CLI
// different rules may use the methods in this class to establish a connection to an external LLM agent for rule evaluation
public class gpt {
    private OpenAIClient client;

    private final String modelNoResponse = "no";
    
    private final String systemMessageTunneling = """
        You are a REST API design validator. Your task is to determine whether HTTP method tunneling is present in a given endpoint.

        An endpoint is considered to be tunneling if it uses a certain HTTP method to perform actions that are more appropriately represented by another HTTP method.

        HTTP Method Definitions (use these exact criteria):
        - POST must be used to create a new resource in a collection or to execute controllers and not for other purposes
        - GET must be used to retrieve a representation of a resource and not for other purposes
        - DELETE should be used to delete a resource and not for other purposes
        - PUT must be used to both insert and update a stored resource and not for other purposes

        If the actual HTTP method does not align with the conventional RESTful method implied by the endpoint's summary or description, classify it as tunneling.

        Only respond with a one-word valid method name (one of POST, GET, DELETE or PUT) or "No" if no violation is detected.
        Do not explain your reasoning.

        Examples:

        Method: POST  
        Description: Update a list.  
        Summary: Update a list based on input.
        Response: PUT

        Method: PUT  
        Description: Submit a new application for review.  
        Summary: Create a new application  
        Response: POST

        Method: DELETE  
        Description: Deactivate a user account.  
        Summary: Soft delete a user  
        Response: No
        """;
    
    private final String systemMessagePluralName = """
        You are a REST API design validator. Your task is to determine whether a given URI violates the rule of alternating between singular and plural nouns. You must analyze each segment of the URI to check for violations.

        Rule Definition:
        The segments in a URI must alternate between singular and plural nouns. No two adjacent segments can both be singular or both be plural.

        Valid patterns:

        singular / plural / singular / plural / ...

        plural / singular / plural / singular / ...

        Evaluation Process:

        Split the URI into its segments (between slashes).

        Label each segment as singular or plural using the rules below.

        Evaluate the list of labels in order.

        If any two adjacent segments are both singular or both plural, this is a violation.

        Classification Rules:

        Path Parameters (enclosed in {}):
        Always treated as singular
        Example: {userId} = singular

        Compound Words:
        Use the final word to determine plurality. Compound words could be separated by dashes or just be one block consisting of two words.

        user-profiles → plural (ends in "profiles")

        information-item → singular (ends in "item")

        example-cases → plural (ends in "cases")

        transactionRules → plural (ends in "Rules")

        Special Words - Interchangeable: Words that only have singular forms (e.g., "information", "advice", "equipment", "furniture", "luggage") or only have plural forms (e.g., "jeans", "trousers", "pants", "scissors", "glasses", "clothes") are INTERCHANGEABLE. They can be treated as either singular or plural depending on what's needed to maintain the alternating pattern:
        "information" can be treated as singular OR plural
        "jeans" can be treated as singular OR plural
        "species" (same singular/plural form) can be treated as singular OR plural

        Abbreviations & Acronyms:
        Treated as singular unless clearly known to be plural (e.g., IDs)

        Violation Conditions:
        A violation occurs if:

        Two singular segments appear consecutively

        Two plural segments appear consecutively

        Two path parameters appear consecutively

        Examples:

        URI: /enterprises/enterprise/people/person
        Labels: plural / singular / plural / singular
        Output: No

        URI: /store/{storeId}/books
        Labels: singular / singular / plural
        Output: Yes

        URI: /users/{userId}/{profileId}
        Labels: plural / singular / singular
        Output: Yes

        URI: /activities/{id}/participant
        Labels: plural / singular / singular
        Output: Yes

        URI: /items/person/book
        Labels: plural / singular / singular
        Output: Yes

        URI: /schools/{student}/books
        Labels: plural / singular / plural
        Output: No

        URI: /adults/{get_adult_ID}/names
        Labels: plural / singular / plural
        Output: No

        URI: /information-item/{informationId}
        Labels: singular / singular
        Output: Yes

        URI: /cases-high-prio/{caseId}
        Labels: singular / singular
        Output: Yes

        URI: /people/{person}/student
        Labels: plural / singular / singular
        Output: Yes

        URI: /person/{person}
        Labels: singular / singular
        Output: Yes

        URI: /information/{informationId}
        Labels: plural (next node is singular, so treat as plural) / singular (path parameters are always singular)
        Output: No

        URI: /orgs/{org}/action/secret/{secret_name}/repositories/{repository_id}
        Labels: plural / singular / singular / singular / singular / plural / singular
        Output: Yes

        Instructions:
        Only reply with "Yes" if a violation is detected, or "No" if there is no violation. Do not explain your reasoning. Follow the rules and examples strictly. Apply reasoning to the entire URI and check each pair of segments.
        """;
    
    public gpt() {
        this.client = OpenAIOkHttpClient.fromEnv();
    }

    public void tunnelingViolationAI(IRestRule rule, String keyPath, String description, String summary,
    String requestType, String requestTypeMessage, String improvement, List<Violation> violations) {
        try {
            String input = this.constructTunnelingInput(description, summary, requestType);
            ResponseCreateParams params = ResponseCreateParams.builder()
            .temperature(0)
            .instructions(this.systemMessageTunneling)
            .input(input)
            .model(ChatModel.GPT_4_1_MINI)
            .build();
            Response response = this.client.responses().create(params);

            response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .flatMap(content -> content.outputText().stream())
                    .forEach(outputText -> {
                        String output = outputText.text();

                        if (!output.toLowerCase().equals(this.modelNoResponse) && !output.toUpperCase().equals(requestType.toUpperCase())) {
                            violations.add(new Violation(rule, RestAnalyzer.locMapper.getLOCOfPath(keyPath),
                                    requestTypeMessage + improvement + output,
                                    keyPath,
                                    ErrorMessage.REQUESTTYPE));
                        }
                    });
        } catch (Exception e) {
            // Log the error but don't crash the analysis
            System.err.println("GPT API error for " + keyPath + ": " + e.getMessage());
        }
    }

    private String constructTunnelingInput(String description, String summary, String requestType) {
        String res = String.format("Method: %s", requestType);

        if (description != null && !description.isEmpty()) {
            res += String.format("\nDescription: %s", description);
        }

        if (summary != null && !summary.isEmpty()) {
            res += String.format("\nSummary: %s", summary);
        }

        return res;
    }

    public List<Violation> pluralNameAI(IRestRule rule, List<Violation> violations, Set<String> paths) {
        int curPath = 1;
        int totalPaths = paths.size();
        for (String path : paths) {
            Output.progressPercentage(curPath, totalPaths);;
            
            if (path.trim().equals("")) {
                continue;
            }
            
            try {
                ResponseCreateParams params = ResponseCreateParams.builder()
                .temperature(0)
                .instructions(this.systemMessagePluralName)
                .input(path)
                .model(ChatModel.GPT_4_1_MINI)
                .build();
                
                Response response = this.client.responses().create(params);
        
                response.output().stream()
                        .flatMap(item -> item.message().stream())
                        .flatMap(message -> message.content().stream())
                        .flatMap(content -> content.outputText().stream())
                        .forEach(outputText -> {
                            String output = outputText.text();
        
                            if (!output.toLowerCase().equals(this.modelNoResponse)) {
                                violations.add(new Violation(rule, RestAnalyzer.locMapper.getLOCOfPath(path),
                                ImprovementSuggestion.PLURAL_NAME,
                                        path,
                                        ErrorMessage.PLURAL_NAME));
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            curPath++;
        }

        return violations;
    }
}
