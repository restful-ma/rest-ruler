REST API Specification Report
=============================
| Line No. | Line | Rule Violated                                                 | Category | Severity | Rule Type | Software Quality Attributes    | Improvement Suggestion                                 |
| -------- | ---- | ------------------------------------------------------------- | -------- | -------- | --------- | ------------------------------ | ------------------------------------------------------ |
| 33       | /v1/ | A trailing forward slash (/) should not be included in URIs   | URIS     | ERROR    | STATIC    | MAINTAINABILITY                | remove trailing forward slash '/' from URI             |
| 33       | /v1/ | Hyphens (-) should be used to improve the readability of URIs | URIS     | ERROR    | STATIC    | COMPATIBILITY, MAINTAINABILITY | Use hyphens to improve the readability of the segments |