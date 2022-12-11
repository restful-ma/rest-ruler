# Tool_Name_TBD

The Tool_Name_TBD CLI is a tool that can evaluate RESTful APIs on the basis of RESTful Design Rules. These Rules are based on the Design Rules from Mark Massé's book ["REST API Design Rulebook"](https://www.oreilly.com/library/view/rest-api-design/9781449317904/)
Currently, Tool_Name_TBD can parse the following RESTful API description languages:

* [OpenAPI v2](https://swagger.io/specification/v2/)
* [OpenAPI v3](https://github.com/OAI/OpenAPI-Specification)


Tool_Name_TBD has been developed in research projects at the [Software Engineering Group](https://www.iste.uni-stuttgart.de/se) of the University of Stuttgart as a Java command-line tool. It takes the path or URL to a OpenAPI definition file as input and displays a listing of design rule violations. Optionally a markdown report file can be generated with a listing of all rule violations with additonal details and improvement suggestions.


## Design Rules

Descriptions of the implemented Design Rules can be found in our [Rule documentation](./docs/Rules/readme.md).

## Project Architecture

A detailed description of the project and implemented components can be found at [Architecture](./cli/README.md)

## General Usage Instructions

Run these commands in the root folder to build and start the tool

```bash
cd cli
# build tool
./gradlew assemble
# execute program
java -jar build/libs/cli-0.1-all.jar -h
```

## Example:

```bash
# Run with an example API from api.guru https://apis.guru
java -jar build/libs/cli-0.1-all.jar -r https://api.apis.guru/v2/specs/1forge.com/0.0.1/swagger.json
```

This produces the following output:

```cli
java -jar build/libs/cli-0.1-all.jar -r https://api.apis.guru/v2/specs/1forge.com/0.0.1/swagger.json

-----------------INFO ANALYSIS----------------
-----------------------------------------------

Besides the static analysis there is the dynamic analysis for which the credentials for the openapi definition need to be provided. These are not stored unless you allow it. No changes will be made to resources nor are they saved.
If you want to do the dynamic analysis, enter yes or y, if you do not want to do it, enter any other key.
[yes/no]
no
----------------------------------------------

Begin with the analysis of the file from: https://api.apis.guru/v2/specs/1forge.com/0.0.1/swagger.json

----------------------------------------------
Dec 09, 2022 9:41:42 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 1 of 15 is now checked:
Forward slash separator (/) must be used to indicate a hierarchical relationship
[==========] 100%
Dec 09, 2022 9:41:42 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 2 of 15 is now checked:
401 ("Unauthorized") must be used when there is a problem with the client's credentials
[==========] 100%
Dec 09, 2022 9:41:42 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 3 of 15 is now checked:
Lowercase letters should be preferred in URI paths
[==========] 100%
Dec 09, 2022 9:41:42 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 4 of 15 is now checked:
Underscores (_) should not be used in URI
[==========] 100%
Dec 09, 2022 9:41:42 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 5 of 15 is now checked:
Content-Type must be used
[==========] 100%
Dec 09, 2022 9:41:42 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 6 of 15 is now checked:
A plural noun should be used for collection or store names
[==========] 100%
Dec 09, 2022 9:41:42 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 7 of 15 is now checked:
A trailing forward slash (/) should not be included in URIs
[==========] 100%
Dec 09, 2022 9:41:42 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 8 of 15 is now checked:
A singular noun should be used for document names
[==========] 100%
Dec 09, 2022 9:41:43 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 9 of 15 is now checked:
A verb or verb phrase should be used for controller names
[==========] 100%
Dec 09, 2022 9:41:44 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 10 of 15 is now checked:
Hyphens (-) should be used to improve the readability of URIs
[==========] 100%
Dec 09, 2022 9:41:44 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 11 of 15 is now checked:
File extensions should not be included in URIs
[==========] 100%
Dec 09, 2022 9:41:44 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 12 of 15 is now checked:
CRUD function names should not be used in URIs
[==========] 100%
Dec 09, 2022 9:41:44 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 13 of 15 is now checked:
GET and POST must not be used to tunnel other request methods
[==========] 100%
Dec 09, 2022 9:41:44 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 14 of 15 is now checked:
GET must be used to retrieve a representation of a resource
[==========] 100%
Dec 09, 2022 9:41:44 AM RestAnalyzer runRuleViolationChecks
INFO: Rule 15 of 15 is now checked:
Description of request should match with the type of the request.
REST API Specification Report
=============================
| Line No. | Line    | Rule Violated                                               |
| -------- | ------- | ----------------------------------------------------------- |
| 38       | /quotes | Content-Type must be used                                   |
| 38       | /quotes | GET must be used to retrieve a representation of a resource |
----------------------------------------------

In total 2 rule violations were found

```

## Command-Line Options
| Option               | Description                                                                                                     | Required |
| :------------------- | :-------------------------------------------------------------------------------------------------------------- | :------- |
| `-r $URI_PATH` `--runAnalysis $URI_PATH`     | Run the rest analysis. Required: Path to Openapi definition (2.0 or higher; json or yaml)                   | YES*     |
| `-e` `--expertMode`   | Enables custom selection of Design Rules to be used for analysis                                                                    | NO     |
| `-o` `--out`    | Generates a report/output file in mark down format | NO**      |
| `-t $FILENAME` `--title $FILENAME`  | Generates a report/output file with custom filename in mark down format. If this option is selected the above option for output is not needed.                     | NO**     |

*Either a file or a URI has to be specified as input.<br>
**If no additional output was specified, the report will only be outputted to the console.

```bash
# Run with no output file:
java -jar build/libs/cli-0.1-all.jar -r path/to/openapi/definiton.json

# Run with url:
java -jar build/libs/cli-0.1-all.jar -r https://www.custom.domain.com/path/to/openapi-definiton.yaml

# Run with custom output file name:
java -jar build/libs/cli-0.1-all.jar -r path/to/openapi/definiton.yaml -t custom-file-name

# Run with generated output file name:
java -jar build/libs/cli-0.1-all.jar -r path/to/openapi/definiton.yaml --out

# Run in expert mode:
java -jar build/libs/cli-0.1-all.jar -r path/to/openapi/definiton.json -e
```
