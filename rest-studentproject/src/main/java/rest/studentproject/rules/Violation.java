package rest.studentproject.rules;

public class Violation {
    private int lineViolation;
    private String improvementSuggestion;

    private String keyViolation;
    private String errorMessage;

    public int getLineViolation() {
        return lineViolation;
    }

    public void setLineViolation(int lineViolation) {
        this.lineViolation = lineViolation;
    }

    public String getImprovementSuggestion() {
        return improvementSuggestion;
    }

    public void setImprovementSuggestion(String improvementSuggestion) {
        this.improvementSuggestion = improvementSuggestion;
    }

    public String getKeyViolation() {
        return keyViolation;
    }

    public void setKeyViolation(String keyViolation) {
        this.keyViolation = keyViolation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
