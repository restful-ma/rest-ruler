import os
import pandas as pd
import matplotlib.pyplot as plt

file_titles = {
    '401 ("Unauthorized") must be used when there is a problem with the client\'s credentials': "Unauthorized",
    "Content-Type must be used": "Content-Type",
    "CRUD function names should not be used in URIs": "CRUD",
    "File extensions should not be included in URIs": "File-extensions",
    "GET must be used to retrieve a representation of a resource": "GET-resource",
    "Hyphens (-) should be used to improve the readability of URIs": "Hyphens",
    "Lowercase letters should be preferred in URI paths": "Lowercase",
    "A plural noun should be used for collection or store names": "Plural",
    "A singular noun should be used for document names": "Singular",
    "Forward slash separator (/) must be used to indicate a hierarchical relationship": "Forward-slash",
    "A trailing forward slash (/) should not be included in URIs": "Trailing-slash",
    "GET and POST must not be used to tunnel other request methods": "Tunnel",
    "Underscores (_) should not be used in URI": "Underscores",
    "A verb or verb phrase should be used for controller names": "Verb",
    "Description of request should match with the type of the request.": "Tunnel-description",
}

df_empty = pd.DataFrame(
    {
        "File name": "",
        "Path": "",
        "Rule": "",
        "false-positive": "",
        "true-positive": "",
    },
    index=[0],
)

df_total = pd.DataFrame(
    {
        "File name": "",
        "Path": "",
        "Rule": "Total violations",
        "false-positive": "Total false positives",
        "true-positive": "Total true positives",
    },
    index=[0],
)

file_prefix = "C:\\Users\\manue\\Documents\\Studium\\Master\\2.Semester\\Forschungsprojekt\\Projektarbeit-Master\\docs\\Studiendesign\\evaluation\\effectivness\\false-positives\\"

data = pd.read_excel(
    file_prefix + "false-positives.xlsx",
    sheet_name=None,
)
violations = data["Tabelle1"]


def analyse_false_positive_total():
    global violations
    violation_rules_false_positive_total = violations["false-positive"].sum()
    violation_rules_true_positive_total = violations["true-positive"].sum()
    violation_rules_total = violations["Rule"].count()

    df_total_values = pd.DataFrame(
        {
            "File name": "",
            "Path": "",
            "Rule": str(violation_rules_total),
            "false-positive": str(violation_rules_false_positive_total),
            "true-positive": str(violation_rules_true_positive_total),
        },
        index=[0],
    )

    violations = pd.concat([violations.loc[:], df_empty]).reset_index(drop=True)
    violations = pd.concat([violations.loc[:], df_total]).reset_index(drop=True)
    violations = pd.concat([violations.loc[:], df_total_values]).reset_index(drop=True)

    with open(
        file_prefix + "total_false-positives.csv",
        "w",
    ) as file:
        violations.to_csv(file, encoding="utf-8")


def analyse_false_positive_per_rule():
    for rule in file_titles:

        violation_rules = violations[violations["Rule"] == rule]
        violation_rules_false_positive_total = violation_rules["false-positive"].sum()
        violation_rules_true_positive_total = violation_rules["true-positive"].sum()
        violation_rules_total = violation_rules["Rule"].count()

        df_total_values = pd.DataFrame(
            {
                "File name": "",
                "Path": "",
                "Rule": str(violation_rules_total),
                "false-positive": str(violation_rules_false_positive_total),
                "true-positive": str(violation_rules_true_positive_total),
            },
            index=[0],
        )
        violation_rules = pd.concat([violation_rules.loc[:], df_empty]).reset_index(
            drop=True
        )
        violation_rules = pd.concat([violation_rules.loc[:], df_total]).reset_index(
            drop=True
        )
        violation_rules = pd.concat(
            [violation_rules.loc[:], df_total_values]
        ).reset_index(drop=True)

        with open(
            file_prefix
            + "false-positives-per-rule\\"
            + file_titles[rule]
            + "_false-positives.csv",
            "w",
        ) as file:
            violation_rules.to_csv(file, encoding="utf-8")

analyse_false_positive_per_rule()
analyse_false_positive_total()
