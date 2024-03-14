# Request methods must not be used to tunnel other request methods

## Category

HTTP

## Importance, severity, difficulty

* Importance: high
* Severity: error
* Difficulty to implement the rule: medium

## Quality Attribute

* Maintainability
* Usability
* Compatibility
* Functional Suitability

## Rule Description

Description from Massé [1].

"Tunneling refers to any abuse of HTTP that masks or misrepresents a message's intent and undermines the protocol's transparency. A REST API must not compromise its design by misusing HTTP's request methods in an effort to accommodate clients with limited HTTP vocabulary. Always make proper use of the HTTP methods as specified by the rules in this section."

## Implemented

* Y

## Implementation Details (Issue #44)

### What is checked:

* For each request, we check if the description or summary attribute corresponds to the request type.
* To implement this, we used Weka [2] to train a Naive Bayes Multinomial classifier (`src/main/resources/models/request_model.dat`) that predicts the request type based on the description or the summary. We curated a dataset of over 3k instances for this (`model-training/request-model-training-data.txt`).
* If the prediction doesn't match with the given request type, we throw a violation.

### What is not checked:

* If the percentage of the predicted value is below 75%, then we don't consider the prediction as reliable.

### Future work

* --

## Source

- [1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
- [2] https://www.cs.waikato.ac.nz/ml/weka/