#!/bin/bash

outdir=../src/main/resources/books
name=short.txt

rm $outdir/*
for i in {1..20}
do
	wget http://gutenberg.org/files/$i/$i.txt -P $outdir/ 
	cat $outdir/$i.txt >> $outdir/$name
	rm $outdir/$i.txt
done
