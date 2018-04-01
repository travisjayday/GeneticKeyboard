#!/bin/bash
rm books/*
for i in {1..200}
do
	wget http://gutenberg.org/files/$i/$i.txt -P books/ 
	cat books/$i.txt >> books/mainbook.txt
	rm books/$i.txt
done
