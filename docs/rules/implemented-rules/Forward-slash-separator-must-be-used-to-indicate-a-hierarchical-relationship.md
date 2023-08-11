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
"The forward slash (/) character is used in the path portion of the URI to indicate a hierarchical relationship between resources." - Massé [1].
The way we understand the rule written by Massé is 
1. a forward slash (/) is to be used as means to separate words in the path of a URI and 
2. the words separated by the separator have to be ordered hierarchically. 

From the above definition of the URI by RPC 3986 we know that a '?' character defines the beginning of a query and the '\#' character defines the beginning of a fragment. 
These characters are therefore illegal in the path segment, as they start new segments in the URI. 
Our implementation of this rule will as a result immediately flag a path containing either of those two characters as a violation of this rule. 

Another class of violations is the use of a different character as a separator instead of the forward slash (/) defined as the separator by RFC 3986. 
By further analysis we discovered that not only could a different character be used to replace the forward slash (/) (e.g., *".shelter.animals.cat"*) but could also only replace a subset of the forward slashes (/) in the path (e.g. *"/germany/states/baden-wuettemberg/shelter -types.animal-shelter.animals.cat"*).
To capture all those cases we analyze the path and search for potential characters used as a separator with the help of complex regular expressions. 
The characters we look for in our implementation are: '.', ':', ';', ',', '\\', '/', '-', '='. 

There are cases we deem to be violations of this rule but have not been implemented. The first case of a violation we do not cover in our implementation is the case where separators might be included in the values of path variables of the path. As these values cannot be identified through a static analysis of the paths: complex, resource-intensive dynamic analysis would be required. 
The second case we do not cover is hierarchy mismatches between the words used into a path, separated by a separator. E.g *"/countries/germany/states/baden -wuerttemberg"* would be a valid hierarchy while *"/house/resident/apartments"* would be an invalid hierarchy as it would make more sense to have "apartments" before "resident" as a house can have multiple apartments with an apartment having a resident. \\ 

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