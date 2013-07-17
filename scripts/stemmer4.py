#!/usr/bin/python

import Stemmer
import string
import os

stemmer = Stemmer.Stemmer('italian')

files = ['coach', 'execution', 'job', 'match', 'mood', 'post', 'range', 'ring', 'side', 'test',
	'education', 'figure', 'letter', 'mission', 'paper', 'pot', 'rest', 'scene', 'soil', 'strain']

table = string.maketrans("","")

for file in files:
	fin = open(file, "r")
	fout = open(file + "_stemm", "w")

	word = ""
	fword = ""
	temp = ""

	for line in fin:
		words = line.split(" ");

		if len(words) != 3:
			exit("none");

		fword = stemmer.stemWord(words[0].translate(table, string.punctuation))
	
		if fword != "" and fword != " ":
			fout.write(fword.lower() + " " + words[1] + " " + words[2])

	fin.close()
	fout.close()
	
	os.remove("./"+file)
	os.rename("./"+file+"_stemm", "./"+file)
