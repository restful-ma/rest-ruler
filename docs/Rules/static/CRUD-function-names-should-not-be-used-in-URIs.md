# CRUD function names should not be used in URIs

## Category
URIs

## Importance, severity, difficulty

* Importance: high
* Severity: error
* Difficulty to implement the rule: easy

## Quality Attribute
* Usability
* Maintainability

## Rule Description
Description from Massé [1].

"URIs should not be used to indicate that a CRUD§ function is performed. URIs should
be used to uniquely identify resources [...]" and the "[...] HTTP request methods
should be used to indicate which CRUD function is performed."

"For example, this API interaction design is preferred:
* DELETE /users/1234

The following anti-patterns exemplify what not to do:

* GET /deleteUser?id=1234
* GET /deleteUser/1234
* DELETE /deleteUser/1234
* POST /users/1234/delete
"

## Implemented

* N

## Implementation Details
Will follow soon.

### What is checked

* Static
* Notes, difficulties

### What is not checked

### Future work

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
