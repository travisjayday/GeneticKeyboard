package com.tziegler.keyboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextManager {
	byte[] book; 
	
	TextManager(String fname) {
		byte[] fileString;
		try {
		//	fileString = Files.readAllBytes(Paths.get("data/books/mainbook.txt"));
			System.out.println("Reading from " + fname); 
			fileString = Files.readAllBytes(Paths.get(fname));
			book = fileString; 
			//book = "hello".getBytes();  	
			//System.out.println("Contents (Java 7 with character encoding ) : " + fileString);		      
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	byte[] getBook() {
		return book;
	}
}
