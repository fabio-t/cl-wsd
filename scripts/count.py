#!/usr/bin/python

import os
import sys

file = open("single_scores", "r")
file2 = open("average_score", "w")

sum1 = 0.0
sum2 = 0.0

i = 0
for line in file:
	splt = line.split(";")

	sum1 = sum1 + float(splt[0])
	sum2 = sum2 + float(splt[1])

	i = i+1

if i != 20:
	print "errore: " + os.getcwd() + " sum1= " + str(sum1) + " sum2= " + str(sum2) + " i= " + str(i) + " line= " + line
	sys.exit(1)

print os.path.basename(os.getcwd()) + "\t\t" + str(sum1/20.0) + "\t" + str(sum2/20.0)

file2.write(os.path.basename(os.getcwd()) + "\t\tprecision\t" + str(sum1/20.0) + "\t\trecall\t" + str(sum2/20.0) + "\n")

file.close()
file2.close()
