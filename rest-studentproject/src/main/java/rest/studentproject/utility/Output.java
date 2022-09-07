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
    private static final Map<String, String> secToNumbAuthMapping = Map.ofEntries(entry("apiKey", "1"), entry("basic"
            , "2"), entry("bearer", "3"));
    private static final Map<String, String> numbToSecAuthMapping = Map.ofEntries(entry("1", "apiKey"), entry("2",
            "basic"), entry("3", "bearer"));
    private static final String yesNo = "[yes/no]";
    private String pathToFile = "";

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
    }

    /**
     * This method starts the analysis with the given path from the user.
     *
     * @param pathToFile path to the OpenAPI definition to be examined
     */
    public void startAnalysis(String pathToFile) {
        this.pathToFile = pathToFile;
        RestAnalyzer restAnalyzer = new RestAnalyzer(pathToFile);
        RestAnalyzer.securitySchemas = askAuth(Utility.getOpenAPI(pathToFile));

        // Example: https://api.apis.guru/v2/specs/aiception.com/1.0.0/swagger.json
        // Very long example (just under 20k lines): https://api.apis.guru/v2/specs/amazonaws.com/autoscaling/2011-01-01/openapi.json
        System.out.println("----------------------------------------------\n");
        System.out.println("Begin with the analysis of the file from: " + pathToFile);
        System.out.println("\n----------------------------------------------");
        restAnalyzer.runAnalyse(new ActiveRules().getAllRuleObjects(), true);
    }

    /**
     * @param openAPI
     * @return empty map --> no auth necessary; null --> no dynamic analysis wanted; map with at least one entry -->
     * entered security
     */
    public Map<String, Map<String, String>> askAuth(OpenAPI openAPI) {
        Scanner scanner = new Scanner(System.in);
        Map<String, String> authSchema = new HashMap<>(Map.of("username", "", "token", ""));
        Map<String, Map<String, String>> secTokens = new HashMap<>();


        System.out.println("\n-----------------INFO ANALYSIS----------------");
        System.out.println("-----------------------------------------------\n");
        System.out.println("Besides the static analysis there is the dynamic analysis for which the credentials for " + "the openapi definition need to be provided. These are not stored unless you allow it. No changes will be" + " made to resources nor are they saved.");
        System.out.println("If you want to do the dynamic analysis, enter yes or y, if you do not want to do it, " +
                "enter any other key.");
        System.out.println(yesNo);

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
                System.out.println("\nChoose the authentication method (number) to get/set the credentials or enter " + "any " + "other key to 'save' and skip:");
                System.out.println("1 - API Key");
                System.out.println("2 - Basic Authentication");
                System.out.println("3 - Bearer");
                System.out.println("4 - OpenID");
                System.out.println("5 - OAuth");
                // Add more
                System.out.println("9 - No authentication needed for requests");
                System.out.println("0 - Redo whole input and cancel authentication");
                choice = scanner.next();

            } else {
                System.out.println("Found " + secSchemas.size() + " security schemas for given openAPI definition.");
                System.out.println("\nChoose the authentication method (number) to get/set the credentials or enter" + " " + "any " + "other " + "key to select other security methods:");

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

                if (!secToNumbAuthMapping.containsKey(secSchemeMap.get(choice))) {
                    System.out.println("We currently do not support this kind of authentication. Please select " +
                            "another method.");
                    continue;
                }
                choice = secToNumbAuthMapping.get(secSchemeMap.get(choice));
            }

            String username = "";
            String token = "";

            Properties properties = new Config().getConfig();
            boolean secInProps = false;

            // authTypPathToFile
            String prefixProps = numbToSecAuthMapping.get(choice) + this.pathToFile;
            // authTypPathToFileUsername
            String keyUsernameProps = prefixProps + "username";
            // authTypPathToFileToken
            String keyTokenProps = prefixProps + "token";

            if (properties.containsKey(keyTokenProps) && properties.containsKey(keyUsernameProps)) {
                System.out.println("\nFound credentials for this security method in config. Do you want to use them " + "(yes or y) or enter new credentials (no or n)?");
                System.out.println(yesNo);
                String input = scanner.next();
                if (input.equals("y") || input.equals("yes")) {
                    secInProps = true;
                    authSchema.put("username", properties.getProperty(keyTokenProps));
                    authSchema.put("token", properties.getProperty(keyUsernameProps));
                    secTokens.put(numbToSecAuthMapping.get(choice), authSchema);
                }
            }

            if (!secInProps) {
                switch (choice) {
                    // API Key
                    case "1":
                        // api key
                        System.out.println("Enter api key: ");
                        token = scanner.next();
                        authSchema.put("username", "");
                        authSchema.put("token", token);
                        secTokens.put(numbToSecAuthMapping.get(choice), authSchema);
                        break;
                    // Basic
                    case "2":
                        // username and pw
                        System.out.println("Enter username: ");
                        username = scanner.next();
                        authSchema.put("username", username);
                        System.out.println("Enter password: ");
                        token = scanner.next();
                        authSchema.put("token", token);
                        secTokens.put(numbToSecAuthMapping.get(choice), authSchema);
                        break;
                    // Bearer
                    case "3":
                        // access_token
                        System.out.println("Enter access token: ");
                        token = scanner.next();
                        authSchema.put("username", "");
                        authSchema.put("token", token);
                        secTokens.put(numbToSecAuthMapping.get(choice), authSchema);
                        break;
                    // OpenID
                    case "4":
                        System.out.println("OpenID currently not implemented");
                        break;
                    // OAuth
                    case "5":
                        System.out.println("OAuth currently not implemented");
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
            }
            System.out.println("Do you want to enter another security schema? Enter yes or y, enter no or n.");
            System.out.println(yesNo);

            choice = scanner.next();

            switch (choice) {
                case "y":
                case "yes":
                    break;
                default:
                    enterMoreSec = false;
            }
        }
        System.out.println("\n" + UNDERLINE);
        System.out.println("Do you want to save the credentials local for further analyses enter yes or y or only " + "use" + " the input only for the next analysis enter any other key.");
        System.out.println(yesNo);

        choice = scanner.next();

        if (choice.equals("y") || choice.equals("yes")) {
            setSec(secTokens);
        }

        scanner.close();
        return secTokens;
    }

    private void setSec(Map<String, Map<String, String>> secTokens) {
        for (Map.Entry<String, Map<String, String>> secToken : secTokens.entrySet()) {
            String prefix = secToken.getKey() + this.pathToFile.trim().replaceAll("\\s+", "");

            // authTypPathToFileUsername
            String username = prefix + "username";
            // authTypPathToFileToken
            String token = prefix + "token";

            new Config().addToConfig(Map.of(username, secToken.getValue().get("username"), token,
                    secToken.getValue().get("token")));
        }
    }

}
