package rest.studentproject.rule.constants;

public class ErrorMessage {

    public static final String UNDERSCORE = "Underscores (_) should not be used in URIs";
    public static final String LOWERCASE = "Lowercase letters should be preferred in URI paths";
    public static final String SEPARATOR = "A forward slash '/' has to be used as a separator";
    public static final String HYPHEN = "Hyphens (-) should be used to improve the readability of URIs";
    public static final String CRUD = "CRUD function names should not be used in URIs";

    private ErrorMessage() {
        throw new IllegalStateException("Utility class");
    }
}