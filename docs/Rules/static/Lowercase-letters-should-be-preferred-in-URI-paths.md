# Lowercase letters should be preferred in URI paths

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

When convenient, lowercase letters are preferred in URI paths since capital letters can
sometimes cause problems. RFC 3986 defines URIs as case-sensitive except for the
scheme and host components.

## Implemented

* Yes

## Implementation Details (Issue #10)

* Static implementation
* Check path if it contains an uppercase letter, if so give a warning
* Give the hint to use lowercase letters instead of uppercase

### What is checked:
* check if the path contains an uppercase letter
* check if the path is empty or there are no paths

### What is not checked:
* presence of invalid delimiters

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/