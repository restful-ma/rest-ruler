# Camel case should be used to improve the readability of URIs

## Category

URIs

## Importance, severity, difficulty

* Importance: medium
* Severity: error
* Difficulty to implement the rule: medium

## Quality Attribute

* Maintainability
* Compatibility

## Rule Description

This rule is implemented to allow users to have a choice in selecting which naming convention they want the tool to check. If the developers of the API opted to follow the camelCase naming convention for their URIs, they can configure the tool to check for conformance to this convention in each URI. Otherwise, the tool will check for conformance to the usage of hyphens and lowercase letters in the URIs by default.

## Implemented

* Yes

## Implementation Details

* Check if a path segment can be reduced to more than one word. If yes and the path segment does not capitalize every word except the first one, there is a violation
* Give the hint to use camelCase in each URI, to keep the API consistent

### What is checked:

* extract path segments from path using "/" as delimiter
* based on an English dictionary, the extracted path is checked for substrings. If one or more substrings are found, and each word except the first is not capitalized, a violation is reported
* pathSegment can be camelCase, kebab-case, snake_case, all lowercase, all uppercase, or just a mixture of these

### What is not checked:

* presence of invalid delimiters
* check of dynamic parameters

### Future work

* Use a bigger dictionary with a caching possibility to improve performance and precision.