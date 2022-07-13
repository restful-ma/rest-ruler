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

Text viewer applications (browsers, editors, etc.) often underline URIs to provide a visual cue that they are clickable.
Depending on the application’s font, the underscore (_) character can either get partially obscured or completely hidden
by this underlining. To avoid this confusion, use hyphens (-) instead of underscores (as described in “Rule: Hyphens (-)
should be used to improve the readability of URIs” --> Link to GitHub file follows).

## Implemented

* N

## Implementation Details (Issue #9)

* Static implementation
* Check path if it contains an underscore (_) and give a warning if it violates the rule
* Give the hint to use hyphens (-) instead of underscores (_)

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/