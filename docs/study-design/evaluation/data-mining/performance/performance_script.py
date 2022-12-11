from calendar import c
import csv
import os
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
from subprocess import Popen, PIPE, STDOUT
import re
from memory_profiler import profile
import psutil
from memory_profiler import memory_usage
import time
import random, os
import statistics

# Get size of all files in kb which are converted in mb
def get_size(start_path = './apis-json'):
    with open('file_size.csv', 'w', newline='', encoding="utf8") as f:
        # create the csv writer
        writer = csv.writer(f)
        data = ["file_name", "file_size"]
        # write a row to the csv file
        writer.writerow(data)
    for dirpath, dirnames, filenames in os.walk(start_path):
        for filename in filenames:
            
            fp = os.path.join(dirpath, filename)
            size = str(os.path.getsize(fp)/1024)
            print(filename + "is : " + size)
            with open('file_size.csv', 'a', newline='', encoding="utf8") as f:
                # create the csv writer
                writer = csv.writer(f)
                data_to_write = [filename, size]
                # write a row to the csv file
                writer.writerow(data_to_write)

# Method to categorize the files based on the number of paths, files need to be downloaded first
def get_size(start_path = './apis-json'):
    # Path to the category file
    with open('api_information_categorisation.csv', 'w', newline='', encoding="utf8") as f:
        # create the csv writer
        writer = csv.writer(f)
        data = ["file_name", "file_size_in_mb", "number_paths", "category"]
        # write a row to the csv file
        writer.writerow(data)
    for dirpath, dirnames, filenames in os.walk(start_path):
        for filename in filenames:
            
            fp = os.path.join(dirpath, filename)
            kbytes = os.path.getsize(fp)/1024
            size = str(kbytes/1024)
            # Path to the file location
            with open("./apis-json/" + filename, 'r', encoding="utf8") as file_json:
                file_validated = file_json.read().replace("}\n{", "},\n{")
                data_json = json.loads(file_validated)
                count_paths_number = len(data_json['paths'])
                print("File name :" + filename + ". Counter of paths: " + str(count_paths_number))
                with open('api_information_categorisation.csv', 'a', newline='', encoding="utf8") as f:
                    # create the csv writer
                    writer = csv.writer(f)
                    category = ""
                    if count_paths_number <= 10:
                        category = "very low"
                    elif count_paths_number <= 30:
                        category = "low"
                    elif count_paths_number <= 70:
                        category = "medium"
                    elif count_paths_number <= 150:
                        category = "high"
                    elif count_paths_number <= 310:
                        category = "very high"
                    elif count_paths_number > 310:
                        category = "huge"
                    data_to_write = [filename, size, count_paths_number, category]
                    # write a row to the csv file
                    writer.writerow(data_to_write)

 # Execution of the jar file using psutil
def execute_java1(json_file, file_name):
    count_java = 0
    statistic = {}
    totalViolations = 0
    failed = []
    pattern = re.compile(r'In total \d+ rule violations were found')
    title = json_file.strip(".json")
    #define path to jar here
    path_to_jar = './java/cli-0.1-all.jar'
    counter = 0
    # '~/RESTRuler/cli/'
    process_cwd = './cli/'
    # print("In method execute: " + str(pathlib.Path().resolve()) + "and the file is : " + json_file)
    print("In methos the file is : " + json_file)
    pfad_file = "C:\\Users\\abaji\\OneDrive\\Desktop\\DataMiningREST\\apis-json\\" + json_file
    process = Popen(['java', '-jar',
                                        path_to_jar,
                                         '-r', pfad_file, '-t', file_name],
                                        cwd=process_cwd, stdout=PIPE, stdin=PIPE, stderr=PIPE, shell=True)
    ps = psutil.Process(process.pid)
    # print(ps.parents()[0].children(recursive=True)[2].memory_percent())
    # print("and the pid of process is : " +  str(process.pid))
    process.communicate(input=b'no')[0]

# Execute the jar file of the tool and measure the time required to perform an analysis
def misure_time(type_of_category, name_of_folder_where_file_located):
    count = 0
    with open('api_information_categorisation_test.csv') as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        for row in csv_reader:
            for dirpath, dirnames, filenames in os.walk('./apis-json'):
                for filename in filenames:
                    if(row[3] == type_of_category and filename == row[0]):
                        print(filename)
                        
                        fp = os.path.join(dirpath, filename)
                        file_name = filename.strip(".json") + ".csv"
                        st = time.time()
                        execute_java1(filename, filename.strip(".json"))
                        et = time.time()
                        elapsed_time = et - st
                        print('Execution time:', elapsed_time, 'seconds')
                        print(filename.strip(".json") + " is done")
                        with open(os.path.join('./'+ name_of_folder_where_file_located + '/',file_name), 'a') as f:
                            f.write(str(count) + "," + filename + "," + str(elapsed_time) + "," + row[1] + "," + row[2] +"\n")
                        count += 1
                        time.sleep(1.5)

# Method to select random files from the folder
def select_random_files():
    dirpath = './apis-json/'
    destDirectory = './random-files'
    filenames = random.sample(os.listdir(dirpath), 30)
    for fname in filenames:
        #filenames.append(fname)
        print(fname)
        os.remove(os.path.join(dirpath, fname))
    print(filenames)

# Method to get statistic data from csv files of the analyzed files
def get_statistic_data(type):
    list_data_time = []
    list_data_size = []
    list_data_paths = []
    for dirpath, dirnames, filenames in os.walk('./' + type):
        for filename in filenames:
            with open('./' + type + '/' + filename, "r", encoding="utf8") as csvfile:
                reader = csv.reader(csvfile, delimiter=',')
                for row in reader:
                    count_char = filename.count(",")
                    print(row[1])
                    print(count_char)
                    if count_char == 0:
                        list_data_time.append(float(row[2]))
                        list_data_size.append(float(row[3]))
                        list_data_paths.append(float(row[4]))
                    elif count_char == 1:
                        list_data_time.append(float(row[3]))
                        list_data_size.append(float(row[4]))
                        list_data_paths.append(float(row[5]))
                    elif count_char == 2:
                        list_data_time.append(float(row[4]))
                        list_data_size.append(float(row[5]))
                        list_data_paths.append(float(row[6]))
                    elif count_char == 3:
                        list_data_time.append(float(row[5]))
                        list_data_size.append(float(row[6]))
                        list_data_paths.append(float(row[7]))
                    elif count_char == 4:
                        list_data_time.append(float(row[6]))
                        list_data_size.append(float(row[7]))
                        list_data_paths.append(float(row[8]))
                    
                    
    list_data_time.sort()
    print(max(list_data_time))
    print(min(list_data_time))
    print(statistics.median(list_data_time))
    print("Size \n")
    print(max(list_data_size))
    print(min(list_data_size))
    print(statistics.median(list_data_size))
    print("Paths \n")
    print(max(list_data_paths))
    print(min(list_data_paths))
    print(statistics.median(list_data_paths))


# Main method to execute the program
if __name__ == '__main__':
    misure_time("medium", "medium2")