
existing_Reports = []
duplicates = []
missing_reports = []

# input file used to for the research_runner script
runner_source_filename = ''
# output file of research_runner script for above input file
runner_output_filename = ''
# file containing all entries in above input file that share the same title
duplicate_list_filename = ''
# output file of this script
output_filename = ''

# data prep: load existing reports
with open(runner_output_filename, "r", encoding="utf8") as file:
    if file.readable():
        # ignore first line with column descriptions
        file.readline()
        line = file.readline()
        while line:
            columns = line.split("|")
            title = str(columns[0]).strip()
            existing_Reports.append(title)
            line = file.readline()

print('loaded data from \'{}\''.format(runner_output_filename))

# load in all titles that belong to multiple entries
with open(duplicate_list_filename, "r", encoding="utf8") as file:
    if file.readable():
        # ignore first line with column descriptions
        file.readline()
        line = file.readline()
        while line:
            duplicates.append(line)
            line = file.readline()


print('loaded data from \'{}\''.format(duplicate_list_filename))

#find missing reports
# enter source file of original run containing all entries
with open(runner_source_filename, "r", encoding="utf8") as f:
    if f.readable():
        # ignore first line with column descriptions
        f.readline()
        line = f.readline()
        while line:
            # prepare data from row
            columns = line.split(";")
            title = str(columns[0])
            if title not in duplicates and title not in existing_Reports:
                print('title: ' + title + ' was missing')
                missing_reports.append(line)
            line = f.readline()

print('writing findings to \'{}\''.format(output_filename))

with open(output_filename, "w", encoding="utf8") as out:
    out.write("title;x-apisguru-categories;openapiVer;swaggerUrl\n")
    for entry in missing_reports:
        out.write(entry)
