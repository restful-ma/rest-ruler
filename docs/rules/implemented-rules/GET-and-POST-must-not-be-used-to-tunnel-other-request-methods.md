# GET and POST must not be used to tunnel other request methods

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

"Tunneling refers to any abuse of HTTP that masks or misrepresents a message's intent and undermines the protocol's transparency. A REST API must not compromise its design by misusing HTTP's request methods in an effort to accommodate clients with limited HTTP vocabulary." - Massé [1].

The way we understand this rule by Massé is that *GET* and *POST* requests may only be used for the use cases they are intended for. That means that *GET* requests should only be used to retrieve a representation of a resource ([RFC 2616](https://www.rfc-editor.org/rfc/rfc2616.html)) using the request URI. *GET* requests should not be used to create, modify, delete data, or even be used to start processes. *POST* requests, on the other hand, should only be used to create a new resource. It can also be used to send a request to a controller or any other data-handling process. *POST* requests should not be used to retrieve a representation of a resource, nor used to delete data. If a request does something it is not supposed to do, like deleting data through a *GET* request, we refer to this as tunneling. In this example case, a *GET* request is tunneling a *DELETE* request. 

In our implementation, we considered two sources to possibly give us signs of a tunneled request. First, we consider the path and look if some kind of **CRUD** (**C**reate **R**ead **U**pdate **D**elete) operation is present. If a **CRUD** operation is found in the path segment, the operation is compared to the request type and checked for a match. In case of a mismatch, a violation is raised.
Secondly, we check the description field of the path segment, if specified in the OpenAPI definition. Here, we search for keywords that imply a **CRUD** operation and compare it to the request type. If there is a mismatch, a warning is raised.
We would like to note that this implementation does not capture all possible violation cases. For example, this implementation does not do any dynamic analysis of the path definitions. As a result, it is not checked if the server that receives the request, handles the request as specified in its use cases. Therefore, there is no guarantee the description of a path with a request type matches what happens on the server side.

## Implemented

* Y

## Implementation Details

### What is checked:

* only checks paths that have a CRUD rule violation
* checks if paths that contain a CRUD rule violation contain a HTTP request operation type and then see if it differs from the set HTTP request operation type.

### What is not checked:

* Server-side handling of the request. it is possible server could for example delete a resource with a GET request.
* Any path that doesn't contain a CRUD rule violation

### Future work

* --

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/