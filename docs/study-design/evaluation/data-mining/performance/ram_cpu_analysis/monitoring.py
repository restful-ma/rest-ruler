import psutil
import time
import os 
from calendar import c
import csv
import urllib.request, json 
import os
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
from subprocess import Popen, PIPE, STDOUT
import subprocess
import re
from memory_profiler import profile
import threading
import psutil
from memory_profiler import memory_usage
import time
import pathlib
import shutil, random, os
import statistics


def method_monitoring():
    count = 0
    counter_boolean = True
    while(True):
        print("First LOOP" + str(count))
        titel = "Process_ID_" + str(count) + ".txt"
        list_process_pids = psutil.pids()
        try:
            for process_pid in list_process_pids:
                process = psutil.Process(process_pid)
                if(process.name() == 'java.exe'):
                    while(psutil.pid_exists(process.pid)):
                        print(process.pid)
                        process_memory = 16000 * (process.memory_percent() / 100)
                        print(process.memory_percent())
                        print(process_memory)
                        if counter_boolean:
                            count += 1
                            counter_boolean = False
                        with open(os.path.join('./test/',titel), 'a') as f:
                            f.write(str(process.name()) + ";" + str(process_memory) + ";" + str(process.memory_percent()) + ";" + str(process.cpu_percent()) + ";" + str(process.pid) + "\n")
                        time.sleep(0.05)
        except:  
            print("Error")
            counter_boolean = True  
            pass

def method_monitoring1():
    count = 0
    counter_boolean = True
    titel = ""
    while(True):
        print("First LOOP" + str(count))
        
        list_process_pids = psutil.pids()
        
        
        titel = "Process_ID_" + str(count) + ".csv"
        try:
            list_psu_process = [psutil.Process(item) for item in list_process_pids]
            matches = [x for x in list_psu_process if x.name() == 'java.exe']
            boolean_test = psutil.pid_exists(matches[0].pid)
            count += 1
            
            print(boolean_test)
            while(psutil.pid_exists(matches[0].pid)):
                #print(process.pid)
                process_memory = 16000 * (matches[0].memory_percent() / 100) 
                #print(process.memory_percent())
                #print(process_memory)
                if process_memory < 10:
                    process = matches[1]
                else:
                    process = matches[0]
                process_memory = 16000 * (process.memory_percent() / 100)
                with open(os.path.join('./medium2/',titel), 'a') as f:
                    f.write(str(process.name()) + "," + str(process_memory) + "," + str(process.memory_percent()) + "," + str(process.cpu_percent()) + "\n")
                time.sleep(0.05)
        except:  
            #print("Error")
            pass
def get_veryhigh_file():
    list_memory = []
    list_cpu = []
    list_max_memory = []
    list_min_memory = []
    list_max_cpu = []
    list_min_cpu = []
    for dirpath, dirnames, filenames in os.walk('./verylow2'):
        for filename in filenames:
            list_temp_memory = []
            list_temp_cpu = []
            with open('./verylow2/'+filename, "r", encoding="utf8") as csvfile:
                reader = csv.reader(csvfile, delimiter=',')
                for row in reader:
                    list_temp_memory.append(float(row[1]))
                    list_temp_cpu.append((float(row[3])/12))
            list_max_memory.append(max(list_temp_memory))
            list_min_memory.append(min(list_temp_memory))
            list_max_cpu.append(max(list_temp_cpu))
            list_min_cpu.append(min(list_temp_cpu))
            list_memory.append(statistics.median(list_temp_memory))
            list_cpu.append(statistics.median(list_temp_cpu))

    print("Memory")
    print("Max: " + str(max(list_max_memory)))
    print("Min: " + str(min(list_min_memory)))
    print("Average: " + str(statistics.median(list_memory)))
    print("CPU")
    print("Max: " + str(max(list_max_cpu)))
    print("Min: " + str(min(list_min_cpu)))
    print("Average: " + str(statistics.median(list_cpu)))


#method_monitoring1()
get_veryhigh_file()