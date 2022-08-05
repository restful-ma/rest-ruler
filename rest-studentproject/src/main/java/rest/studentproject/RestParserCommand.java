package rest.studentproject;

import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import rest.studentproject.analyzer.RestAnalyzer;

import java.net.MalformedURLException;

@Command(name = "rest-parser", description = "...", mixinStandardHelpOptions = true)
public class RestParserCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws MalformedURLException {
        PicocliRunner.run(RestParserCommand.class, args);
        RestAnalyzer restAnalyzer = new RestAnalyzer("https://api.apis.guru/v2/specs/aiception.com/1.0.0/swagger.json");
        restAnalyzer.runAnalyse(new ActiveRules().getActiveRules());
    }

    public void run() {
        // business logic here

        if (verbose) {
            // TODO: implement
            System.out.println("Hi!");
        }
    }
}
