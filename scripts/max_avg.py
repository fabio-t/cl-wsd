#!/usr/bin/python

import re

f1 = open("averages", "r")
f2 = open("max_avg", "w")

max_prec = 0.0
max_rec = 0.0
max_name_prec = ""
max_name_rec = ""

for l in f1:
	s = re.split("\t+", l.strip())
	
	if float(s[2]) > max_prec:
		max_prec = float(s[2])
		max_name_prec = s[0]
	
	if float(s[4]) > max_rec:
		max_rec = float(s[4])
		max_name_rec = s[0]

f2.write(max_name_prec + " = " + str(max_prec) + "\n" + max_name_rec + " = " + str(max_rec) + "\n")

f1.close()
f2.close()
