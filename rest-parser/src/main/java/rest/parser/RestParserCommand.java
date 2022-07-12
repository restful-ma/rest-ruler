package rest.parser;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "rest-parser", description = "...",
        mixinStandardHelpOptions = true)
public class RestParserCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        SwaggerParseResult result = new OpenAPIParser().readLocation("C:/Users/manue/Documents/Studium/Master/2.Semester/Projektarbeit/Projektarbeit-Master/docs/studiendesign/evaluation/gold-standard/static-OpenAPI.yaml", null, null);
        System.out.println("Hi!" + result.getOpenAPI().getComponents().getSecuritySchemes());
        PicocliRunner.run(RestParserCommand.class, args);
    }

    public void run() {
        // business logic here
        
        if (verbose) {
            System.out.println("Hi!");
        }
    }
}
