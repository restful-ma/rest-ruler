# Must use normalized paths without empty path segments

## Category

HTTP

## Importance, severity, difficulty

* Importance: medium
* Severity: error
* Difficulty to implement the rule: easy

## Quality Attribute

* Compatibility
* Maintainability

## Rule Description

Description from Zalando [1].

"You must not specify paths with duplicate or trailing slashes, e.g. /customers//addresses or /customers/. As a consequence, you must also not specify or use path variables with empty string values."

## Implemented

* Y

## Implementation Details

### What is checked

* Checks every path in the OpenAPI spec
* Validates that there are no empty path segments in each URI
* Provides clear error messages if an empty path segment is detected

### What is not checked

* Presence of more than one empty path segments in a URI (should occur very very rarely anyway)
* Presence of multiple slashes chained together (e.g., //// etc.)

### Future work

* --

## Source

[1] https://opensource.zalando.com/restful-api-guidelines/#136