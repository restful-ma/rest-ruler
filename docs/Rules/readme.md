# Rules
- [Rules](#rules)
  - [Documenting implemented rule](#documenting-implemented-rule)
  - [Implemented rules](#implemented-rules)
    - [Background information](#background-information)
    - [List of implemented rules](#list-of-implemented-rules)
    - [Rule implementation and extension](#rule-implementation-and-extension)
- [Sources](#sources)

## Documenting implemented rule
The rules implemented in the CLI are from Masse's book [1]. Additional informations about this rule are from Kotstein and Bogner [2]. Rules were selected by importance (categorization by Delphi study) --> first, implementation of high importance, ... Then add rule properties (difficulty of implementation, severity, type of analysis, software quality attributes , etc.)
* **Category** of the rule (from Kotstein and Bogner [2])
* Rules are categorized into three categories regarding **difficulty of implementation**: *easy, medium, and difficult*. 
* Matching the implementation idea, add the **type of analysis**: *static and/or dynamic*.
* **Software quality attributes** linked to rules are from Kotstein and Bogner. Quality attributes are: *functional suitability, performance efficiency, compatibility, usability, reliability, maintainability and portability*. 
* Categorize the rules according to the **severity**: *Warning* (may be), *Error* (should be), and *Critical* (must be). The severity indicates the urgency of a violation to be fixed. Naming conventions were inspired by IBM [3]. The tag *Critical* is derived from Massé's definition of the rule, in which *"must be"* is used as a phrase. It means that there is a critical condition with this violation and must be fixed immediately. An *Error* is assigned if *"should be"* is used as a phrase in the rule definition and means that there is an error condition with the violation, which should be removed. Lastly, the tag *Warning* is used when *"may be"* is used as a phrase and indicates a possible problem that may be fixed sooner or later.
* Add implementation details (what is checked, what is not checked, future implementations)

Template for the rules is in the file `./rule-template.md`

## Implemented rules

### Background information
Many of the rules implemented work with a part of the URI segment for an HTTP request.
URI stands for **U**niform **R**esource **I**dentifier and is a sequence of characters that identifies an abstract or physical resource. According to RFC 3986 [4] a URI has following structure:

*URI: scheme "://" authority "/" path [ "?" query ] [ "\#" fragment ]* 

A possible example of a valid URI, following above structure would be:

*https://example.com:8080/shelter/animals?name=cat\#rescue*

with *"https"* being the scheme, *"example.com:8080"* the authority, *"shelter/animals"* the path, *"?name=cat"* the query, and *"\#rescue"* the fragment.

For the implementation of rules related to the URI, we work on the *"path"*-portion of the URI. In an OpenAPI definition of an API, the path portion can be found in the *"paths"*-segment. Each path in the *"paths"* segment may optionally contain a list of parameter definitions for both path variables as also query parameters next to the required definition of the path. It may also contain request bodies and response definitions for numerous HTTP response codes. In these responses, a detailed description of what kind of response object, such as their data type, will be received for each HTTP response code.

### List of implemented rules
* Forward slash separator (/) must be used to indicate a hierarchical relationship
* A trailing forward slash (/) should not be included in URIs
* GET and POST must not be used to tunnel other request methods 
* GET must be used to retrieve a representation of a resource
* File extensions should not be included in URIs
* Content-Type must be used
* CRUD function names should not be used in URIs
* Underscores (_) should not be used in URI
* 401 (Unauthorized) must be used when there is a problem with the client’s credentials
* Hyphens (-) should be used to improve the readability of URIs
* Lowercase letters should be preferred in URI paths
* A plural noun should be used for collection or store names
* A singular noun should be used for document names
* A verb or verb phrase should be used for controller names

More documentation about the rules can be found in the file `./implemented-rules.md`

### Rule implementation and extension
For each rule, a single Java class is created, which can be found in `./rest-studentproject/src/main/java/rest/studentproject/rule/rules`. It is just as easy to implement a new rule. For implementing a new rule, it is merely necessary to create a Java class in the folder just mentioned, which implements the `IRestRule` interface. Then, a constructor with an `isActive` boolean is needed. Now the rule is automatically recognized and listed in the CLI. This is the minimum that needs to be done to implement a new static rule. However, for a dynamic analysis, it must first be checked whether it is requested by the user. Therefore, it is necessary to check the attributes `dynamicAnalysis` (boolean) and `securitySchemas` of the calling class. This is possible because they are `public static` in the Java class. The latter attribute is a list of user-specified security schemes for the API, either a `bearer` token, `basic` authentication, or an `api-key`. Based on this, the necessary information for a dynamic analysis is available and requests can be sent to the API.

For a better user experience, the progress bar can be called from the output class (`./rest-studentproject/src/main/java/rest/studentproject/rule/utility/Output.java`).

# Sources
[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/

[2] KOTSTEIN, Sebastian; BOGNER, Justus. Which RESTful API design rules are important and how do they improve software quality? A Delphi study with industry experts. In: Symposium and Summer School on Service-Oriented Computing. Springer, Cham, 2021. S. 154-173.

[3] IBM Message severity tags: https://www.ibm.com/docs/en/ess/6.0.1_ent?topic=messages-message-severity-tags

[4] Fielding et al. RFC 3986: https://www.rfc-editor.org/rfc/rfc3986
