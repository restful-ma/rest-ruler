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
* Static check if path ends with a file extension

## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/