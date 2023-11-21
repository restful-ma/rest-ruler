# A trailing forward slash (/) should not be included in URIs

## Category

URIs

## Importance, severity, difficulty

* medium
* error severity
* easy difficulty
* 
## Quality Attribute

* Maintainability

## Rule Description
As the last character within a URI's path, a forward slash (/) adds no semantic value and may cause confusion. REST APIs should not expect a trailing slash and should not include them in the links that they provide to clients.[1].
The way we understand this rule is that no forward slash (/) is to be included from the end of a path segment in a URI. To enforce this rule, we check each path segment in an OpenAPI definition if it ends with a forward slash (/) and in the case of a trailing forward slash (/), throw a violation.

## Implemented

* Y
  
## Implementation Details

The last character of each URI paths is checked.

### What is checked:

- Checks if a path segement contains a trailing foward slash (/)

### What is not checked:

* --

### Future work

* --

## Source
[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/