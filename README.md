# Tool_Name_TBD

The Tool_Name_TBD CLI is a tool that can evaluate RESTful APIs on the basis of RESTful Design Rules. These Rules are based on the Design Rules from Mark Massé's book ["REST API Design Rulebook"](https://www.oreilly.com/library/view/rest-api-design/9781449317904/)
Currently, Tool_Name_TBD can parse the following RESTful API description languages:
* [OpenAPI v2](https://swagger.io/specification/v2/)
* [OpenAPI v3](https://github.com/OAI/OpenAPI-Specification)


Tool_Name_TBD has been developed in research projects at the [Software Engineering Group](https://www.iste.uni-stuttgart.de/se) of the University of Stuttgart as a Java command-line tool. It takes the path or URL to a OpenAPI definition file as input and displays a listing of design rule violations. Optionally a markdown report file can be generated with a listing of all rule violations with additonal details and improvement suggestions.


## Design Rules
Descriptions of the implemented Design Rules can be found in our [Rule documentation](docs/Rles/README.md).

## General Usage Instructions
Run these commands in the root folder to build and start the tool
```bash
cd rest-studentproject
# build tool
./gradlew assemble
# execute program
java -jar build/libs/rest-studentproject-0.1-all.jar
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
java -jar build/libs/rest-studentproject-0.1-all.jar -r path/to/openapi/definiton.json

# Run with url:
java -jar build/libs/rest-studentproject-0.1-all.jar -r https://www.custom.domain.com/path/to/openapi-definiton.yaml

# Run with custom output file name:
java -jar build/libs/rest-studentproject-0.1-all.jar -r path/to/openapi/definiton.yaml -t custom-file-name

# Run with generated output file name:
java -jar build/libs/rest-studentproject-0.1-all.jar -r path/to/openapi/definiton.yaml --out

# Run in expert mode:
java -jar build/libs/rest-studentproject-0.1-all.jar -r path/to/openapi/definiton.json -e
```
