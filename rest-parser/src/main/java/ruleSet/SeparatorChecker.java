package ruleSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RULE: Forward slash separator (/) must be used to indicate a hierarchical relationship
 */
public class SeparatorChecker {
    private static char[] separators = { '-', '.', ':', ';', ',', '\\','#', '/'};
    //TODO:
    //check expected pattern
    //check for other possible separators such as '-', '.', ':', ';', ',', '\', '#'
    //and count their appearances
    // at a threshhold: generate antipattern
    //http://regexr.com/?37i6s

    /**
     * checks a given path for potential rule violations
     * @param urlList
     * @return
     */
    public static boolean checkSeparator(List<String> urlList){
        //expected Pattern
        Pattern expectedPattern = Pattern.compile("^(\\/((\\{\\d+\\})|[-a-zA-Z0-9@:%_\\+.~#?&=]+))+$");

        for (String url: urlList) {

            Matcher matcher = expectedPattern.matcher(url);
            if (matcher.find()){
                //check....
            }else {
                //check if a different character is used as a separator
                Map<Character, Long> appearances = countPossibleSeparators(url);
                long ForwardSlashAppearances = appearances.get('/');

                for (char c: appearances.keySet()) {
                    if (appearances.get(c) >= ForwardSlashAppearances && c != '/'){
                        boolean isSeparator = checkForSeparator(c, url);
                        if (isSeparator){
                            //TODO: creat violation object
                        }
                    }
                }
                //return false;
            }
        }
        return false;
    }

    /**
     * checks a String for possible separators
     * @param separator separator candidate
     * @param path path under test
     * @return true or fals deoending on if it is a separator
     */
    private static boolean checkForSeparator(char separator, String path){

        Pattern pattern;

        //escape regex operation characters
        switch (separator){
            case '.':
                pattern = Pattern.compile("^(" + "\\." + "((\\{\\d+\\})|[-a-zA-Z0-9@:%_\\+.~#?&=]+))+$");
                break;
            case '\\':
                pattern = Pattern.compile("^(" + "\\\\" + "((\\{\\d+\\})|[-a-zA-Z0-9@:%_\\+.~#?&=]+))+$");
                break;
            default:
                pattern = Pattern.compile("^(" + separator + "((\\{\\d+\\})|[-a-zA-Z0-9@:%_\\+.~#?&=]+))+$");
        }

        Matcher matcher = pattern.matcher(path);

        return matcher.find();
    }

    /**
     * counts all appearances of possible separator characters
     * @param url
     * @return
     */
    private static Map<Character, Long> countPossibleSeparators(String url){

        HashMap<Character, Long> appearances = new HashMap<>();
        //count appearance
        for (int i=0; i< separators.length; i++) {
            char separator = separators[i];
            long num = url.chars().filter(ch -> ch == separator).count();
            appearances.put(separator,num);
        }
        return appearances;
    }
}
