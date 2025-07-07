package cli.ai;

import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.*;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

import java.util.List;

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
    
    public gpt() {
        this.client = OpenAIOkHttpClient.fromEnv();
    }

    public void tunnelingViolationAI(IRestRule rule, String keyPath, String description, String summary,
    String requestType, String requestTypeMessage, String improvement, List<Violation> violations) {
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
}
