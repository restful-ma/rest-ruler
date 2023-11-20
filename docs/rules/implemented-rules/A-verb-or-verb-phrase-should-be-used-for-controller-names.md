# A verb or verb phrase should be used for controller names

## Category

URIs

## Importance, severity, difficulty

* Importance: medium
* Severity: error
* Difficulty to implement the rule: difficult

## Quality Attribute

* Usability
* Maintainability

## Rule Description

Description from Massé [1].

"Like a computer program's function, a URI identifying a controller resource should be named to indicate its action."

## Implemented

* Y

## Implementation Details (Issue #39)

### What is checked

* Split every path in path segments and analyze the last one. If the last path segment contains a verb as first word, and the request is not of type GET or POST then the rule is violated. 
* The rule requires to use a verb to indicate controller actions. A controller can only be of type GET or POST.

### What is not checked

* The semantic of rule is not checked, only the syntax following the defined schema.
  
### Future work

* Use the description of each path to gather more information and to perform a better decision on suggestion to the user if the last path segment could be used as controller. For example, if the last path segment is a noun, then the rule could suggest using a verb or verb phrase to indicate the action.

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
[2] https://restapilinks.com/controller/#:~:text=Like%20a%20traditional%20web%20application%27s,%2C%20also%20known%20as%20CRUD).&text=A%20controller%20resource%20name%20is%20a%20verb%20instead%20of%20a%20noun.
