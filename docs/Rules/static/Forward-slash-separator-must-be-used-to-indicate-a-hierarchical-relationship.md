# Rule
Forward slash separator (/) must be used to indicate a hierarchical relationship
## Category
URIs
## Importance, severity, difficulty
* High importance
* Critical severity
* Easily estimated difficulty to develop rule
## Quality Attribute
* Maintainability 
## Rule Description
The forward slash (/) character is used in the path portion of the URI to indicate a hierarchical relationship between resources.[1].
## Implemented
* Y
## Implementation Details
This is a rule that is examined statically. 
### What is checked:
* "/" is used as a separator
* if other symbols are used as the separator instead of '/' such as '.', ',', ';', ':', '\', '#', '-', e.g. 'this:is:a:new:path'
* if other symbols are used partly as a separator e.g. '/this/is:a:new/path' or ':this:is/another:path'
* path variable names (surrounded in curly brakets '{}') do not contain the separator

### What is not checked:
* path variable values do not contain the separator "/" to runtime
* if words left and right of the separator are hierachically related e.g. building/skyscrapers

## Source
[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/