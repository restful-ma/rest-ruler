package rest.studentproject.utility;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.ActiveRules;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Utility;

import java.util.*;

import static java.util.Map.entry;

/**
 * Class in which all outputs are made for the cli
 */
public class Output {
    private static final String UNDERLINE = "----------------------------------------------";
    private static final Map<String, String> authMapping = Map.ofEntries(entry("apiKey", "1"), entry("basic", "2"),
            entry("bearer", "3"));

    /**
     * Method for the expert mode. User will be asked to enable or disable each rule. The input will be saved in the
     * config file.
     */
    public void askActiveRules() {
        Scanner scanner = new Scanner(System.in);

        ActiveRules activeRules = new ActiveRules();
        List<IRestRule> activeRuleList = activeRules.getAllRuleObjects();

        System.out.println("\n------------------EXPERT MODE------------------");
        System.out.println(UNDERLINE);
        System.out.println(activeRuleList.size() + " " + "rules are currently implemented. To customize the rule " +
                "list" + " " + "start configuration by entering " + "y/yes. To skip the configuration press any key");

        String startConfig = scanner.next().trim();

        if (startConfig.equals("y") || startConfig.equals("yes")) {
            Map<String, String> config = new HashMap<>();
            int currentRuleIndex = 1;

            System.out.println("\n---------------------INFO---------------------");
            System.out.println(UNDERLINE);
            System.out.println("For every rule enable it with y/yes and disable it with n/no. For further " +
                    "information" + " to the rule press" + " i/info. To cancel the configuration and discard the " +
                    "input press q/quit. To " + "skip the rest of the " + "rules without discarding the input press " + "s/skip.");
            System.out.println("\n-----------------SELECT RULES-----------------");
            System.out.println(UNDERLINE);

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

            new Config().addToConfig(config);
        } else System.out.println("Skip configuration");

        scanner.close();
    }

    /**
     * This method starts the analysis with the given path from the user.
     *
     * @param pathToFile path to the OpenAPI definition to be examined
     */
    public void startAnalysis(String pathToFile) {
        RestAnalyzer restAnalyzer = new RestAnalyzer(pathToFile);
        RestAnalyzer.securitySchemas = askAuth(pathToFile, Utility.getOpenAPI(pathToFile));

        // Example: https://api.apis.guru/v2/specs/aiception.com/1.0.0/swagger.json
        // Very long example (just under 20k lines): https://api.apis.guru/v2/specs/amazonaws.com/autoscaling/2011-01-01/openapi.json
        System.out.println("----------------------------------------------\n");
        System.out.println("Begin with the analysis of the file from: " + pathToFile);
        System.out.println("\n----------------------------------------------");
        restAnalyzer.runAnalyse(new ActiveRules().getAllRuleObjects(), true);
    }

    /**
     * @param pathToFile
     * @param openAPI
     * @return empty map --> no auth necessary; null --> no dynamic analysis wanted; map with at least one entry -->
     * entered security
     */
    public Map<String, String> askAuth(String pathToFile, OpenAPI openAPI) {
        // TODO check if credential saved
        // TODO check if credentials already defined
        Scanner scanner = new Scanner(System.in);
        Map<String, String> secTokens = new HashMap<>();


        System.out.println("\n-----------------INFO ANALYSIS----------------");
        System.out.println("-----------------------------------------------\n");
        System.out.println("Besides the static analysis there is the dynamic analysis for which the credentials for " + "the openapi definition need to be provided. These are not stored unless you allow it. No changes will be" + " made to resources nor are they saved.");
        System.out.println("If you want to do the dynamic analysis, enter yes or y, if you do not want to do it, " +
                "enter any other key.");
        System.out.println("[yes/no]");

        String dynamicAnalysis = scanner.next();

        if (!(dynamicAnalysis.equals("y") || dynamicAnalysis.equals("yes"))) return null;

        System.out.println("\n----------------AUTHENTICATION----------------");
        System.out.println(UNDERLINE);

        Map<String, SecurityScheme> secSchemas = openAPI.getComponents().getSecuritySchemes();

        boolean secDefined = secSchemas != null && !secSchemas.isEmpty();


        boolean enterMoreSec = true;
        String choice = "";
        while (enterMoreSec) {
            Map<String, String> secSchemeMap = new HashMap<>();

            if (!secDefined) {
                System.out.println("There is no authentication method found in the openAPI definition.");
                System.out.println("\nChoose the authentication method (number) to set the credentials or enter any " + "other key to 'save' and skip:");
                System.out.println("1 - API Key");
                System.out.println("2 - Basic Authentication");
                System.out.println("3 - Bearer");
                // Add more
                System.out.println("9 - No authentication needed for requests");
                System.out.println("0 - Redo whole input and cancel authentication");
                choice = scanner.next();

            } else {
                System.out.println("Found " + secSchemas.size() + " security schemas for given openAPI definition.");
                System.out.println("\nChoose the authentication method (number) to set the credentials or enter" + " "
                        + "any " + "other " + "key to select other security methods:");

                int selectionNumber = 0;
                for (Map.Entry<String, SecurityScheme> secSchema : secSchemas.entrySet()) {
                    System.out.println(++selectionNumber + " - " + secSchema.getValue().getType());
                    secSchemeMap.put(String.valueOf(selectionNumber), secSchema.getValue().getType().toString());

                }
                choice = scanner.next();

                if (secSchemeMap.get(choice) == null) {
                    secDefined = false;
                    continue;
                }

                if (!authMapping.containsKey(secSchemeMap.get(choice))) {
                    System.out.println("We currently do not support this kind of authentication. Please select " +
                            "another method.");
                    continue;
                }
                choice = authMapping.get(secSchemeMap.get(choice));
            }


            switch (choice) {
                // API Key
                case "1":
                    // username und api key
                    System.out.println("Choice 1");
                    break;
                // Basic
                case "2":
                    System.out.println("Choice 2");
                    break;
                // Bearer
                case "3":
                    System.out.println("Choice 3");
                    break;
                // No auth needed for requests
                case "9":
                    return new HashMap<>();
                // Do not save any input and skip auth
                case "0":
                    System.out.println("Authentication canceled and no credentials are used.");
                    return null;
                default:
                    System.out.println("Authentication skipped.");
                    enterMoreSec = false;
            }
            System.out.println("Do you want to enter another security schema? Enter yes or y, enter no or n.");
            System.out.println("[yes/no]");

            choice = scanner.next();

            switch (choice) {
                case "y":
                case "yes":
                    break;
                default:
                    enterMoreSec = false;
            }
        }
        System.out.println("Do you want to save the credentials local for further analyses enter yes or y or only use" +
                " the input only for the next analysis enter any other key.");
        System.out.println("[yes/no]");

        choice = scanner.next();

        if (choice.equals("y") || choice.equals("yes")) {
            // TODO
            setSec();
        }

        scanner.close();
        return secTokens;
    }

    private void setSec(){

    }
}
