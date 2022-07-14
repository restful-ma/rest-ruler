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
        PicocliRunner.run(RestParserCommand.class, args);
    }

    public void run() {
        // business logic here
        
        if (verbose) {
            System.out.println("TODO");
        }
    }
}
