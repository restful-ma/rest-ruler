REST API Specification Report
=============================
| Line No. | Line                               | Rule Violated                                              | Category | Severity | Rule Type | Software Quality Attributes | Improvement Suggestion                     |
| -------- | ---------------------------------- | ---------------------------------------------------------- | -------- | -------- | --------- | --------------------------- | ------------------------------------------ |
| 16       | /user_names                        | Underscores (_) should not be used in URI                  | URIS     | ERROR    | STATIC    | MAINTAINABILITY             | Use hyphens (-) instead of underscores (_) |
| 44       | /user_names/{userId}               | A plural noun should be used for collection or store names | URIS     | ERROR    | STATIC    | USABILITY, MAINTAINABILITY  | Use singular nouns for document names      |
| 44       | /user_names/{userId}               | Underscores (_) should not be used in URI                  | URIS     | ERROR    | STATIC    | MAINTAINABILITY             | Use hyphens (-) instead of underscores (_) |
| 78       | /users/{userId}/cvs/place_of_birth | Underscores (_) should not be used in URI                  | URIS     | ERROR    | STATIC    | MAINTAINABILITY             | Use hyphens (-) instead of underscores (_) |
| 112      | /_user                             | Underscores (_) should not be used in URI                  | URIS     | ERROR    | STATIC    | MAINTAINABILITY             | Use hyphens (-) instead of underscores (_) |