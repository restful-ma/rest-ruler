# Rule
GET and POST must not be used to tunnel other request methods
## Category
HTTP
## Importance, severity, difficulty
* High importance
* Critical severity
* estimated difficult to develop rule
## Quality Attribute
* Maintainability
* Usability
* Compatibility
* Functional Suitability 
## Rule Description
Tunneling refers to any abuse of HTTP that masks or misrepresents a message’s intent
and undermines the protocol’s transparency. A REST API must not compromise its
design by misusing HTTP’s request methods in an effort to accommodate clients with
limited HTTP vocabulary.[1].
## Implemented
* Y
## Implementation Details
This is a rule that is examined statically. 
### What is checked:
* only checks paths that have a CRUD rule violation
* checks if paths that contain a CRUD rule violation contain a HTTP request operation type and then see if it differes from the set HTTP request operation type.
### What is not checked:
* Serverside handling of the request. it is possible server could for example delete a resource with a GET request.
* Any path that doesn't contain a CRUD rule violation
### Future work
* Dynamic Checks
## Source
[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/