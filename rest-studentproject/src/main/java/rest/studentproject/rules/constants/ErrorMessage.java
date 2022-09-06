package rest.studentproject.rules.constants;

public class ErrorMessage {

    public static final String UNDERSCORE = "Underscores (_) should not be used in URIs";
    public static final String LOWERCASE = "Lowercase letters should be preferred in URI paths";
    public static final String SEPARATOR = "A forward slash '/' has to be used as a separator";
    public static final String HYPHEN = "Hyphens (-) should be used to improve the readability of URIs";
    public static final String GET_RESOURCE = "GET request is missing a content definition for its response";
    public static final String GET_RESOURCE_REQUESTBODY = "GET requests do not have a request body";
    public static final String GET_RESOURCE_MISSING_RESPONSE = "Missing Response";


    private ErrorMessage(){
        throw new IllegalStateException("Utility class");
    }

}
