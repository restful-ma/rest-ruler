package rest.studentproject.rule.constants;

public class ImprovementSuggestion {

    // Add constant improvement suggestions here
    public static final String UNDERSCORE = "Use hyphens (-) instead of underscores (_)";
    public static final String LOWERCASE = "Change uppercase letters to lowercase letters";
    public static final String SEPARATOR = "remove any '#' and '?' from the path";
    public static final String SEPARATOR_UNKNOWN = "Please check validity of path";
    public static final String HYPHEN = "Use hyphens to improve the readability of the segments";
    public static final String GET_RESOURCE = "Add a response object content definition";
    public static final String GET_RESOURCE_REQUESTBODY = "Remove the request body from the get request";
    public static final String GET_RESOURCE_MISSING_RESPONSE = "Add missing Response for HTTP Code 200";
    public static final String SINGULARDOCUMENTNAME = "Use singular nouns for document names";
    public static final String PLURALNAME = "Use plural nouns for collection or store names";

    private ImprovementSuggestion(){
        throw new IllegalStateException("Utility class");
    }
}
