package rest.studentproject.rules;

import java.util.Comparator;

public class Violation {
    private int lineViolation;
    private String improvementSuggestion;

    //location name of the violation in API file
    private String keyViolation;
    private String errorMessage;

    private IRestRule rule;

    public Violation(IRestRule rule, int lineViolation, String improvementSuggestion, String keyViolation, String errorMessage) {
        this.rule = rule;
        this.lineViolation = lineViolation;
        this.improvementSuggestion = improvementSuggestion;
        this.keyViolation = keyViolation;
        this.errorMessage = errorMessage;
    }

    public IRestRule getRule() {
        return rule;
    }

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

    public static Comparator<Violation> getComparator() {
        return Comparator.comparing(Violation::getLineViolation).thenComparing(Violation::getKeyViolation).thenComparing(v -> v.getRule().getTitle()).thenComparing(v -> v.getRule().getRuleType());
    }

}
