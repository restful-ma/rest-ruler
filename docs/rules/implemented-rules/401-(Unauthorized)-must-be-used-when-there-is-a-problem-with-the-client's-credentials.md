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
* When the security is globally defined all operations for the path need the 401 response
* When the security is locally defined all local operations for the path need the 401 response

### Future work

* --

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
