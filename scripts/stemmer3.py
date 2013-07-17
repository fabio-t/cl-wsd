#!/usr/bin/python

import Stemmer
import string
import os

stemmeri = Stemmer.Stemmer('italian')
stemmere = Stemmer.Stemmer('english')

files = ['coach', 'execution', 'job', 'match', 'mood', 'post', 'range', 'ring', 'side', 'test',
	'education', 'figure', 'letter', 'mission', 'paper', 'pot', 'rest', 'scene', 'soil', 'strain']

#files = ['race', 'change', 'step', 'bank', 'sentence', 'balance']

table = string.maketrans("","")

for file in files:
	fin = open(file, "r")
	fout = open(file + "_new", "w")

	word = ""
	fword = ""
	temp = ""

	for line in fin:
		subline = line.split("|")

		for word in subline[0].split():
			fword = word.translate(table, string.punctuation)
				
			if fword != file:
				fword = stemmere.stemWord(fword)
				
			fout.write(fword.lower() + " ")
			
		fout.write("|")
					
		for word in subline[1].split():
			fword = word.translate(table, string.punctuation)
				
			if fword != file:
				fword = stemmeri.stemWord(fword)
				
			fout.write(fword.lower() + " ")

		fout.write("\n")

	fin.close()
	fout.close()

	
	os.remove("./"+file)
	os.rename("./"+file+"_new", "./"+file)

