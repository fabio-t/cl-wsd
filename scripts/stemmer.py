#!/usr/bin/python

import Stemmer
import string

stemmer = Stemmer.Stemmer('italian')

fin = open("corpus_it", "r")
fout = open("corpus_it_stemm", "w")

table = string.maketrans("","")

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
