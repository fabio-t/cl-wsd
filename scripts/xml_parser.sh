#!/bin/bash

for i in `ls *.data`
do
	`cat $i | grep -E "<.+>.+<.+>.+<.+>" > prova.$i`
	`sed -e "s/[ \t]*<context>\(.*\)<head>\(.\+\)<\/head>\(.*\)<\/context>[ \t]*/\1 \3/g" prova.$i > $i.test`
done
