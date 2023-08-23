# RESTRuler

The RESTRuler CLI is a tool that can evaluate RESTful APIs on the basis of design rule violations. These violations are based on the design rules from Mark Massé's book [REST API Design Rulebook](https://www.oreilly.com/library/view/rest-api-design/9781449317904/).
Currently, RESTRuler can parse the following Web API description languages:

* [OpenAPI v2](https://swagger.io/specification/v2/)
* [OpenAPI v3](https://github.com/OAI/OpenAPI-Specification)

RESTRuler has been developed in the [Empirical Software Engineering Group](https://www.iste.uni-stuttgart.de/ese) at the University of Stuttgart, Germany, as a research prototype written in Java.
It is a command-line tool that takes the path or URL to an OpenAPI definition file as input and displays a list of design rule violations as output.
Optionally, a Markdown report file can be generated with additional details and improvement suggestions.

## Design Rules

Descriptions of the implemented design rules can be found in our [rule documentation](./docs/rules/README.md).

## Project Architecture

A detailed description of the project and implemented components can be found in the [architecture documentation](./docs/architecture.md).

## General Usage Instructions

For a quick start, you can simply download `rest-ruler.jar` for a certain [release](https://github.com/restful-ma/rest-ruler/releases).
Then execute these commands in the same folder as the JAR file:

```bash
# execute JAR file to display CLI parameters
java -jar rest-ruler.jar -h

# run with an example API from https://apis.guru
java -jar rest-ruler.jar -p https://api.apis.guru/v2/specs/circleci.com/v1/openapi.yaml
```

If you want to download or clone the full repository, run these commands in the root folder of the repository to build and start the tool:

```bash
cd cli
# create JAR file
./gradlew assemble
# execute JAR file to display CLI parameters
java -jar build/libs/rest-ruler.jar -h
# run tests
./gradlew test
# test coverage (output: ./build/reports/jacoco/test/html/index.html)
./gradlew jacocoTestReport
```

## Usage Example

```bash
# run with an example API from https://apis.guru
java -jar build/libs/rest-ruler.jar -p https://api.apis.guru/v2/specs/circleci.com/v1/openapi.yaml
```

This produces the following output:

```cli
java -jar build/libs/rest-ruler.jar -p https://api.apis.guru/v2/specs/circleci.com/v1/openapi.yaml

-----------------INFO ANALYSIS----------------
-----------------------------------------------

Besides the static analysis there is the dynamic analysis for which the credentials for the openapi definition need to be provided. These are not stored unless you allow it. No changes will be made to resources nor are they saved. 
If you want to do the dynamic analysis, enter yes or y, if you do not want to do it, enter any other key.
[yes/no]
n
----------------------------------------------

Begin with the analysis of the file from: https://api.apis.guru/v2/specs/circleci.com/v1/openapi.yaml

----------------------------------------------
Aug 11, 2023 3:45:01 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 1 of 15 is now checked:
CRUD function names should not be used in URIs
[==========] 100%
Aug 11, 2023 3:45:01 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 2 of 15 is now checked:
GET must be used to retrieve a representation of a resource
[==========] 100%
Aug 11, 2023 3:45:01 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 3 of 15 is now checked:
Forward slash separator (/) must be used to indicate a hierarchical relationship
[==========] 100%
Aug 11, 2023 3:45:01 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 4 of 15 is now checked:
A verb or verb phrase should be used for controller names
[==========] 100%
Aug 11, 2023 3:45:03 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 5 of 15 is now checked:
Hyphens (-) should be used to improve the readability of URIs
[==========] 100%
Aug 11, 2023 3:45:04 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 6 of 15 is now checked:
File extensions should not be included in URIs
[==========] 100%
Aug 11, 2023 3:45:04 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 7 of 15 is now checked:
GET and POST must not be used to tunnel other request methods
[==========] 100%
Aug 11, 2023 3:45:04 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 8 of 15 is now checked:
A singular noun should be used for document names
[==========] 100%
Aug 11, 2023 3:45:08 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 9 of 15 is now checked:
Description of request should match with the type of the request.
Aug 11, 2023 3:45:08 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 10 of 15 is now checked:
401 ("Unauthorized") must be used when there is a problem with the client's credentials
[==========] 100%
Aug 11, 2023 3:45:08 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 11 of 15 is now checked:
Underscores (_) should not be used in URI
[==========] 100%
Aug 11, 2023 3:45:08 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 12 of 15 is now checked:
Content-Type must be used
[==========] 100%
Aug 11, 2023 3:45:08 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 13 of 15 is now checked:
Lowercase letters should be preferred in URI paths
[==========] 100%
Aug 11, 2023 3:45:08 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 14 of 15 is now checked:
A trailing forward slash (/) should not be included in URIs
[==========] 100%
Aug 11, 2023 3:45:08 PM cli.analyzer.RestAnalyzer runRuleViolationChecks
INFO: Rule 15 of 15 is now checked:
A plural noun should be used for collection or store names
[==========] 100%
REST API Specification Report
=============================
| Line No. | Line                                                     | Rule Violated                                                                           |
| -------- | -------------------------------------------------------- | --------------------------------------------------------------------------------------- |
| 27       | /me                                                      | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 38       | /project/{username}/{project}                            | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 38       | /project/{username}/{project}                            | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 38       | /project/{username}/{project}                            | A plural noun should be used for collection or store names                              |
| 80       | /project/{username}/{project}/build-cache                | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 80       | /project/{username}/{project}/build-cache                | A plural noun should be used for collection or store names                              |
| 80       | /project/{username}/{project}/build-cache                | A verb or verb phrase should be used for controller names                               |
| 97       | /project/{username}/{project}/checkout-key               | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 97       | /project/{username}/{project}/checkout-key               | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 97       | /project/{username}/{project}/checkout-key               | A plural noun should be used for collection or store names                              |
| 128      | /project/{username}/{project}/checkout-key/{fingerprint} | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 128      | /project/{username}/{project}/checkout-key/{fingerprint} | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 128      | /project/{username}/{project}/checkout-key/{fingerprint} | A plural noun should be used for collection or store names                              |
| 154      | /project/{username}/{project}/envvar                     | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 154      | /project/{username}/{project}/envvar                     | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 154      | /project/{username}/{project}/envvar                     | A plural noun should be used for collection or store names                              |
| 154      | /project/{username}/{project}/envvar                     | Hyphens (-) should be used to improve the readability of URIs                           |
| 170      | /project/{username}/{project}/envvar/{name}              | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 170      | /project/{username}/{project}/envvar/{name}              | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 170      | /project/{username}/{project}/envvar/{name}              | A plural noun should be used for collection or store names                              |
| 197      | /project/{username}/{project}/ssh-key                    | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 197      | /project/{username}/{project}/ssh-key                    | A plural noun should be used for collection or store names                              |
| 237      | /project/{username}/{project}/tree/{branch}              | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 237      | /project/{username}/{project}/tree/{branch}              | A plural noun should be used for collection or store names                              |
| 272      | /project/{username}/{project}/{build_num}                | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 272      | /project/{username}/{project}/{build_num}                | A plural noun should be used for collection or store names                              |
| 288      | /project/{username}/{project}/{build_num}/artifacts      | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 288      | /project/{username}/{project}/{build_num}/artifacts      | A plural noun should be used for collection or store names                              |
| 303      | /project/{username}/{project}/{build_num}/cancel         | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 303      | /project/{username}/{project}/{build_num}/cancel         | A plural noun should be used for collection or store names                              |
| 303      | /project/{username}/{project}/{build_num}/cancel         | Description of request should match with the type of the request.                       |
| 318      | /project/{username}/{project}/{build_num}/retry          | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 318      | /project/{username}/{project}/{build_num}/retry          | A plural noun should be used for collection or store names                              |
| 333      | /project/{username}/{project}/{build_num}/tests          | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 333      | /project/{username}/{project}/{build_num}/tests          | A plural noun should be used for collection or store names                              |
| 333      | /project/{username}/{project}/{build_num}/tests          | Description of request should match with the type of the request.                       |
| 350      | /projects                                                | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 362      | /recent-builds                                           | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 376      | /user/heroku-key                                         | 401 ("Unauthorized") must be used when there is a problem with the client's credentials |
| 376      | /user/heroku-key                                         | Hyphens (-) should be used to improve the readability of URIs                           |
----------------------------------------------

In total 40 rule violations were found
```

## Command Line Options
| Option                                       | Description                                                                                                               | Required |
| :------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------ | :------- |
| `-p $URI_PATH`,<br>`--path $URI_PATH`        | Local path or public URL to OpenAPI definition (2.0 or higher; JSON or YAML)                                              | YES      |
| `-e`,<br>`--expertMode`                      | Interactively select the rules for the analysis                                                                           | NO       |
| `-r`,<br>`--report`                          | Generate a Markdown report file with the analysis results                                                                 | NO*      |
| `-rn $FILENAME`,<br>`--reportName $FILENAME` | Specify a custom filename for the Markdown report. If this option is selected, the above option for output is not needed. | NO*      |

*If no additional output was specified, the results will only be printed to the console.

```bash
# Run with local file and no output file
java -jar build/libs/rest-ruler.jar -p path/to/openapi/definiton.json

# Run with public URL and no output file
java -jar build/libs/rest-ruler.jar -p https://www.custom.domain.com/path/to/openapi-definiton.yaml

# Run with custom filename for Markdown report
java -jar build/libs/rest-ruler.jar -p path/to/openapi/definiton.yaml -rn custom-file-name

# Run with generated filename for Markdown report
java -jar build/libs/rest-ruler.jar -p path/to/openapi/definiton.yaml -r

# Run in expert mode
java -jar build/libs/rest-ruler.jar -p path/to/openapi/definiton.json -e
```
