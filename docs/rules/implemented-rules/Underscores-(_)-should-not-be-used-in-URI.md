# Underscores (_) should not be used in URI

## Category

URIs

## Importance, severity, difficulty

* Importance: high
* Severity: error
* Difficulty to implement the rule: easy

## Quality Attribute

* Maintainability

## Rule Description

Description from Massé [1].

"Text viewer applications (browsers, editors, etc.) often underline URIs to provide a visual cue that they are clickable. Depending on the application's font, the underscore (_) character can either get partially obscured or completely hidden by this underlining. To avoid this confusion, use hyphens (-) instead of underscores (as described in “Rule: Hyphens (-) should be used to improve the readability of URIs” --> Link to GitHub file follows)."

## Implemented

* Y

## Implementation Details (Issue #9)

### What is checked:

* Checks every path (server paths included) if it contains an underscore (_) and returns a list of violations

### What is not checked:

* The parameters in curly brackets are excluded from the path and are therefore currently not checked.

### Future work

* --

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/