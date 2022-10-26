import subprocess
import re

statistic = {}
totalViolations = 0
failed = []
pattern = re.compile(r'In total \d+ rule violations were found')

#define path to jar here
path_to_jar = ''

# '~/Projektarbeit-Master/rest-studentproject/'
process_cwd = ''

with open("apis.csv", "r", encoding="utf8") as f:
    if f.readable():
        # ignore first line with column descriptions
        f.readline()
        line = f.readline()

        while line:
            # prepare datano
            columns = line.split(";")
            title = columns[0]
            categories = columns[1]
            version = columns[2]
            url = columns[3]

            process = subprocess.run(['java', '-jar',
                                      path_to_jar,
                                      '-r', url, '-t', title],
                                     cwd=process_cwd,
                                     input=b'no', capture_output=True)

            print("title: " + title)
            # process failed to finish
            if process.returncode != 0:
                failed.append((title, url, process.stderr))
                line = f.readline()
                continue


            for outputline in process.stdout.split(b'\n'):
                if pattern.search(str(outputline)):
                    for content in outputline.split():
                        if re.search(r'\d+', str(content)):
                            statistic[title] = int(content)
                            totalViolations += int(content)

            line = f.readline()

with open("out.csv", "w", encoding="utf8") as outputFile:
    outputFile.write('title | number of Violations found\n')
    for key, value in statistic.items():
        outputFile.write(key + ' | ' + str(value) + '\n')
    # failed APIs
    outputFile.write('---------------------------------------------------\n')
    outputFile.write('failed to read following OpenAPI definitions\n')
    outputFile.write('title | url | print trace \n')
    for item in failed:
        outputFile.write(item[0] + ' | ' + item[1] + ' | ' + item[2] + '\n')
    outputFile.write('---------------------------------------------------\n')
    outputFile.write('total APIs failed to read: ' + str(len(failed)) + '\n')
    outputFile.write('---------------------------------------------------\n')
    outputFile.write('total Violations found: ' + str(totalViolations))
