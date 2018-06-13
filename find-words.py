#This programm can help you match word on page`s content on the Internet
#When you run this script you should enter 2 or many parameters.
#The first is URL where you finding matches (necessarily)
#The second and the following are what words you want find (one word is necessarily)
#For example: python find-words.py "https://www.google.ru/" "google" "run" "Dima"
#If you want save logs to file 'log_current_date', change var 'TO_LOG' to '1'
#For example: export TO_LOG=1

import sys
import os
import requests
import time
import datetime


try:
    html_request = requests.get(sys.argv[1])

except requests.exceptions.ConnectionError:
    print "URL cat`t be recived"
    sys.exit(3)
except:
    print "URL empty or does not valid"
    sys.exit(1)

try:
    flag = 0
    num_args = len(sys.argv)
    count_list = list()
    count_list.append(sys.argv[0])
    count_list.append(sys.argv[1])
    sys.argv[2]
    env_var_log = os.getenv("TO_LOG")
    log_file_name = "log_" +  datetime.datetime.today().strftime("%d-%m-%Y_%H-%M-%S")
except:
    print "Print one or more words"
    sys.exit(1)

try:
    for i in range(2,num_args):
        if (len(count_list) <= i):
            count_list.append(html_request.text.count(sys.argv[i]))
        else:
            count_list[i] = count_list[i] + html_request.text.count(sys.argv[i])
        if count_list[i] != 0:
            flag = 1
except:
    print "OOPS!"
    sys.exit(1)

if flag == 0:
    print "All words are not finding on URL"
    sys.exit(2)
try:
    for i in range(2,num_args):
        print sys.argv[i], "-", count_list[i], "time(s)"
except:
    print "OOPS!"
    sys.exit(1)

try:
    if (env_var_log != None and env_var_log == "1"):
        f = open(log_file_name, "w")
        for i in range(2,num_args):
            f.write(str(sys.argv[i]) + " - " + str(count_list[i]) + " time(s)\n")
        f.close()
        print "logs saved at", os.getcwd() + "/" + log_file_name
    else:
        print "Environment variable TO_LOG does not exist or has value other then '1';\nIf you want have logs, run: export TO_LOG=1"
except:
    print "OOPS!"
    sys.exit(1)
else:
    sys.exit(0)
