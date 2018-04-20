package com.tziegler.keyboard;

import java.awt.List;
import java.io.File;
import java.io.FileOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import com.tziegler.keyboard.datacollector.DataMotionEvents;
import com.tziegler.keyboard.datacollector.MotionEventAnalyzer;
import com.tziegler.keyboard.graphics.EvolutionPlotter;

public class PopulationManager {

	static final boolean DEBUG = false;

	int mutationSize; // determines how many 
								// keyboards will be mutants in the next gen. (M)
	Map<Keyboard, Double> popList; 

	TextManager textManager;
	int leastFittestInGen = 0;
	Keyboard mostFittestInGen;//= Integer.MAX_VALUE; 
	EvolutionParams evoParams; 
	long startTime = 0; 
	
	public void initPopulation() {
		//evoParams = new EvolutionParams(); 
		File params = new File("EvoParams.xml");
		
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(EvolutionParams.class);
		    Unmarshaller m = context.createUnmarshaller(); 
		    
		    System.out.println("Reading from: " + params.getAbsolutePath());
		    if (!params.exists()) { 
		    	evoParams = new EvolutionParams(); 
			    Marshaller ma = context.createMarshaller();
			    ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				   
			    System.out.println("EvoParams.xml not found, writing defaults to: " + params.getAbsolutePath());
				    
				params.createNewFile();
				FileOutputStream out = new FileOutputStream(params); 
				    
				ma.marshal(evoParams, out);
		    }
		    else {
		    	evoParams = (EvolutionParams) m.unmarshal(params); 
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		startTime = System.currentTimeMillis(); 
		System.out.println("Timestamp Start: " + System.currentTimeMillis());
		System.out.println("populationSize: " + evoParams.POP_SIZE);
		System.out.println("survivalSize: " + evoParams.SURVIVAL_SIZE);
		System.out.println("crossover prob: " + evoParams.CROSSOVER_PROB);
		System.out.println("mutation prob: " + evoParams.MUTATION_PROB);
		System.out.println("mutation rate: " + evoParams.MUTATION_RATE);
		// init text
		textManager = new TextManager(); 
		textManager.loadBook(); 
		
		popList = new LinkedHashMap<Keyboard, Double>(); 
		
		mostFittestInGen = new Keyboard(true); 
		popList.put(mostFittestInGen, (double) mostFittestInGen.getFitness(textManager)); 
		
		for (int i = 0; i < evoParams.POP_SIZE; i++) {
			Keyboard b = new Keyboard(true); // random keyboard
			int fit = b.getFitness(textManager);
			popList.put(b, (double)fit);
			
			if (fit > leastFittestInGen)
				leastFittestInGen = fit; 
			if (fit < mostFittestInGen.getFitness()) 
				mostFittestInGen = b; 
		}
	
	}
	
	public void runGeneration() {
		EvolutionPlotter plotter = new EvolutionPlotter();
		long genStartTime = System.currentTimeMillis(); 
		int gen = 0;
		while (gen < evoParams.GENERATIONS) {
	
			// sort list from small to big, keeping only the limit(n) smallest entries
			popList = popList.entrySet().stream()
		        .sorted(Map.Entry.<Keyboard, Double>comparingByValue())
		     //   .limit(survivalSize)   
		        .collect(Collectors.toMap(
		        		Map.Entry::getKey, 
		        		Map.Entry::getValue, 
		        		(k, v) -> k,
		        		LinkedHashMap::new));
			
			double inc = 1 / (double)evoParams.POP_SIZE; 
			int i = 0; 
			//sortedMap.replaceAll((k, v) -> v = (++i) * inc);
			OptionalDouble avg = popList.entrySet().stream().mapToInt(a -> (int)(double)a.getValue()).average(); 
			
			for (Map.Entry<Keyboard, Double> e : popList.entrySet()) {
				e.setValue(++i * inc); 
			}
			
			mostFittestInGen = popList.entrySet().iterator().next().getKey(); 
			plotter.addFittestInGen(mostFittestInGen.getFitness());
			plotter.addAvgFitInGen((int)avg.getAsDouble()); 
			
			if (DEBUG) {
				System.out.println("Population: \n");
				popList.forEach((k, v) -> System.out.println(k + " : "+ v));
			//	System.out.println("\nElites: \n");
			//	p.forEach((k, v) -> System.out.println(k + " : "+ v));
			}
			
			
	
			System.out.println("\nGeneration: " + gen);
			System.out.println("least fittest: " + leastFittestInGen);
			System.out.println("best fitness: " + mostFittestInGen.getFitness());
			System.out.println("avg fitness: " + avg);
		
			double dTime = (System.currentTimeMillis() - genStartTime) / 1000.0; 
			System.out.println("performance: " + evoParams.POP_SIZE / dTime + " keyboards per second");
			
			genStartTime = System.currentTimeMillis();
			LinkedHashMap<Keyboard, Double> nextGen = new LinkedHashMap<>(evoParams.POP_SIZE + 1);
			
			//sortedMap.forEach((k, v) -> nextGen.put(k, v));  
			Iterator<Map.Entry<Keyboard, Double>> it = popList.entrySet().iterator(); 
			for (i = 0; i < evoParams.SURVIVAL_SIZE; i++) {
				Map.Entry<Keyboard, Double> e = it.next();
				nextGen.put(e.getKey(), (double)e.getKey().getFitness(textManager)); 
			}
			
			System.out.println("nextgen size: " + nextGen.size());
			// fill remaining gen with new bois
			while (nextGen.size() < evoParams.POP_SIZE) {
				
				if (DEBUG) System.out.println("Populating gen");
				// roulette selection 
				double randFather = Math.random();
				double randMother = Math.random(); 
				
				Keyboard mother = null, father = null, son = null; 
	
				double cumuSum = 0.0;
				for (Map.Entry<Keyboard, Double> entry : popList.entrySet()) {
					if (cumuSum > randFather) {
						father = entry.getKey();
						//System.out.println("found father: " + entry.getKey());
						if (mother != null) break;
					}
					if (cumuSum > randMother) {
						mother = entry.getKey(); 
						//System.out.println("found mother: " + entry.getKey());
						if (father != null) break;
					}
					
					cumuSum += entry.getValue(); 	// add fitness
				}
				
				
				if (mother == null || father == null) {
					if (DEBUG) System.out.println("skipping null parents");
					continue;
				}
				
				if (mother == father) {
					if (DEBUG) System.out.println("prevented incest, mother = " + mother);
					continue;
				}
				
				son = reproduce(father, mother); 
				
				if (Math.random() <= evoParams.MUTATION_PROB) {
					mutate(son); 
				}
				
				int fit = son.getFitness(textManager); 
				nextGen.put(son, (double)fit); 
			}
			
			popList = nextGen; 
			gen++; 
			mostFittestInGen.graphicsShow();
			plotter.plotFittestInGen(true);
		}
		System.out.println("Timestamp End: " + System.currentTimeMillis());
		System.out.println("Delta Time: " + (System.currentTimeMillis() - startTime) / (1000.0 * 60.0) + "minutes");
	}
	
	public void mutate(Keyboard board) {
		for (int i = 0; i < Keyboard.NUM_ABCKEYS; i++) {
			if (Math.random() < evoParams.MUTATION_RATE) {
				Key boardKey = board.getKey(i); 
				
				Key otherKey = //board.asciiToIndex(
						board.getKey((int)Math.round(Math.random() * (Keyboard.NUM_ABCKEYS -1)));//.getMainChar()); 
				int otherKeyIdx = board.asciiToIndex(otherKey.getMainChar()); 
				
				board.setKey(otherKeyIdx, boardKey); 
				board.setKey(i, otherKey); 

				// TODO: optimize this
				board.populateAbcToIndex(); 
			}
		}
	}

	// implements crossover
	public Keyboard reproduce(Keyboard fadder, Keyboard mudder) {
		
		if (fadder == mudder)
			System.out.println("Warning: Incest has occurred!");
		
		Keyboard father = fadder.clone(); 
		Keyboard mother = mudder.clone(); 
	/*	System.out.println("Mother keyboard index to key");
		for (char c = 'a'; c < 'z'; c++) 
			System.out.println(c + "  |  " + mother.asciiToIndex(c)); 
		}*/
		

		// copy a random amount of this keyboard to the child, 
		// then copy a random amount of the mother's keyboard
		// into this child.
		for (int i = 0; i < Keyboard.NUM_ABCKEYS; i++) {
			if (Math.random() < evoParams.CROSSOVER_PROB) {
				Key fatherKey = father.getKey(i); 
				Key motherKey = mother.getKey(i);
				
				int fatherKeyIndex = father.asciiToIndex(motherKey.getMainChar()); 
				int motherKeyIndex = mother.asciiToIndex(fatherKey.getMainChar()); 

				father.setKey(i, motherKey); 
				mother.setKey(i, fatherKey); 
				
				father.setKey(fatherKeyIndex, fatherKey);
				mother.setKey(motherKeyIndex, motherKey); 
				
				father.populateAbcToIndex(); 
				mother.populateAbcToIndex();
			}
		}
		return father; 
	}
}
