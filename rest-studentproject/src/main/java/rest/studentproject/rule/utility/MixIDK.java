package rest.studentproject.rule.utility;

import rest.studentproject.rule.rules.HyphensRule;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MixIDK {

    public static boolean getPathSegmentMatch(String word, String filePath) {
        boolean isWordInDictionary = false;
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.useDelimiter("\\Z").next().contains(word)) isWordInDictionary = true;
        } catch (Exception e) {
            Logger logger = Logger.getLogger(HyphensRule.class.getName());
            logger.log(Level.SEVERE, "Error on checking if a word is contained in a dictionary {e}", e);
        }
        return isWordInDictionary;
    }
}
