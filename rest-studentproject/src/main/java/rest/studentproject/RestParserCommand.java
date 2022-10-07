package rest.studentproject;

import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import rest.studentproject.utility.Output;

@Command(name = "rest-parser", description = "...", mixinStandardHelpOptions = true)
public class RestParserCommand implements Runnable {

    @Option(names = {"-e", "--expertMode"}, description = "Enable if you want to select the rules.")
    private boolean expertMode;
    @Option(names = {"-r", "--runAnalyse"}, description = "Run the rest analysis. Required: Path to openapi " +
            "definition (2.0 or higher; json or yaml)")
    private String path;

    public static void main(String[] args) {
        PicocliRunner.run(RestParserCommand.class, args);
    }

    /**
     * When the user runs a command, this method is called.
     */
    public void run() {
        // this.path = "https://api.apis.guru/v2/specs/abstractapi.com/geolocation/1.0.0/openapi.json";
        // this.path = "C:\\Users\\manue\\Documents\\Studium\\Master\\2.Semester\\Projektarbeit\\Projektarbeit-Master\\rest-studentproject\\src\\main\\java\\rest\\studentproject\\docs\\Oxford_OpenAPI.json";
        this.path = "C:\\Users\\manue\\Documents\\Studium\\Master\\2.Semester\\Projektarbeit\\Projektarbeit-Master\\rest-studentproject\\src\\test\\java\\rest\\studentproject\\rule\\contentTypeTest\\InvalidOpenAPIContentTypeRule.json";
        // this.path = "C:\\Users\\manue\\Documents\\Studium\\Master\\2.Semester\\Projektarbeit\\Projektarbeit-Master\\rest-studentproject\\src\\test\\java\\rest\\studentproject\\validopenapi\\validOpenAPI.json";
        this.expertMode = false;
        Output output = new Output();
        if (this.expertMode) output.askActiveRules();
        if (this.path != null) {
            output.startAnalysis(this.path);
        }
    }
}


