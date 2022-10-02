package rest.studentproject.rule;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.json.simple.JSONObject;
import rest.studentproject.rule.constants.RequestType;
import rest.studentproject.rule.constants.SecuritySchema;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static rest.studentproject.oxford.dictionary.api.OxfordConstants.PLURAL;
import static rest.studentproject.oxford.dictionary.api.OxfordConstants.SINGULAR;
import static rest.studentproject.oxford.dictionary.api.OxfordDictionariesApi.checkWordUsingOxfordDictionariesAPI;

public class Utility {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Utility() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean getPathSegmentContained(String word, String filePath) {
        boolean isWordInDictionary = false;
        try (FileReader fileReader = new FileReader(filePath)) {
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNext()) {
                String wordFromDictionary = scanner.next();
                if (word.toLowerCase().contains(wordFromDictionary)) {
                    isWordInDictionary = true;
                    break;
                }
            }
        } catch (Exception e) {
            logger.severe("Error on checking if a word is contained in a dictionary: " + e.getMessage());
        }
        return isWordInDictionary;
    }

    public static boolean getPathSegmentMatch(String word, String filePath) {
        boolean isWordInDictionary = false;
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.useDelimiter("\\Z").next().matches(word)) isWordInDictionary = true;
        } catch (Exception e) {
            logger.severe("Error on checking if a word is contained in a dictionary: " + e.getMessage());
        }
        return isWordInDictionary;
    }

    public static OpenAPI getOpenAPI(String path) {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(path, null, null);
        return swaggerParseResult.getOpenAPI();
    }

    /**
     * Method to determine the switchPathSegment based on the firstPathSegment.
     *
     * @param pathSegments
     * @param switchPathSegment
     * @param firstPathSegment
     * @return
     */
    public static String getSwitchPathSegment(String[] pathSegments, String switchPathSegment,
                                              String firstPathSegment) {
        if (!firstPathSegment.isEmpty()) {
            switchPathSegment = getPluralOrSingularOfWord(firstPathSegment);
        } else if (pathSegments.length > 1) {
            firstPathSegment = pathSegments[1].trim().toLowerCase();
            switchPathSegment = getPluralOrSingularOfWord(firstPathSegment);
        }
        return switchPathSegment;
    }

    /**
     * Method to get the plural or singular form of a word using the OxfordDictionaryAPI.
     *
     * @param firstPathSegment
     * @return
     */
    public static String getPluralOrSingularOfWord(String firstPathSegment) {
        String switchPathSegment;
        boolean firstPathSegmentForm = checkWordUsingOxfordDictionariesAPI(firstPathSegment);
        switchPathSegment = getControlPathSegmentForRule(firstPathSegmentForm);
        return switchPathSegment;
    }

    /**
     * Method to change the switchPathSegment from plural to singular a vice versa.
     *
     * @param equals
     * @return
     */
    public static String getControlPathSegmentForRule(boolean equals) {
        if (equals) {
            return SINGULAR;
        } else {
            return PLURAL;
        }
    }

    public static HttpURLConnection createHttpConnection(URL url, RequestType requestMethod) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(requestMethod.name());
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception occurred when creating the http url connection: {e}", e);
        }
        return con;
    }

    public static URL getURL(SecuritySchema securitySchema, String pw, String serverURL, String path) {
        URL url = null;
        try {
            if (securitySchema == SecuritySchema.APIKEY) {
                url = new URL(serverURL + path + "?api_key=" + pw.substring(0, pw.length() - 1));
            } else url = new URL(serverURL + path);
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Exception on trying to request: {0}", e.getMessage());
        }
        return url;
    }
}
