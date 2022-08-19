package rest.studentproject;

import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Help.Ansi;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.IRestRule;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.jansi.AnsiConsole;

@Command(name = "rest-parser", description = "...", mixinStandardHelpOptions = true)
public class RestParserCommand implements Runnable {
    public static final String ANSI_CLS = "\u001b[2J";
    public static final String ANSI_HOME = "\u001b[H";
    public static final String ANSI_BOLD = "\u001b[1m";
    public static final String ANSI_AT55 = "\u001b[10;10H";
    public static final String ANSI_REVERSEON = "\u001b[7m";
    public static final String ANSI_NORMAL = "\u001b[0m";
    public static final String ANSI_WHITEONBLUE = "\u001b[37;44m";

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Option(names = {"-e", "--expertMode"}, description = "Enable if you want to select the rules.")
    boolean expertMode;
    @Option(names = {"-r", "--runAnalyse"}, description = "Run the rest analysis. Required: Path to openapi " +
            "definition (2.0 or higher; json or yaml)")
    private String path;

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        PicocliRunner.run(RestParserCommand.class, args);
        AnsiConsole.systemUninstall();
    }

    public void run() {
        if (expertMode) askActiveRules();
        if (path != null) {

            RestAnalyzer restAnalyzer = null;

            try {
                //https://api.apis.guru/v2/specs/aiception.com/1.0.0/swagger.json
                // Handle when no rule is active
                System.out.println("----------------------------------------------\n");
                System.out.println("Begin with the analysis of the file from: " + this.path);
                restAnalyzer = new RestAnalyzer(this.path);
            } catch (IOException e) {
                logger.severe("Exception when reading the URI: " + e.getMessage());
            }
            restAnalyzer.runAnalyse(new ActiveRules().getActiveRules(), true);
        }
    }

    private void askActiveRules() {
        Scanner scanner = new Scanner(System.in);
        ActiveRules activeRules = new ActiveRules();
        List<IRestRule> activeRuleList = activeRules.getActiveRules();
        System.out.println("\n-----------------EXPERT MODE-----------------");
        System.out.println("-----------------------------------------------");
        System.out.println(activeRuleList.size() + " " + "rules are currently implemented. To customize the rule " +
                "list" + " " + "start configuration by entering " + "y/yes. To skip the configuration press any key");
        String startConfig = scanner.next().trim();
        if (startConfig.equals("y") || startConfig.equals("yes")) {
            Map<String, Boolean> config = new HashMap<>();
            int currentRuleIndex = 1;
            System.out.println("\n---------------------INFO---------------------");
            System.out.println("----------------------------------------------");
            System.out.println("For every rule enable it with y/yes and disable it with n/no. For further " +
                    "information" + " to the rule press" + " i/info. To cancel the configuration and discard the " +
                    "input press q/quit. To " + "skip the rest of the " + "rules without discarding the input press " + "s/skip.");
            System.out.println("\n-----------------SELECT RULES-----------------");
            System.out.println("----------------------------------------------");
            while (currentRuleIndex <= activeRuleList.size()) {
                IRestRule currentRule = activeRuleList.get(currentRuleIndex - 1);
                System.out.println("--> Enable Rule " + currentRuleIndex + " of " + activeRuleList.size() + ": " + currentRule.getTitle() + " [y/n]");
                String userRuleInput = scanner.next().trim().toLowerCase();
                switch (userRuleInput) {
                    case "y":
                    case "yes":
                        config.put(currentRule.getTitle().replaceAll("\\s+", ""), true);
                        currentRuleIndex++;
                        break;
                    case "n":
                    case "no":
                        config.put(currentRule.getTitle().replaceAll("\\s+", ""), false);
                        currentRuleIndex++;
                        break;
                    case "i":
                    case "info":
                        System.out.println("Rule: '" + currentRule.getTitle() + ".' is from the category: '" + currentRule.getCategory() + "' with severity type: '" + currentRule.getSeverityType() + "' and has an influence on: '" + currentRule.getRuleSoftwareQualityAttribute() + "'");
                        break;
                    case "s":
                    case "skip":
                        System.out.println("Rest of the rules are skipped. The input is saved.");
                        currentRuleIndex = activeRuleList.size() + 1;
                        break;
                    case "q":
                    case "quit":
                    default:
                        System.out.println("Configuration cancelled without saving.");
                        config = null;
                        currentRuleIndex = activeRuleList.size() + 1;
                }
            }
            System.out.println("Finished configuration.");
            activeRules.setActiveRules(config);
        } else System.out.println("Skip configuration");
    }
}
