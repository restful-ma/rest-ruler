# Rule
A trailing forward slash (/) should not be included in URIs
## Category
URIs
## Importance, severity, difficulty
* medium
* error severity
* easy difficulty
## Quality Attribute
* Maintainability

## Rule Description
As the last character within a URI’s path, a forward slash (/) adds no semantic value
and may cause confusion. REST APIs should not expect a trailing slash and should not
include them in the links that they provide to clients.[1].
## Implemented
* N
## Implementation Details
This is a rule that is examined statically. 
### What is checked:

### What is not checked:

### Future work

## Source
[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/