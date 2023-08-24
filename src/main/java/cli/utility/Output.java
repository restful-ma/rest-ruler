package cli.utility;

import cli.rule.ActiveRules;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Utility;
import cli.rule.constants.SecuritySchema;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.EnumUtils;

import static java.util.Map.entry;

/**
 * Class in which all outputs are made for the cli
 */
public class Output {
    private static final String UNDERLINE = "----------------------------------------------";
    private static final Map<SecuritySchema, String> secToNumbAuthMapping =
            Map.ofEntries(entry(SecuritySchema.APIKEY, "1"), entry(SecuritySchema.BASIC, "2"),
                    entry(SecuritySchema.BEARER, "3"));
    private static final Map<String, SecuritySchema> numbToSecAuthMapping =
            Map.ofEntries(entry("1", SecuritySchema.APIKEY), entry("2", SecuritySchema.BASIC),
                    entry("3", SecuritySchema.BEARER));
    private static final String YES_NO = "[yes/NO]";
    private String pathToFile = "";
    private final Scanner scanner = new Scanner(System.in);
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private String choice;

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
                                + "'. The rule will be checked: " + currentRule.getRuleType());
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

        this.pathToFile = pathToFile;
        RestAnalyzer restAnalyzer = new RestAnalyzer(pathToFile);

        checkServer();

        restAnalyzer.runAnalyse(new ActiveRules().getAllRuleObjects(), generateReport);
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

        this.pathToFile = pathToFile;
        RestAnalyzer restAnalyzer = new RestAnalyzer(pathToFile);

        checkServer();

        restAnalyzer.runAnalyse(new ActiveRules().getAllRuleObjects(), title);
    }

    private void checkServer() {
        boolean checkDynamicAnalysis = checkDynamicAnalysis();
        RestAnalyzer.dynamicAnalysis = checkDynamicAnalysis;
        if (checkDynamicAnalysis) {
            OpenAPI openAPI = Utility.getOpenAPI(pathToFile);
            boolean foundRunningServer = false;
            for (Server server : openAPI.getServers()) {
                String url = server.getUrl();
                System.out.println("Ping server: " + server.getUrl());
                if (!pingURL(url)) {
                    System.out.println("Server is not responding: " + url);
                    System.out.println(
                            "Make sure the server is running or delete it from the openAPI definition.");
                } else
                    foundRunningServer = true;
            }
            if (!foundRunningServer) {
                System.out.println("--> Skip dynamic analysis.");
                RestAnalyzer.dynamicAnalysis = false;
            }
            if (RestAnalyzer.dynamicAnalysis) {
                RestAnalyzer.securitySchemas = askAuth(openAPI);
            }

        }
        System.out.println("----------------------------------------------\n");
        System.out.println("Begin with the analysis of the file from: " + pathToFile);
        System.out.println("\n----------------------------------------------");
    }

    /**
     * Asks the user if he also wants to have a dynamic analysis
     *
     * @return true for dynamic analysis; false only static analysis
     */
    public boolean checkDynamicAnalysis() {
        System.out.println("\n-----------------INFO ANALYSIS----------------");
        System.out.println("-----------------------------------------------\n");
        System.out.println(
                "RESTRuler always performs static analysis. For additional dynamic analysis, credentials for authenticating with the API need to be provided. These are not stored unless you allow it. No changes will be made to resources nor are they saved.");
        System.out.println(
                "If you want to perform dynamic analysis, enter yes or y. Otherwise, enter any other key.");
        System.out.println(YES_NO);

        String dynamicAnalysis = this.scanner.nextLine();

        return dynamicAnalysis.equals("y") || dynamicAnalysis.equals("yes");
    }

    /**
     * Asks the user for authentication --> needed for dynamic analysis
     * <p>
     * Currently implemented auth schemas: bearer, api key and basic authentication
     *
     * @param openAPI the openAPI definition that will be checked --> Needed the get the defined sec
     *        schemas
     * @return a map with the security schemas and the passwords
     */
    public Map<SecuritySchema, String> askAuth(OpenAPI openAPI) {
        // TODO: Delete all auth
        // TODO: Check syntax of auth input (also not empty)
        // TODO: Wrong auth input
        EnumMap<SecuritySchema, String> secTokens = new EnumMap<>(SecuritySchema.class);

        System.out.println("\n----------------AUTHENTICATION----------------");
        System.out.println(UNDERLINE);

        Map<String, SecurityScheme> secSchemas = openAPI.getComponents().getSecuritySchemes();
        Set<String> secSchemaNames = new HashSet<>();
        for (SecurityScheme secSchema : secSchemas.values())
            secSchemaNames.add(secSchema.getType().toString());

        System.out.println(secSchemaNames);

        boolean secDefined = secSchemaNames != null && !secSchemaNames.isEmpty();

        boolean enterMoreSec = true;
        this.choice = "";
        boolean secInProps = false;

        while (enterMoreSec) {
            Map<String, String> secSchemeMap = new HashMap<>();

            if (!secDefined) {
                System.out.println(
                        "\nChoose the authentication method (number) to get/set the credentials or enter "
                                + "any " + "other key to 'save' and skip:");
                System.out.println("1 - API Key");
                System.out.println("2 - Basic Authentication");
                System.out.println("3 - Bearer");

                // Add more
                System.out.println("9 - No authentication needed for requests");
                System.out.println("0 - Redo whole security input and cancel authentication");
                this.choice = this.scanner.next();

            } else {
                System.out.println("Found " + secSchemaNames.size()
                        + " security schemas for given openAPI definition.");
                System.out.println(
                        "\nChoose the authentication method (number) to get/set the credentials or enter"
                                + " " + "any " + "other "
                                + "key to select other security methods:");

                int selectionNumber = 0;
                for (String secSchema : secSchemaNames) {
                    System.out.println(++selectionNumber + " - " + secSchema);
                    secSchemeMap.put(String.valueOf(selectionNumber), secSchema);

                }
                this.choice = this.scanner.next();

                if (secSchemeMap.get(this.choice) == null) {
                    secDefined = false;
                    continue;
                }

                boolean secSchemaSupported = EnumUtils.isValidEnum(SecuritySchema.class,
                        secSchemeMap.get(choice).toUpperCase());

                if (!secSchemaSupported || !secToNumbAuthMapping.containsKey(
                        SecuritySchema.valueOf(secSchemeMap.get(choice).toUpperCase()))) {
                    System.out.println(
                            "We currently do not support this kind of authentication. Please select "
                                    + "another method.");
                    continue;
                }
                this.choice = secToNumbAuthMapping
                        .get(SecuritySchema.valueOf(secSchemeMap.get(choice).toUpperCase()));
            }

            String token = "";

            Properties properties = new Config().getConfig();

            // authTypPathToFile
            String prefixProps = numbToSecAuthMapping.get(this.choice) + this.pathToFile;

            // authTypPathToFileToken
            String keyTokenProps = prefixProps + "token";

            if (properties != null && properties.containsKey(keyTokenProps)) {
                System.out.println(
                        "\nFound credentials for this security method in config. Do you want to use them "
                                + "(yes or y) or enter new credentials (no or n)?");
                System.out.println(YES_NO);
                String input = this.scanner.nextLine();
                if (input.equals("y") || input.equals("yes")) {
                    secInProps = true;
                    secTokens.put(numbToSecAuthMapping.get(this.choice),
                            properties.getProperty(keyTokenProps));
                }
            } else if (properties == null) {
                logger.severe("Path to config file is not set correctly --> Contact devs");
            }

            if (!secInProps) {
                switch (this.choice) {
                    // API Key
                    case "1":
                        // api key
                        System.out.println("Enter api key or type 0 (zero) to skip: ");
                        token = this.scanner.next();
                        if (token.equals("0"))
                            break;
                        secTokens.put(numbToSecAuthMapping.get(this.choice), token);
                        break;
                    // Basic
                    case "2":
                        // username and pw
                        System.out.println(
                                "Enter token consisting of username and password or type 0 (zero) to "
                                        + "skip:" + " ");
                        token = this.scanner.next();
                        if (token.equals("0")) {
                            System.out.println("No token entered; skipping.");
                            break;
                        }
                        secTokens.put(numbToSecAuthMapping.get(this.choice), token);
                        break;
                    // Bearer
                    case "3":
                        // access_token
                        System.out.println("Enter access token or type 0 (zero) to skip: ");
                        token = this.scanner.next();
                        if (token.equals("0"))
                            break;
                        secTokens.put(numbToSecAuthMapping.get(this.choice), token);
                        break;
                    // No auth needed for requests
                    case "9":
                        // Do not save any input and skip auth
                    case "0":
                        System.out.println("Authentication canceled and no credentials are used.");
                        return new HashMap<>();
                    default:
                        System.out.println("Authentication skipped.");
                        enterMoreSec = false;
                }
            }
            System.out.println(
                    "Do you want to enter another security schema? Enter yes or y, enter no or n.");
            System.out.println(YES_NO);

            this.choice = this.scanner.nextLine();

            switch (this.choice) {
                case "y":
                case "yes":
                    break;
                default:
                    enterMoreSec = false;
            }
        }

        if (!secTokens.isEmpty())
            askSafeSec(secInProps, secTokens);

        this.scanner.close();
        return secTokens;
    }

    /**
     * Asks the user if he wants to save the entered security credentials in the config file.
     *
     * @param secInProps true if the security credentials are already in the config file
     * @param secTokens the security credentials
     */
    private void askSafeSec(boolean secInProps, EnumMap<SecuritySchema, String> secTokens) {
        if (!secInProps) {
            System.out.println("\n" + UNDERLINE);
            System.out.println("---------------------Save---------------------");
            System.out.println(
                    "To save the credentials locally for further analyses, enter yes or y. Enter any other key to use the input only for the next analysis.");
            System.out.println(YES_NO + "\n");

            this.choice = this.scanner.nextLine();

            if (this.choice.equals("y") || this.choice.equals("yes")) {
                setSec(secTokens);
            }
        }
    }

    /**
     * Saves the security schema in the config.
     * <p>
     * Structure: type of security schema + path to file + "token" = password
     *
     * @param secTokens map of the security schemas and the passwords to be saved
     */
    private void setSec(Map<SecuritySchema, String> secTokens) {
        for (Map.Entry<SecuritySchema, String> secToken : secTokens.entrySet()) {
            String prefix =
                    secToken.getKey().name() + this.pathToFile.trim().replaceAll("\\s+", "");

            // authTypPathToFileToken
            String token = prefix + "token";

            new Config().addToConfig(Map.of(token, secToken.getValue()));
        }
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

    /**
     * Pings HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the
     * server responded.
     *
     * @param url The HTTP URL to be pinged.
     * @return <code>true</code> if the given HTTP URL is reachable on a HEAD request within the
     *         given timeout, otherwise <code>false</code>.
     */
    private boolean pingURL(String url) {
        if (!url.isEmpty() && !url.equals("")) {
            url = url.replaceFirst("^https", "http"); // Otherwise, an exception may be thrown on
                                                      // invalid SSL
                                                      // certificates.
            return doUrlCall(url);
        }
        return false;
    }

    private boolean doUrlCall(String urlPath) {

        InetAddress address;
        try {
            address = InetAddress.getByName(new URL(urlPath).getHost());
            String ip = address.getHostAddress();
            System.out.println("IP: " + ip);
            return !ip.isEmpty();
        } catch (IOException e) {
            System.err.println("Error while checking URL: " + e.getMessage());
            return false;
        }
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

}
