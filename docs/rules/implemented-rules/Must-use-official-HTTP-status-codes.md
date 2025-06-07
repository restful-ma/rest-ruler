# Must use official HTTP status codes

## Category

HTTP

## Importance, severity, difficulty

* Importance: high
* Severity: error
* Difficulty to implement the rule: easy

## Quality Attribute

* Compatibility
* Maintainability
* Usability

## Rule Description

Description from Zalando [1].

"You must only use official HTTP status codes consistently with their intended semantics. Official HTTP status codes are defined via RFC standards and registered in the IANA Status Code Registry."

## Implemented

* Y

## Implementation Details

### What is checked

* Checks every response status code in the OpenAPI specification against the official IANA registry of HTTP status codes [2]
* Validates that status codes are numeric (except for 'default' responses which are valid)
* Ensures all status codes used are official codes from the IANA registry
* Provides clear error messages indicating which status codes are not official or not numeric

### What is not checked

* The semantic meaning of the status codes (i.e., whether they are used appropriately for their intended purpose)
* Custom status codes that might be valid for specific use cases but not in the IANA registry

### Future work

* Add validation of status code usage against their semantic meaning
* Consider adding support for custom status codes with proper documentation
* Add validation of status code consistency across similar operations

## Source

[1] https://opensource.zalando.com/restful-api-guidelines/#243  
[2] https://www.iana.org/assignments/http-status-codes/http-status-codes.xhtml 