package rest.studentproject.utility;

import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.ActiveRules;
import rest.studentproject.rule.IRestRule;

import java.util.*;

/**
 * Class in which all outputs are made for the cli
 */
public class Output {

    /**
     * Method for the expert mode. User will be asked to enable or disable each rule. The input will be saved in the
     * config file.
     */
    public void askActiveRules() {
        Scanner scanner = new Scanner(System.in);

        ActiveRules activeRules = new ActiveRules();
        List<IRestRule> activeRuleList = activeRules.getAllRuleObjects();

        System.out.println("\n------------------EXPERT MODE------------------");
        System.out.println("-----------------------------------------------");
        System.out.println(activeRuleList.size() + " " + "rules are currently implemented. To customize the rule " +
                "list" + " " + "start configuration by entering " + "y/yes. To skip the configuration press any key");

        String startConfig = scanner.next().trim();

        if (startConfig.equals("y") || startConfig.equals("yes")) {
            Map<String, String> config = new HashMap<>();
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
                        Objects.requireNonNull(config).put(currentRule.getTitle().replaceAll("\\s+", ""), "true");
                        currentRuleIndex++;
                        break;
                    case "n":
                    case "no":
                        Objects.requireNonNull(config).put(currentRule.getTitle().replaceAll("\\s+", ""), "false");
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
            if (config != null) new Config().addToConfig(config);

        } else System.out.println("Skip configuration");
    }

    /**
     * This method starts the analysis with the given path from the user.
     *
     * @param pathToFile path to the OpenAPI definition to be examined
     */
    public void startAnalysis(String pathToFile) {
        // Example: https://api.apis.guru/v2/specs/aiception.com/1.0.0/swagger.json
        // Very long example (just under 20k lines): https://api.apis.guru/v2/specs/amazonaws.com/autoscaling/2011-01-01/openapi.json
        System.out.println("----------------------------------------------\n");
        System.out.println("Begin with the analysis of the file from: " + pathToFile);

        RestAnalyzer restAnalyzer = new RestAnalyzer(pathToFile);
        restAnalyzer.runAnalyse(new ActiveRules().getAllRuleObjects(), true);
    }
}
