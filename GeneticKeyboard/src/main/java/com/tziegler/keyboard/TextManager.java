package com.tziegler.keyboard;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextManager {
	byte[] book; 
	
	void loadBook() {
		byte[] fileString;
		try {
		//	fileString = Files.readAllBytes(Paths.get("data/books/mainbook.txt"));
			fileString = Files.readAllBytes(Paths.get(TextManager.class.getResource("/books/long.txt").toURI()));
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
