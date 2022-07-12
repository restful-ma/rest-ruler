# 401 ("Unauthorized") must be used when there is a problem with the client's credentials
## Category
HTTP
## Importance, severity, difficulty
* High importance
* Critical severity
* Easily estimated difficulty to develop rule
## Quality Attribute
* Usability (8)
* Compatibility (8)
* Maintainability (7)
* Functional Suitability (1)
## Rule Description
A 401 error response indicates that the client tried to operate on a protected resource without providing the proper authorization. It may have provided the wrong credentials or none at all [1].
## Implemented
* N
## Implementation Details
This is a rule that is examined statically. 

## Source
[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/