import subprocess
import re
import pandas as pd
import os

# list of all report generations that failed
failed = []
# list of all report generations that succeeded
success = []
# list of lines of files that got skipped due to being too large
skipped_lines = []
pattern = re.compile(r'In total \d+ rule violations were found')

#define path to jar here
path_to_jar = '~/rest-studentproject/build/libs/rest-studentproject-0.1-all.jar'

# '~/Projektarbeit-Master/rest-studentproject/'
process_cwd = ''

# specify input filename here
input_filename = ''

# specify output filename here
output_filename = ''

REPORT_OUT_DIR = os.path.join(process_cwd, 'out')





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


with open('{}.csv'.format(input_filename), "r", encoding="utf8") as f:

    if f.readable():
        # ignore first line with column descriptions
        f.readline()
        line = f.readline()

        # for displaying current line number
        count = 2

        while line:
            # prepare data
            columns = line.split(";")
            title = columns[0]
            categories = columns[1]
            version = columns[2]
            url = columns[3]
            print( str(count) +  " title: " + title)
            smaller = file_size_smaller(title, 4)
            count += 1
            if not smaller:
                print("File skipped because too large")
                skipped_lines.append(line)
            else:
                dir_size = len(os.listdir(REPORT_OUT_DIR))
                process = subprocess.run(['java', '-jar',
                                        path_to_jar,
                                        '-r', url, '-t', title],
                                        cwd=process_cwd,
                                        input=b'no', capture_output=True)
                # check if a report file was created
                dir_size_after = len(os.listdir(REPORT_OUT_DIR))
                if dir_size < dir_size_after:
                    success.append(line)
                else:
                    failed.append((line, process.stdout, process.stderr))



            line = f.readline()

# write failed to file
with open("final_round/failed/failed_" + output_filename + ".txt", "w", encoding="utf8") as outputFile:
    outputFile.write('---------------------------------------------------\n')
    outputFile.write('failed to read following OpenAPI definitions\n')
    outputFile.write('---------------------------------------------------\n')
    outputFile.write('---------------------------------------------------\n')
    for entry in failed:
        columns = entry[0].split(";")
        title = columns[0]
        categories = columns[1]
        version = columns[2]
        url = columns[3]

        outputFile.write('Title: ' + title + '\n')
        outputFile.write('Categories: ' + categories + '\n')
        outputFile.write('Version: ' + version + '\n')
        outputFile.write('LINE: ' + str(entry[0]) + '\n')
        outputFile.write('URL: ' + url + '\n')
        outputFile.write('OUT: ' + str(entry[1]) + '\n')
        outputFile.write('ERROR: ' + str(entry[2]) + '\n')
        outputFile.write('---------------------------------------------------\n')
    outputFile.write('total: ' + str(len(failed)))

# write success to file
with open("final_round/failed/succeeded_" + output_filename + ".txt", "w", encoding="utf8") as outputFile:
    outputFile.write('---------------------------------------------------\n')
    outputFile.write('failed to read following OpenAPI definitions\n')
    outputFile.write('---------------------------------------------------\n')
    outputFile.write('---------------------------------------------------\n')
    for entry in success:
        columns = entry.split(";")
        title = columns[0]
        categories = columns[1]
        version = columns[2]
        url = columns[3]

        outputFile.write('Title: ' + title + '\n')
        outputFile.write('Categories: ' + categories + '\n')
        outputFile.write('Version: ' + version + '\n')
        outputFile.write('LINE: ' + str(entry) + '\n')
        outputFile.write('URL: ' + url + '\n')

        outputFile.write('---------------------------------------------------\n')

# track skipped files
with open("final_round/failed/skipped_" + output_filename + ".csv", "w", encoding="utf8") as out:
    out.write("title;x-apisguru-categories;openapiVer;swaggerUrl\n")
    for entry in skipped_lines:
        out.write(entry)
