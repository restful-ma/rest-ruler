package cli;

import cli.utility.Output;
import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "java -jar rest-ruler.jar", description = "...", mixinStandardHelpOptions = true)
public class RestRulerCli implements Runnable {

    @Option(names = {"-p", "--path"}, required = true,
            description = "Local path or public URL to the OpenAPI definition file that should be analyzed (required); version 2.0 or higher in JSON or YAML are supported")
    private String openApiPath;

    @Option(names = {"-e", "--expertMode"},
            description = "Interactively select the rules for the analysis")
    private boolean expertMode;

    @Option(names = {"-r", "--report"},
            description = "Generate a Markdown report file with the analysis results")
    private boolean generateReport;

    @Option(names = {"-rn", "--reportName"},
            description = "Specify a custom filename for the Markdown report")
    private String filename;

    public static void main(String[] args) {
        PicocliRunner.run(RestRulerCli.class, args);
    }

    /**
     * When the user runs a command, this method is called.
     */
    public void run() {
        Output output = new Output();
        if (this.expertMode)
            output.askActiveRules();
        if (this.openApiPath != null) {

            if (filename != null)
                output.startAnalysis(this.openApiPath, this.filename);
            else
                output.startAnalysis(this.openApiPath, this.generateReport);
        }
    }
}
