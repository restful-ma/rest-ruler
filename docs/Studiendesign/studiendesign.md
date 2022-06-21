# Study Design
## Goal 
In this study we will, on basis of the 45 Rules listed in the Delphi study [1], create a software artefact that recognizes violations of those 45 rules mentioned. These rules are dividable into semantic and syntactical rules. Additionally, to providing a software artefact the goal is to document our approach to implementing those semantic rules.
To achieve this, we will be using a bottom-up approach, by taking the previous mentioned rules and creating metrics and heuristics to identify rule violations. These will then be linked back to individual software quality attributes. 
The remaining rules, not covered by previous studies will be implemented in an explorative and iterative fashion. Ideally a general approach to implementing those rules can be derived and formulated.

**RQ1:** Is there a general approach to implement semantic rules for RESTful APIs?

**RQ2:** How precise is the implementation tool to detect violations of the rules? --> Benchmark test

## Methodology
<pre><code>// Step 0: Creat Create foundation of the CLI
implementFoundation()

// Step 1: Extend CLI in iterative fashion
while not 30.08.22 do
    analyzeRules()
    implementRules()
    test()
end while

// Step 2: Get openAPI samples for benchmark
mineOpenAPIFiles()
testParsingOfSamplesCLI()

// Step 3: Benchmark CLIs
compareOutputBetweenCLIs()

// Step 4: Scientific paper
wirtePaper()
</code></pre>

![Steps](https://i.imgur.com/1WAowsh.png)


### Step 0
Developing the foundation of the CLI with the parser. Java is used as the programming language for the CLI. In addition to the CLI, a UI and/or a VSCode plugin can be developed.

We use **Scrum** with sprints of 2 weeks at the beginning. Sprint planning is done with the help of the GitHub issue board. A pipline is set up to ensure a certain software quality (tests + Lint + Sonarqube?).

Requirements for the **CLI**:
* Reading of a json/yaml file by specifying a path
* Detection of rule violations
* report about rule violations with indication in which line of code it can be found and how critical it is
* Report at the end about the software quality (rule violations are attributed/subordinated to quality properties)
* Output file (rule violations + improvement suggestions + software quality report)

Requirements for the **VSCode plugin**:
* Detection of rule violations
* Display rule violations directly in the code
* When hovering over the rule violation, suggestions for improvement are displayed
* A button to create a report (rule violations + improvement suggestions + software quality report)

Requirements to the **UI**:
* Reading a json/yaml file by specifying a path.
* Viewing of the read file in a dif
* The dif is editable and can be revalidated by a button
* Representation of the rule violations with appropriate improvement suggestions and connection to software quality. Additionally how critical each rule is
* By clicking on the rule, the violation is highlighted in the dif.

### Step 1
Iteratively, rules from Paper [1] are first examined and then implemented using Masse's book [2] as a helping tool (only those that are implementable). These will then be added to the CLI and tested. Testing is ensured by writing unit tests that guarantee that each rule is detected and evaluated to provide the appropriate feedback. The rules will be added by 30.08.22.

![](https://i.imgur.com/pqyOk38.png)


### Step 2
After the tool is able to detect and evaluate the rules, we will run a benchmark with different RestApiFiles from different sources to test the performance. This will be a real scenario where we can test how well the tool works with different rules, but also to see if there are no major bugs or complications. To extract and provide the RestApiFiles, we will use the website https://apis.guru/, where we will get 2,353 RestAPIFiles. The mining of these rules wil be done automatically by a Python script. This wil be possible thanks to the API that the website offers and through which one can retrieve all the RestAPIFiles that are hosted on the website. Then we will store them locally and evaluate them with our tool. This evaluation will provide us with data to better understand how well our tool is working. This data will be used as reference for the next step. The whole step 2 is done automatically by a Python script that first retrieves the rules, stores them and finally evaluates them with our tool. 

![Step 2](https://i.imgur.com/w8Ud61X.png)



### Step 3
Step 2 is then performed for already existing tools (e.g. [3]). The results obtained are then compared with the self-implemented tool and evaluated. It is not compared how many rule violations occurred between the tools, instead only the number of correctly executed runs of the samples. The testing of the tool is automated by a Python script.



[1] https://link.springer.com/chapter/10.1007/978-3-030-87568-8_10
[2] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
[3] https://github.com/zalando/zally

