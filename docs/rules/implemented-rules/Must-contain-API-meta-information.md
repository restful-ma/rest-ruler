# Must contain API meta information

## Category

META

## Importance, severity, difficulty

* Importance: medium
* Severity: warning
* Difficulty to implement the rule: easy

## Quality Attribute

* Maintainability
* Usability

## Rule Description

Description from Zalando [1].

"API specifications must contain the following OpenAPI meta information to allow for API management:

#/info/title as (unique) identifying, functional descriptive name of the API

#/info/version to distinguish API specifications versions following semantic rules

#/info/description containing a proper description of the API

#/info/contact/{name,url,email} containing the responsible team"

## Implemented

* Y

## Implementation Details

### What is checked

* Checks the info field of an OpenAPI spec
* Checks whether the `title`, `version`, `description`, and `contact/{name, url, email}` fields are populated
* Provides clear error messages if any of the fields are missing/empty

### What is not checked

* Other information components (such as `x-api-id` or `x-audience` etc.)

### Future work

* More fields that are required for clear documentation may be identified and checked

## Source

[1] https://opensource.zalando.com/restful-api-guidelines/#218