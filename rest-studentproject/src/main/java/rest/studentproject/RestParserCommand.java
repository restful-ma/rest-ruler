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

    @Option(names = {"-o", "--out"}, description = "generates a report/output file")
    private boolean generateReport;

    @Option(names = {"-t", "--title"}, description = "generates a report file with a custom title")
    private String title;

    public static void main(String[] args) {
        PicocliRunner.run(RestParserCommand.class, args);
    }

    /**
     * When the user runs a command, this method is called.
     */
    public void run() {
        Output output = new Output();
        if (this.expertMode) output.askActiveRules();
        if (this.path != null) {
            if (title != null)
                output.startAnalysis(this.path, title);
            else if(generateReport)
                output.startAnalysis(this.path, true);
            else
                output.startAnalysis(this.path, false);

        }
    }
}


