package ruleSet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * RULE: Forward slash separator (/) must be used to indicate a hierarchical relationship
 */
public class SeparatorChecker {
    private static char[] separators = { '-', '.', ':', ';', ',', '\\','#'};
    //TODO:
    //check expected pattern
    //check for other possible separators such as '-', '.', ':', ';', ',', '\', '#'
    //and count their appearances
    // at a threshhold: generate antipattern
    //http://regexr.com/?37i6s
    public static boolean checkSeparator(List<String> uriList){
        //expected Pattern
        Pattern expectedPattern = Pattern.compile("(.* /)*");

        for (String url: uriList) {
            try {
                URL urlObject = new URL(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            //count appearance
            for (int i=0; i< separators.length; i++) {
                char separator = separators[i];
                long num = url.chars().filter(ch -> ch == separator).count();
            }

        }
        return false;
    }
}
