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

## Implemented

* Y

## Implementation Details (Issue #19)

### What is checked

* Currently, static implementation only
* Checks every path (server paths included) if it contains CRUD operation keywords and returns a list of violations
* Individual segments are checked if they have a crud operation as substring (target includes get) --> there is no violation
* Keywords that are currently checked: "get", "post", "delete", "put", "create", "read", "update", "patch", "insert", "select", "fetch", "purge", "retrieve", "add"

### What is not checked

* The parameters in curly brackets are excluded from the path and are therefore currently not checked in a dynamic fashion
* If more than these previously defined words are to be considered CRUD violations, perform the following steps:
   1. Add the appropriate words to the attribute `CRUD_OPERATIONS` in the `RESTRuler\cli\src\main\java\cli\rule\rules\CRUDRule.java` 
   2. Mine the words that have the appropriate words as substring from `RESTRuler\cli\src\main\java\cli\docs\wordninja_words.txt` and add them to the list `RESTRuler\cli\src\main\java\cli\docs\CRUD_words.txt` (Exclude similar words to still detect the violation; e.g. "gets" from "get")

### Future work

* Dynamic analysis will check the parameter input if it contains CRUD operation keywords

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
