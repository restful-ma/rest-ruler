package cli.rule.constants;

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
    public static final String FILE_EXTENSION = "File extensions should not be included in URIs";
    public static final String CONTENT_TYPE = "Content-Type must be used";
    public static final String TUNNELING = "Possibly wrong HTTP request type used";
    public static final String VERBPHRASE = " A verb or verb phrase should be used for controller names";
    public static final String TRAILINGSLASH = "A trailing forward slash '/' was found in the URI";
    public static final String REQUESTTYPE = "Type of the request does not match with the description of the request";
    public static final String REQUESTTYPETUNNELINGGET = "This request is performing more than one action. GET request should not be used to tunnel other request";
    public static final String REQUESTTYPETUNNELINGPOST = "This request is performing more than one action. POST request should not be used to tunnel other request";
    public static final String CAMELCASE = "Multi-word URI segment does not follow camelCase convention";
    public static final String HTTP_STATUS_CODE_NOT_OFFICIAL = "HTTP status code %d is not an official HTTP status code";
    public static final String HTTP_STATUS_CODE_NOT_NUMERIC = "HTTP status code '%s' is not a numeric value";
    public static final String EMPTYPATHSEGMENT = "Path contains empty segments (//) which violates the path normalization rule.";
    public static final String METAINFO = "Make sure to include title, version, description, name, URL, and email in the information field of the API";


    private ErrorMessage() {
        throw new IllegalStateException("Utility class");
    }
}
