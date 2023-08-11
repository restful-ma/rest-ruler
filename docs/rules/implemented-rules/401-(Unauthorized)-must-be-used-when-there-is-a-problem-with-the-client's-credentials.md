# 401 ("Unauthorized") must be used when there is a problem with the client's credentials
## Category
URIs

## Importance, severity, difficulty
* Importance: high
* Severity: critical
* Difficulty to implement the rule: easy

## Quality Attribute
* Usability
* Compatibility
* Maintainability

## Rule Description
Description from Massé [1].

"A 401 error response indicates that the client tried to operate on a protected resource without providing the proper authorization. It may have provided the wrong credentials or none at all."

## Implemented
* Y

## Implementation Details (Issue #31)
### What is checked
#### Static analysis
* When the security is globally defined all operations for the path need the 401 response
* When the security is locally defined all local operations for the path need the 401 response

#### Dynamic analysis
For GET operations that statically had no violation and no 401 response defined, a request is made with manipulated pw. If no 401 is received as a response, there is a rule violation.

### What is not checked
* POST, PUT, DELETE, ... are not checked *<sup>1</sup>
* If a wrong status code (not the 401 response) is used even though an auth is required *<sup>2</sup>

*<sup>1</sup> Problems: e.g. POST request does not have an auth --> resources are updated

*<sup>2</sup> Implementation ideas: Use the response messages and train an ai if the message contains something like: unauthorized, wrong auth, not auth, ...

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
