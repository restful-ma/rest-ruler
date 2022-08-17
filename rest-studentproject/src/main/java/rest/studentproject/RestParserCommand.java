package rest.studentproject;

import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import rest.studentproject.analyzer.RestAnalyzer;

import java.net.MalformedURLException;

@Command(name = "rest-parser", description = "...", mixinStandardHelpOptions = true)
public class RestParserCommand implements Runnable {

    @Option(names = {"-r", "--runAnalyse"}, description = "Run the rest analysis. Required: Path to openapi definition (2.0 or higher; json or yaml)")
    boolean verbose;
    @Parameters(index = "0", description = "")
    private String path;

    public static void main(String[] args) {
        PicocliRunner.run(RestParserCommand.class, args);
        System.out.println("Hi!");
    }

    public void run() {
        // business logic here
        if (verbose) {
            RestAnalyzer restAnalyzer = null;
            try {
                //https://api.apis.guru/v2/specs/aiception.com/1.0.0/swagger.json
                System.out.println("Begin with the analysis of the file from: " + this.path);
                restAnalyzer = new RestAnalyzer(this.path);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            restAnalyzer.runAnalyse(new ActiveRules().getActiveRules(), true);


        }
    }
}
