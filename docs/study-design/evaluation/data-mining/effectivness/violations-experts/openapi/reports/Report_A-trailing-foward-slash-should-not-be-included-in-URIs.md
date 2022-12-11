REST API Specification Report
=============================
| Line No. | Line             | Rule Violated                                               | Category | Severity | Rule Type | Software Quality Attributes | Improvement Suggestion                     |
| -------- | ---------------- | ----------------------------------------------------------- | -------- | -------- | --------- | --------------------------- | ------------------------------------------ |
| 15       | /users/          | A trailing forward slash (/) should not be included in URIs | URIS     | ERROR    | STATIC    | MAINTAINABILITY             | remove trailing forward slash '/' from URI |
| 47       | /users/{userId}/ | A trailing forward slash (/) should not be included in URIs | URIS     | ERROR    | STATIC    | MAINTAINABILITY             | remove trailing forward slash '/' from URI |