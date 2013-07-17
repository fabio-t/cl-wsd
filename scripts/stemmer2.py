#!/usr/bin/python

import Stemmer
import string
import os

#stemmer = Stemmer.Stemmer('english')
stemmer = Stemmer.Stemmer('italian')

#files = ['coach', 'execution', 'job', 'match', 'mood', 'post', 'range', 'ring', 'side', 'test',
#	'education', 'figure', 'letter', 'mission', 'paper', 'pot', 'rest', 'scene', 'soil', 'strain']

#files = ['race', 'change', 'step', 'bank', 'sentence', 'balance']

#files = ['stopwords_en']
files = ['stopwords_it']

table = string.maketrans("","")

for file in files:
	fin = open(file, "r")
	fout = open(file + "_stemm", "w")

	word = ""
	fword = ""
	temp = ""

	for line in fin:
		for word in line.split():
			fword = stemmer.stemWord(word.translate(table, string.punctuation))
		
			fout.write(fword.lower() + " ")

		fout.write("\n")

	fin.close()
	fout.close()
	
	#os.remove("./"+file)
	#os.rename("./"+file+"_new", "./"+stemmer.stemWord(file))

