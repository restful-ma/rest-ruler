package rest.studentproject.rule;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.rule.rules.HyphensRule;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utility {

    private Utility() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean getPathSegmentMatch(String word, String filePath) {
        boolean isWordInDictionary = false;
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.useDelimiter("\\Z").next().matches(word)) isWordInDictionary = true;
        } catch (Exception e) {
            Logger logger = Logger.getLogger(HyphensRule.class.getName());
            logger.log(Level.SEVERE, "Error on checking if a word is contained in a dictionary {e}", e);
        }
        return isWordInDictionary;
    }

    public static List<String> getServer(){
        return null;
    }

    public static OpenAPI getOpenAPI(String path) {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(path, null, null);
        return swaggerParseResult.getOpenAPI();
    }
}
