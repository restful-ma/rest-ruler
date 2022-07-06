# Study Design
## Goal
The goal of this project is to provide a tool-based approach to automatically identify design rule violations in REST APIs. This approach is intended to support developers in detecting and fixing rule violations in their REST API definitions.

### Research Questions
**RQ1:** What is a feasible tool-supported approach to automatically identify design rule violations in REST APIs?
* Static and dynamic analysis
* Relating software quality attributes to the rule violations
* Extensibility of rules in the CLI

**RQ2:** How reliably is the tool prototype able to identify violations of the implemented rules?
- Using static rules
- Using dynamic rules

## Methodology
### Step 1 (Rule Analysis)
* Analyze and categorize rules (static/dynamic, notes and ideas for implementation)
* Create concept for analysis (md file on GitHub)

### Step 2 (Rule Implementation) 
* Create Architeture for CLI
* Iterative steps:
    * Create OpenAPI definition that contains (multiple) rule violation to be developed (for gold standard) --> Single file
        * File is reviewed by third parties
    * Create concept for rule and a possible implementation (md file on GitHub)
    * Implementation of a rule
        * Developer of rule creates unit tests
        * Second and third developer checks code via PR
* After the implementation, the gold standard is used to test and improve the CLI

#### CLI Requirements (May change during development)
* Reading a OpenAPI json/yaml file by specifying a path
* Detection of rule violations
* Report about rule violations with indication in which line of code it can be found and how critical it is
* Report at the end about the software quality (rule violations are attributed/subordinated to quality properties)
* Output file (rule violations + improvement suggestions + software quality report)

#### Reasons for developing a new tool
Existing tools are too **static**, difficult to extend (software quality attributes; dynamic rules; evaluation of previously impl. rules) --> Custom implementation more flexible and most likely better/faster way to address requirements

### Step 3 (Evaluation RQ2)
* **Static analysis** of mined APIs from apis.guru [3] (automated):
    1. How many APIs can the tool examine without any problems
        * Problems with parsing as well as problems in analysis --> Is an error thrown?
    2. What static rule violations are found
    3. Comparison with other CLIs
        * Problems in parsing as well as problems in analysis (1.)
        * Recognition of same rules (2.) --> Are the same rules recognized at the same place by both CLIs?
* **Dynamic analysis** of individual APIs (manual). Similar to the approach used by the study of Palma et al [4].
    * What dynamic rule violations are found?
* **Gold Standard** examines recall and precision --> collection of rule violations in openAPI file from step 2 is the gold standard.


# Roadmap
Date | Milestone
------------- | -------------
06\. July    | Rule analysis (Step 1)
31\. August  | Rule implementation (Step 2)
15\. October  | Evaluation (Step 3)
30\. November | Writing Paper

# Sources
[1] Kotstein, S., Bogner, J. (2021). Which RESTful API Design Rules Are Important and How Do They Improve Software Quality? A Delphi Study with Industry Experts. In: Barzen, J. (eds) Service-Oriented Computing. SummerSOC 2021. Communications in Computer and Information Science, vol 1429. Springer, Cham. https://doi.org/10.1007/978-3-030-87568-8_10

[2] https://www.oreilly.com/library/view/rest-api-design/9781449317904/

[3] https://apis.guru/

[4] Palma, F., Gonzalez-Huerta, J., Moha, N., Guéhéneuc, Y., & Tremblay, G. (2015). Are RESTful APIs Well-Designed? Detection of their Linguistic (Anti)Patterns. ICSOC. https://doi.org/10.1007/978-3-662-48616-0_11
