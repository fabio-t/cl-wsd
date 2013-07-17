#!/usr/bin/python

files = ['balance', 'race', 'change', 'step', 'bank', 'sentence', 'right']

splt = []

word = ""

for file in files:
	corp = open(file, "r")
	new_corp = open(file + "_new", "w")
	
	for line in corp:
		splt = line.split("|")

		new_corp.write(splt[0] + "\n")

	corp.close()
	new_corp.close()

