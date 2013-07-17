#!/usr/bin/python
# -*- coding: utf-8 -*-

import Stemmer
import string
import os
import re

p = re.compile("(\w+) \\(\\{(.*?)\\}\\) ")

stemmer = Stemmer.Stemmer('english')

#target_words = ['coach', 'execution', 'job', 'match', 'mood', 'post', 'range', 'ring', 'side', 'test',
#	'education', 'figure', 'letter', 'mission', 'paper', 'pot', 'rest', 'scene', 'soil', 'strain']

target_words = ['execution']

files = {}

table = string.maketrans("","")

for target_word in target_words:
	toWrite = False
	corpus = open("corpus_aligned", "r")
	files[target_word] = open(target_word + ".csv", "w")

	for line in corpus:
		line = corpus.next()
		
		temp = "\""
		ita = ""
		# inglese
		
		words = line.split(" ")

		# italiano con {}
		line = corpus.next()
		
		words_stemm = stemmer.stemWords(words)
		target_word_stemm = stemmer.stemWord(target_word)
	
		if target_word_stemm in words_stemm:
			for word in words:
				word = (word.strip()).translate(table, string.punctuation)
				if word != "" and word != " " and stemmer.stemWord(word) != target_word_stemm:
					temp = temp + word + " "
			
			print temp
			ita_tuples = re.findall(p, line)
			print ita_tuples
			for ita_tuple in ita_tuples:
				if str(words_stemm.index(target_word_stemm)+1) in " " + str(ita_tuple[1]).strip() + " ":
					print words_stemm.index(target_word_stemm)
					print str(ita_tuple[1]).strip()
					print ita_tuple[0].strip()
					ita = (ita_tuple[0].strip()).translate(table, string.punctuation)
					if ita != "" and ita != " ":
						temp = temp.strip() + "\"" + "," + ita
						toWrite = True
					else:
						toWrite = False
					break
			print ita_tuple
			print ita
			print toWrite
			print temp
			if toWrite:
				files[target_word].write(temp + "\n")
	
	files[target_word].close()
	corpus.close()
