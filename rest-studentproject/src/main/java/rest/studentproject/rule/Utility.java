package rest.studentproject.rule;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
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
        try (FileReader fileReader = new FileReader(filePath);) {
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

    /**
     * Method to determine the switchPathSegment based on the firstPathSegment.
     * @param pathSegments
     * @param switchPathSegment
     * @param firstPathSegment
     * @return
     */
    public static String getSwitchPathSegment(String[] pathSegments, String switchPathSegment, String firstPathSegment) {
        if(!firstPathSegment.isEmpty()) {
            switchPathSegment = getPluralOrSingularOfWord(firstPathSegment);
        }else if(pathSegments.length > 1){
            firstPathSegment = pathSegments[1].trim().toLowerCase();
            switchPathSegment = getPluralOrSingularOfWord(firstPathSegment);
        }
        return switchPathSegment;
    }

    /**
     * Method to get the plural or singular form of a word using the OxfordDictionaryAPI.
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
}
