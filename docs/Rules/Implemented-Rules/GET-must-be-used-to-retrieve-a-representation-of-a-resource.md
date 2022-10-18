# Rule
GET must be used to retrieve a representation of a resource
## Category
HTTP
## Importance, severity, difficulty
* High importance
* Critical severity
* difficult
## Quality Attribute 
* Maintainability
* Usability
* Compatibility
* Functional Suitability
## Rule Description
A REST API client uses the GET method in a request message to retrieve the state of a resource, in some representational form. A client’s GET request message may contain
headers but no body.[1].
## Implemented
* Y
## Implementation Details
This is a rule that is examined statically. 
### What is checked:
* GET Request contains a request body
* GET Request has a valid response: HTTP-200 and/or default
* GET Request contains a valid and complete response object definition for either HTTP-200 or default

### What is not checked:
* API holds to the definition and does in fact return the specified object
* A different HTTP Request type is used to retrieve a representation of a resource

## Source
[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/