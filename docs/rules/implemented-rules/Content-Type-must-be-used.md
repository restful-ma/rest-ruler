# File extensions should not be included in URIs

## Category

Meta



## Importance, severity, difficulty

* Importance: high

* Severity: critical

* Difficulty to implement the rule: easy



## Quality Attribute



* Compatibility

* Usability



## Rule Description



Description from Massé [1].



The Content-Type header names the type of data found within a request or response message’s body. The value of this header is a specially formatted text string known as a media type. Clients and servers rely on this header’s value to tell them how to process the sequence of bytes in a message’s body.



## Implemented

* Y



## Implementation Details (Issue #9)

Rule is statically checked.



### What is checked

* Check if every response contains a defined content --> the refs in components are also checked (If the ref references to another ref --> violation)
  * 204 (No Content) no violation is given if no content type was given

* Check if post, put and patch requests have defined a content in the request body --> the refs in components are also checked (If the ref references to another ref --> violation)



### What is not checked

* The content type of the parameters is not checked
* Dynamic check



## Source



[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/