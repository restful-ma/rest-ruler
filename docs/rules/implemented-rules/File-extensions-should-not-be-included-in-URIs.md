# File extensions should not be included in URIs
## Category
URIs

## Importance, severity, difficulty
* Importance: medium
* Severity: error
* Difficulty to implement the rule: easy

## Quality Attribute

* Maintainability

## Rule Description

Description from Massé [1].

On the Web, the period (.) character is commonly used to separate the file name and extension portions of a URI. A REST API should not include artificial file extensions in URIs to indicate the format of a message’s entity body. Instead, they should rely on the media type, as communicated through the Content-Type header, to determine how to process the body’s content.

## Implemented
* Y

## Implementation Details (Issue #9)

### What is checked
The Path is divided into the individual segments. Then, to check if a segment ends with a dot and a file extension (.file-extension), a list [2] of 838 different file extensions is searched. If one of them matches the end of the segment, a rule violation is returned. This list can be extended very easily if file extensions are missed. The list is located here: `./src/main/java/cli/docs/file_extensions.txt`.

### What is not checked
* The path needs to end with .file-extension. If the path does not end with this, the rule is not violated.
* It is possible that individual file extensions are not recognized because they have not been recorded in the system. To create a rule violation anyway, simply add the non-existing extensions to the list: `./src/main/java/cli/docs/file_extensions.txt`


## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
[2] https://en.wikipedia.org/wiki/List_of_filename_extensions