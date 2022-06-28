package restapianalyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {

        // parse a swagger description from the petstore and get the result
        SwaggerParseResult result = new OpenAPIParser().readLocation("https://api.apis.guru/v2/specs/github.com/1.1.4/openapi.json", null, null);
  
        // or from a file
        //   SwaggerParseResult result = new OpenAPIParser().readLocation("./path/to/openapi.yaml", null, null);

        // the parsed POJO
        OpenAPI openAPI = result.getOpenAPI();

        if (result.getMessages() != null) result.getMessages().forEach(System.err::println); // validation errors and warnings

        // the parsed model (if the parsing was successful) or null (if not)    
        if (openAPI != null) {  
            System.out.println(openAPI.getInfo().getTitle());
            System.out.println(openAPI.getInfo().getVersion());
        }
        System.out.println("Hello World!");
    }
}
