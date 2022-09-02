package rest.studentproject.rule.constants;

public class ImprovementSuggestion {

    // Add constant improvement suggestions here
    public static final String UNDERSCORE = "Use hyphens (-) instead of underscores (_)";
    public static final String LOWERCASE = "Change uppercase letters to lowercase letters";
    public static final String SEPARATOR = "remove any '#' and '?' from the path";
    public static final String HYPHEN = "Use hyphens to improve the readability of the segments";
    public static final String UNAUTHERIZED = "Provide the 401 response in the definition of the path";

    private ImprovementSuggestion() {
        throw new IllegalStateException("Utility class");
    }
}
