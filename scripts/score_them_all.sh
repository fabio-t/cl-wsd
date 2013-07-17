#!/bin/bash

# ./score.sh 

cd ../data/results

for l in "it" "fr" "de" "nl" "es"
do
	echo "========== $l =========="

	cd gold_${l}

	for t in "best" "oof"
	do
		echo "======= $t ======="

		cd $t

		rm averages
		rm max_avg

		for d in `ls -d *`
		do
			echo "===== $d ====="

			if [ -d $d ]; then
				cd $d

				pwd

				for i in `ls ../../*.txt`
				do
					echo $i

					../../../../../scripts/scorer2.pl `basename $i "_gold.txt"` ${i} -t $t
				done

				cat *.results | grep "^precision" | sed -e "s/^precision = \(.\+\), recall = \(.\+\)\s*$/\1;\2/" > single_scores

				wc -l single_scores

				../../../../../scripts/count.py

				cat average_score >> ../averages

				cd ..

			else
				echo "Error! $d doesn't exists! Exiting.."
				exit 1
			fi

		done

		../../../../scripts/max_avg.py

		cd ..
	done

	cd ..
done
