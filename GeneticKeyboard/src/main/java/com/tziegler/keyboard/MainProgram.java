package com.tziegler.keyboard;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;

public class MainProgram {

/*	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//System.load(new java.io.File("path/EntityFramework.dll").getAbsolutePath());
	}*/
	
	private static void loadLibrary() {
	    try {
	        InputStream in = null;
	        File fileOut = null;
	        String osName = System.getProperty("os.name");
	        System.out.println("OS: " + osName);
	        if(osName.startsWith("Windows")) {
                System.out.println("Loading OpenCV from: /opencv/win/opencv_java320.dll");
                in = MainProgram.class.getResourceAsStream("/opencv/win/opencv_java320.dll");
                fileOut = File.createTempFile("lib", ".dll");
	        }
	        else if(osName.startsWith("Linux")){
	        	System.out.println("Loading OpenCV from: opencv/linux/opencv_java320.so");
	            in = MainProgram.class.getResourceAsStream("/opencv/linux/libopencv_java320.so");
	            fileOut = File.createTempFile("lib", ".so");
	        }

	        byte[] buffer = new byte[in.available()]; 
	        in.read(buffer); 
	        OutputStream out = new FileOutputStream(fileOut); 
	        out.write(buffer);
	        in.close();
	        out.close();
	        System.load(fileOut.toString());

	    } catch (Exception e) {
	        throw new RuntimeException("Failed to load opencv native library", e);
	    }
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		loadLibrary();
		GraphicKeyboard.motionDurationBasedOnDistances();
	/*	Keyboard dvorak = new Keyboard(); 
		//Keyboard.DEBUG = true; 
		//dvorak.DEBUG = true;
		//dvorak.setKeysDvorak();
		dvorak.setKeysQWERTY();
		//	dvorak.shuffleKeys()
		TextManager man = new TextManager(); 
		man.loadBook();
		dvorak.getFitness(man); 
		dvorak.graphicsShow();*/
		
		/*	MotionEventAnalyzer analyzer = new MotionEventAnalyzer(); 
		analyzer.beginTest();
		
		/* DEMO GENERATION EVOLUTION */
		System.out.println("Starting evolution");
		PopulationManager manager = new PopulationManager(); 
		manager.initPopulation(); 
		manager.runGeneration();
		
	/*	Keyboard board = new Keyboard(true); 
		board.graphicsShow("before", true);
		for (int i = 0; i < 10; i++)
		manager.mutate(board);*/
		//board.graphicsShow("mutated", true); 
	//	manager.runGeneration();
		
		/* DEMO KEYGEN */
		/*TextManager man = new TextManager(); 
		man.loadBook();
		man.getBook(); 
	//	Keyboard board = new Keyboard(true); 
		int fit = 900000;
		while (fit > 24500) {
			board.shuffleKeys();
			fit = board.getFitness(man);
			board.graphicsShow();
		}
		board.graphicsShow();*/
		
		/*Keyboard mum = new Keyboard(true); 
		Keyboard dad = new Keyboard(true); 
		
		mum.graphicsShow("mum", true);
		dad.graphicsShow("dad", true); 
		
		manager.reproduce(dad, mum).graphicsShow("son", true); 
		
		// TODO Auto-generated method stub
		/*Mat j = Mat.ones(10, 10, 1);
		System.out.println(j);
		Keyboard board = new Keyboard();
		//board.graphicsShow(); 
		//board.shuffleKeys();
		board.graphicsShow();
		
		TextManager manager = new TextManager();
		manager.loadBook();
		board.computeFitness(manager);
		
		PopulationManager popManager = new PopulationManager(); 
		
		int fit = 900000;
		while (fit > 24500) {
			board.shuffleKeys();
			fit = board.computeFitness(manager);
			board.graphicsShow();
		}
		board.graphicsShow();
		
		Keyboard board1 = new Keyboard(true); 
		Keyboard board2 = new Keyboard(true); 
		
		board1.graphicsShow("father", true); 
		board2.graphicsShow("mother", true); 
		
		Keyboard board3 = popManager.reproduce(board1, board2); 
		
		board3.graphicsShow("son", true); 
		
	
		/*int iter = 10000000;
		long total = 0; 
		for (int i = 0; i < iter; i++) {
			total += board.shuffleKeys(); 
		}*/
		
		//System.out.println("Average time: " + total / (double) (iter) + "ns"); 
	}


}
