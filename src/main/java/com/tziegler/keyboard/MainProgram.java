package com.tziegler.keyboard;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
import com.tziegler.keyboard.datacollector.DataMotionEvents;
import com.tziegler.keyboard.datacollector.MotionEventAnalyzer;
import com.tziegler.keyboard.graphics.EvolutionPlotter;
import com.tziegler.keyboard.graphics.GraphicKeyboard;
import com.tziegler.keyboard.graphics.MotionEventPlot;

import sun.misc.GC;

public class MainProgram {

/*	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//System.load(new java.io.File("path/EntityFramework.dll").getAbsolutePath());
	}*/
	
	/*private static void loadLibrary() {
	    try {
	        InputStream in = null;
	        File fileOut = null;
	        String osName = System.getProperty("os.name");
	        System.out.println("OS: " + osName + " " + System.getProperty("sun.arch.data.model") + " bit");
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
	       
	        System.out.println("Loading lib from " + fileOut.getAbsolutePath() + " size: " + fileOut.length());
	        TimeUnit.SECONDS.sleep(5);
	        System.load("/tmp/lib7303705437850547593.so");
	        


	    } catch (Exception e) {
	        throw new RuntimeException("Failed to load opencv native library", e);
	    }
	}*/
	
	private static void loadOpencv() {
	    try {
	        String osName = System.getProperty("os.name");
	        System.out.println("OS: " + osName + " " + System.getProperty("sun.arch.data.model") + " bit");
	        File opencvDir = new File("res/opencv"); 
	        if (!opencvDir.exists()) {
	        	throw new Exception("Cannot find opencv folder!");
	        }
	        if(osName.startsWith("Windows")) {
                System.out.println("Loading OpenCV from: opencv/win/opencv_java320.dll");
                System.load(opencvDir.getAbsolutePath() + "\\win\\opencv_java320.dll");
	        }
	        else if(osName.startsWith("Linux")){
	        	System.out.println("Loading OpenCV from: opencv/linux/opencv_java320.so");
	        	System.load(opencvDir.getAbsolutePath() + "/linux/libopencv_java320.so");
	        }
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to load opencv native library", e);
	    }
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		loadOpencv();
		GraphicKeyboard.motionDurationBasedOnDistances();
		
		while (true) { 
			System.out.println("what do you want to do?");
			System.out.print("1: run evolution"
					+ "\n2: run brute force"
					+ "\n3: train empirical data"
					+ "\n4: set motion event empirically"
					+ "\n5: set motion event times by distance (default)"
					+ "\n6: cross-over example"
					+ "\n7: mutation example");
			
			Scanner in = new Scanner(System.in); 
			int i = in.nextInt(); 
			
			switch (i) {
			case 1: 
				TextManager txt = new TextManager(); 
				txt.loadBook();
				Keyboard qwertyBoard = new Keyboard(); 
				qwertyBoard.setKeysQWERTY();
				System.out.println("Qwerty Fitness: " + qwertyBoard.getFitness(txt));
				
				Keyboard dvorakBoard = new Keyboard();
				dvorakBoard.setKeysDvorak();
				System.out.println("Dvorak Fitness: " + dvorakBoard.getFitness(txt));
				
				System.out.println("Starting evolution");
				PopulationManager manager = new PopulationManager(); 
				manager.initPopulation(); 
				manager.runGeneration();
				
				break;
			case 2:
				EvolutionPlotter plot = new EvolutionPlotter(); 
				System.out.println("Brute forcing...");
				Keyboard board = new Keyboard(); 
				TextManager man = new TextManager(); 
				man.loadBook();
				man.getBook(); 
				int fit = 900000;
				int bestFit = Integer.MAX_VALUE;
				long genStartTime; 
				while (fit > 24500) {
					genStartTime = System.currentTimeMillis(); 
					board.shuffleKeys();
					fit = board.getFitness(man);
					board.graphicsShow();
					if (fit < bestFit) {
						System.out.println("Fittest: " + bestFit + "  \t| " 
								+ (System.currentTimeMillis() - genStartTime) / 1000.0 + " keyboards per second");
						bestFit = fit; 
						plot.addFittestInGen(bestFit);
						plot.plotFittestInGen(false);
					}
				}
				board.graphicsShow();
				break;
				
			case 3: 
				MotionEventAnalyzer an = new MotionEventAnalyzer(); 
				an.beginTest();
				break;
			case 4: 
				MotionEventPlot.motionDurationBasedEmpiricalEvents(MotionEventPlot.loadAllMotionEvents(true));
				System.out.println("Motion events are based on trained data");
				Fingers.printMotionEventTimes();
				break;
			case 5: 
				GraphicKeyboard.motionDurationBasedOnDistances();
				Fingers.printMotionEventTimes();
				MotionEventPlot p = new MotionEventPlot(); 
				p.displayFingerMotionEvents();
				break;
			case 6: 
				Keyboard mum = new Keyboard(true); 
				Keyboard dad = new Keyboard(true); 
				
				mum.graphicsShow("mum", true);
				dad.graphicsShow("dad", true); 
				
				PopulationManager mg = new PopulationManager(); 
				mg.evoParams = new EvolutionParams(); 
				mg.evoParams.CROSSOVER_PROB = 0.2;
				mg.reproduce(dad, mum).graphicsShow("son", true); 
				
				break;
			case 7: 
				
				PopulationManager mg2 = new PopulationManager(); 
				mg2.evoParams = new EvolutionParams(); 
				mg2.evoParams.MUTATION_RATE = 0.03;
				Keyboard normal = new Keyboard(true); 
				normal.graphicsShow("before", true);
				mg2.mutate(normal);
				normal.graphicsShow("mutated", true);
				
				break;
			}
		}
		//System.load("/tmp/lib7503263439452856737.so");
		//DataMotionEvents ev = new DataMotionEvents(); 
		
		
	//	MotionEventPlot plot = new MotionEventPlot(""); 
	//	plot.displayAllMotionEvents();
	//	plot.displayMotionEvent(ev);
		
	

		
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
		
		//MotionEventAnalyzer analyzer = new MotionEventAnalyzer(); 
		//analyzer.beginTest();
		
		/* DEMO GENERATION EVOLUTION  */

		
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
