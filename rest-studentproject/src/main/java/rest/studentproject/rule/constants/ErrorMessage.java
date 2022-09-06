package rest.studentproject.rule.constants;

public class ErrorMessage {

    public static final String UNDERSCORE = "Underscores (_) should not be used in URIs";
    public static final String LOWERCASE = "Lowercase letters should be preferred in URI paths";
    public static final String SEPARATOR = "A forward slash '/' has to be used as a separator";
    public static final String HYPHEN = "Hyphens (-) should be used to improve the readability of URIs";
    public static final String GET_RESOURCE = "GET request is missing a content definition for its response";
    public static final String GET_RESOURCE_REQUEST_BODY = "GET requests do not have a request body";
    public static final String GET_RESOURCE_MISSING_RESPONSE = "Missing Response";
    public static final String CRUD = "CRUD function names should not be used in URIs";
    public static final String UNAUTHORIZED =
            "401 (\"Unauthorized\") must be used when there is a problem with the " + "client's credentials";
    public static final String SINGULAR_DOCUMENT_NAME = "A singular noun should be used for document names";
    public static final String PLURAL_NAME = "A plural noun should be used for collection or store names";

    private ErrorMessage() {
        throw new IllegalStateException("Utility class");
    }
}
