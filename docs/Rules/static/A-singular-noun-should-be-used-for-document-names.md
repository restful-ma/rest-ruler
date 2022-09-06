# A singular noun should be used for document names

## Category

URIs

## Importance, severity, difficulty

* Importance: high
* Severity: error
* Difficulty to implement the rule: difficult

## Quality Attribute

* Usability
* Maintainability

## Rule Description

Description from Massé [1].

"A URI representing a document resource should be named with a singular noun or
noun phrase path segment."

## Implemented

* Y

## Implementation Details (Issue #15)

### What is checked

* Currently, static implementation only
* Check every pathSegment which are generated from the split of the path using the "/" as delimiter. If the pathSegment is a
  document name, it should be singular. If it is not singular, it is a violation. 
* The structure of a path is normally as follows: singular/plural/singular.. or plural/singular/plural.. . If we have a path with plural/plural then we have a violation.

### What is not checked

* For each pathSegment is being checked if the word is singular or plural. For this we have two different sources. 
* The first one is a JsonDictionary which contains all singular and plural words populated from previous scanner of different apis.
* The second one is a list of words which are not in the dictionary. For these words we use the OxfordDictionaryApi.
### Future work

* Dynamic analysis will check the parameter input if it is plural or singular

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
