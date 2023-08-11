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
"A REST API client uses the GET method in a request message to retrieve the state of a resource, in some representational form. A client’s GET request message may contain
headers but no body." - Massé [1].
The way we understand this rule by Massé is
1. *GET* requests should never contain a request body and 
2. a *GET* request should always receive some kind of object that represents a resource in the case of an *HTTP 200* response.

To check for this rule in our implementation, we search within each path object in the OpenAPI definition of an API for *GET* requests. For all found *GET* requests, we check if they contain a request body. If they do, our implementation will raise a violation of the rule. If no request body is found, the defined responses in the path object is checked. Here, we look if either an *HTTP 200* or a default response is present. If none are defined, a violation of this rule is raised. Additionally, if definitions of a default and/or *HTTP 200* response exist, they are checked if a representation of a resource is defined. For that, we check if a content type is defined inside the response or if a reference to an object specification exists. For re-usability purposes, OpenAPI allows objects to be defined in the components segment of the specification, so these can be reused for multiple requests and responses. If none of the above are defined, a violation of this rule is raised.
This implementation also does not cover several violation cases. For instance, we do not check if a server returns the specified object from the OpenAPI definition. Additionally, it is not checked by this rule if a different request operator is used to retrieve a representation of a resource. These set violation cases are partly covered by the implementation for tunneling of *GET* and *POST* requests.
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