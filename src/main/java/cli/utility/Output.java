package cli.utility;

import cli.rule.ActiveRules;
import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.constants.*;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Class in which all outputs are made for the cli
 */
public class Output {
    private static final String UNDERLINE = "----------------------------------------------";
    private final Scanner scanner = new Scanner(System.in);
    private boolean useCamelCase = false;
    private boolean enableLLM = false;

    /**
     * Sets which naming convention rules should be active
     * @param useCamelCase if true, enables camelCase rule and disables kebab-case rules
     */
    public void setNamingConventionRules(boolean useCamelCase) {
        this.useCamelCase = useCamelCase;
    }

    /**
     * Toggles flag to enable the usage of LLMs in some rules
     * @param enableLLM if true, enables the usage of LLMs
     */
    public void setEnableLLM(boolean enableLLM) {
        this.enableLLM = enableLLM;
    }

    /**
     * Method for the expert mode. User will be asked to enable or disable each rule. The input will
     * be saved in the config file.
     */
    public void askActiveRules() {
        ActiveRules activeRules = new ActiveRules();
        List<IRestRule> activeRuleList = activeRules.getAllRuleObjects();

        System.out.println("\n------------------EXPERT MODE------------------");
        System.out.println(UNDERLINE);
        System.out.println(activeRuleList.size()
                + " rules are currently implemented. To customize the rule list, start configuration by entering y/yes. Press any other key to skip the configuration.");

        String startConfig = this.scanner.next().trim();

        if (startConfig.equals("y") || startConfig.equals("yes")) {
            Map<String, String> config = new HashMap<>();
            int currentRuleIndex = 1;

            System.out.println("\n---------------------INFO---------------------");
            System.out.println(UNDERLINE);
            System.out.println(
                    "For every rule, enable it with y/yes and disable it with n/no. For further information on the rule, press i/info. To cancel the configuration and discard the input, press q/quit. To skip the rest of the rules without discarding the input, press s/skip.");
            System.out.println("\n-----------------SELECT RULES-----------------");
            System.out.println(UNDERLINE);

            while (currentRuleIndex <= activeRuleList.size()) {
                IRestRule currentRule = activeRuleList.get(currentRuleIndex - 1);

                System.out.println("--> Enable Rule " + currentRuleIndex + " of "
                        + activeRuleList.size() + ": " + currentRule.getTitle() + " [y/n]");

                String userRuleInput = this.scanner.next().trim().toLowerCase();
                switch (userRuleInput) {
                    case "y":
                    case "yes":
                        Objects.requireNonNull(config)
                                .put(currentRule.getTitle().replaceAll("\\s+", ""), "true");
                        currentRuleIndex++;
                        break;
                    case "n":
                    case "no":
                        Objects.requireNonNull(config)
                                .put(currentRule.getTitle().replaceAll("\\s+", ""), "false");
                        currentRuleIndex++;
                        break;
                    case "i":
                    case "info":
                        System.out.println("Rule: '" + currentRule.getTitle()
                                + ".' is from the category: '" + currentRule.getCategory()
                                + "' with severity type: '" + currentRule.getSeverityType()
                                + "' and has an influence on: '"
                                + currentRule.getRuleSoftwareQualityAttribute()
                                + "'.");
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
            if (config != null)
                new Config().addToConfig(config);

        } else
            System.out.println("Skip configuration");
    }

    /**
     * This method starts the analysis with the given path from the user.
     *
     * @param pathToFile path to the OpenAPI definition to be examined
     * @param generateReport if true, a report will be generated
     */
    public void startAnalysis(String pathToFile, boolean generateReport) {
        if (pathToFile.toLowerCase().startsWith("http") && !checkURL(pathToFile)) {
            System.err.println("The URL was not reachable. Please check the URL and try again.");
            return;
        } else if (!pathToFile.toLowerCase().startsWith("http") && !checkFileLocation(pathToFile)) {
            System.err.println(
                    "The file was not found. Please check the path to the file and try again.");
            return;
        }

        RestAnalyzer restAnalyzer = new RestAnalyzer(pathToFile);
        printStartAnalysis(pathToFile);
        
        // Get rules and apply naming convention
        ActiveRules activeRules = new ActiveRules();
        List<IRestRule> ruleList = activeRules.getAllRuleObjects();
        for (IRestRule rule : ruleList) {
            RuleIdentifier ruleIdentifier = rule.getIdentifier();

            if (ruleIdentifier == RuleIdentifier.REQUEST_TYPE_DESCRIPTION) {
                rule.setEnableLLM(this.enableLLM);
            }
            if (ruleIdentifier == RuleIdentifier.PLURAL_NAME) {
                rule.setEnableLLM(this.enableLLM);
            }

            if (useCamelCase) {
                if (ruleIdentifier == RuleIdentifier.CAMEL_CASE) {
                    rule.setIsActive(true);
                } 
                if (ruleIdentifier == RuleIdentifier.HYPHENS || ruleIdentifier == RuleIdentifier.LOWERCASE) {
                    rule.setIsActive(false);
                }
            } else {
                if (ruleIdentifier == RuleIdentifier.CAMEL_CASE) {
                    rule.setIsActive(false);
                } 
                if (ruleIdentifier == RuleIdentifier.HYPHENS || ruleIdentifier == RuleIdentifier.LOWERCASE) {
                    rule.setIsActive(true);
                }
            }
        }
        
        restAnalyzer.runAnalyse(ruleList, generateReport);
    }

    /**
     * This method starts the analysis with the given path from the user.
     *
     * @param pathToFile path to the OpenAPI definition to be examined
     * @param title title of the report that will be generated
     */
    public void startAnalysis(String pathToFile, String title) {

        if (pathToFile.toLowerCase().startsWith("http") && !checkURL(pathToFile)) {
            System.err.println("The URL was not reachable. Please check the URL and try again.");
            return;
        } else if (!pathToFile.toLowerCase().startsWith("http") && !checkFileLocation(pathToFile)) {
            System.err.println(
                    "The file was not found. Please check the path to the file and try again.");
            return;
        }

        RestAnalyzer restAnalyzer = new RestAnalyzer(pathToFile);
        printStartAnalysis(pathToFile);
        
        // Get rules and apply naming convention
        ActiveRules activeRules = new ActiveRules();
        List<IRestRule> ruleList = activeRules.getAllRuleObjects();
        for (IRestRule rule : ruleList) {
            RuleIdentifier ruleIdentifier = rule.getIdentifier();

            if (ruleIdentifier == RuleIdentifier.REQUEST_TYPE_DESCRIPTION) {
                rule.setEnableLLM(this.enableLLM);
            }
            if (ruleIdentifier == RuleIdentifier.PLURAL_NAME) {
                rule.setEnableLLM(this.enableLLM);
            }

            if (useCamelCase) {
                if (ruleIdentifier == RuleIdentifier.CAMEL_CASE) {
                    rule.setIsActive(true);
                } 
                if (ruleIdentifier == RuleIdentifier.HYPHENS || ruleIdentifier == RuleIdentifier.LOWERCASE) {
                    rule.setIsActive(false);
                }
            } else {
                if (ruleIdentifier == RuleIdentifier.CAMEL_CASE) {
                    rule.setIsActive(false);
                } 
                if (ruleIdentifier == RuleIdentifier.HYPHENS || ruleIdentifier == RuleIdentifier.LOWERCASE) {
                    rule.setIsActive(true);
                }
            }
        }
        
        restAnalyzer.runAnalyse(ruleList, title);
    }

    /**
     * Check if the URL to the openAPI definition is reachable.
     * 
     * @param url URL to the openAPI definition
     * @return <code>true</code> if the URL is reachable, <code>false</code> otherwise
     */
    private boolean checkURL(String url) {
        HttpURLConnection huc;
        try {
            huc = (HttpURLConnection) new URL(url).openConnection();
            huc.setRequestMethod("HEAD");
            int responseCode = huc.getResponseCode();

            return responseCode != 404;
        } catch (Exception e) {
            System.err.println("Error while checking URL: " + e.getMessage());
        }

        return false;
    }

    /**
     * Checks if the given path is a valid path to a file and not a dir.
     * 
     * @param fileLocation path to the file
     * @return <code>true</code> if the path is valid, <code>false</code> otherwise
     */
    private boolean checkFileLocation(String fileLocation) {
        File f = new File(fileLocation);
        return f.exists() && !f.isDirectory();
    }

    public static void progressPercentage(int remain, int total) {
        if (remain > total) {
            throw new IllegalArgumentException();
        }
        int maxBareSize = 10; // 10unit for 100%
        int remainProcent = ((100 * remain) / total) / maxBareSize;
        char defaultChar = ' ';
        String icon = "=";
        String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";
        StringBuilder bareDone = new StringBuilder();
        bareDone.append("[");
        for (int i = 0; i < remainProcent; i++) {
            bareDone.append(icon);
        }
        String bareRemain = bare.substring(remainProcent, bare.length());
        System.out.print("\r" + bareDone + bareRemain + " " + remainProcent * 10 + "%");
        if (remain == total) {
            System.out.print("\n");
        }
    }

    private void printStartAnalysis(final String path) {
        System.out.println("\n----------------START ANALYSIS----------------");
        System.out.println(UNDERLINE);
        System.out.println("\nBegin with the analysis of the file from: " + path + "\n");
        System.out.println(UNDERLINE);
    }
}
