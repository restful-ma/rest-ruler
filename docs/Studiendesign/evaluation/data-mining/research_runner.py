import subprocess
import re
import pandas as pd


def file_size_smaller(name, mbs):
    df = pd.read_csv(
        "file_size_apis_guru.csv",
        sep=",",
        encoding="utf8",
        names=["file_name", "file_size_in_mb"],
    )
    if (
        name in df["file_name"].values
        and float(df[df["file_name"] == name]["file_size_in_mb"].iloc[0]) < mbs
    ):
        print(
            "File size is smaller than 4MBs --> Value: "
            + df[df["file_name"] == name]["file_size_in_mb"].iloc[0]
            + " MB"
        )
        return True
    if name in df["file_name"].values:
        print(
            "File size is bigger than 4MBs --> Value: "
            + df[df["file_name"] == name]["file_size_in_mb"].iloc[0]
            + " MB"
        )
    else:
        print("File is not in the list")
    return False


statistic = {}
totalViolations = 0
failed = []
pattern = re.compile(r"In total \d+ rule violations were found")
skip_files = 0


# define path to jar here
path_to_jar = "C:\\Users\\manue\\Documents\\Studium\\Master\\2.Semester\\Forschungsprojekt\\Projektarbeit-Master\\rest-studentproject\\build\\libs\\rest-studentproject-0.1-all.jar"

# '~/Projektarbeit-Master/rest-studentproject/'
process_cwd = "C:\\Users\\manue\\Documents\\Studium\\Master\\2.Semester\\Forschungsprojekt\\Projektarbeit-Master\\rest-studentproject"

with open("apis.csv", "r", encoding="utf8") as f:
    if f.readable():
        # ignore first line with column descriptions
        f.readline()
        line = f.readline()

        # for displaying current line number
        count = 1
        # start_index = 1206

        while line:
            # if count <= start_index:
            #     count += 1
            #     line = f.readline()
            #     continue

            # prepare datano
            columns = line.split(";")
            title = columns[0]
            categories = columns[1]
            version = columns[2]
            url = columns[3]

            print(str(count) + " title: " + title)
            smaller = file_size_smaller(title, 4)
            count += 1
            if not smaller:
                print("File skipped because too large")
                skip_files += 1
                count += 1
                line = f.readline()
                continue

            process = subprocess.run(
                ["java", "-jar", path_to_jar, "-r", url, "-t", title],
                cwd=process_cwd,
                input=b"no",
                capture_output=True,
            )

            # process failed to finish
            if process.returncode != 0:
                failed.append((title, url, process.stderr))
                line = f.readline()
                print("Analysis FAILED for: " + title + " |" + url + "\n")
                print(process.stderr)
                print("\n")
                continue

            for outputline in process.stdout.split(b"\n"):
                if pattern.search(str(outputline)):
                    for content in outputline.split():
                        if re.search(r"\d+", str(content)):
                            statistic[title] = int(content)
                            totalViolations += int(content)

            line = f.readline()

with open("out.csv", "w", encoding="utf8") as outputFile:
    outputFile.write("title | number of Violations found\n")
    for key, value in statistic.items():
        outputFile.write(key + " | " + str(value) + "\n")
    # failed APIs
    outputFile.write("---------------------------------------------------\n")
    outputFile.write("failed to read following OpenAPI definitions\n")
    outputFile.write("title | url | print trace \n")
    for item in failed:
        outputFile.write(item[0] + " | " + item[1] + " | " + item[2] + "\n")
    outputFile.write("---------------------------------------------------\n")
    outputFile.write("total APIs failed to read: " + str(len(failed)) + "\n")
    outputFile.write("---------------------------------------------------\n")
    outputFile.write(
        "total APIs skipped because of file size: " + str(skip_files) + "\n"
    )
    outputFile.write("---------------------------------------------------\n")
    outputFile.write("total Violations found: " + str(totalViolations))
