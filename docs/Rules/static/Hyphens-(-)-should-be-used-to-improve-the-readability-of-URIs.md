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

To make your URIs easy for people to scan and interpret, use the hyphen (-) character
to improve the readability of names in long path segments. Anywhere you would use
a space or hyphen in English, you should use a hyphen in a URI.

## Implemented

* TODO

## Implementation Details (Issue #13)

* Static implementation
* Check if a path segment can be reduced to more than one word. If yes and the path segment does not contain (-) hyphens, there is a violation
* Give the hint to use hyphens to separate words in a path segment

### What is checked:
* extract path segments from path using "/" as delimiter
* based on an English dictionary, the extracted path is checked for substrings. If one or more substrings are found, there is a violation of the rule
* pathSegment can be camelCase, kebabCase, snakeCase, all lowercase, all uppercase, or just a mixture of these

### What is not checked:
* 
## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/