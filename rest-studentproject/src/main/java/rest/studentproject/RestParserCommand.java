package rest.studentproject;

import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.report.Report;

@Command(name = "rest-parser", description = "...",
        mixinStandardHelpOptions = true)
public class RestParserCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(RestParserCommand.class, args);
        RestAnalyzer restAnalyzer = new RestAnalyzer();
        Report report = new Report();
        report = restAnalyzer.runAnalyse("https://petstore3.swagger.io/api/v3/openapi.json");
        System.out.println(report.getDescription());



    }

    public void run() {
        // business logic here
        
        if (verbose) {
            System.out.println("TODO");
        }
    }
}
