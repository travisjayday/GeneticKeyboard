package com.tziegler.keyboard;

import java.awt.List;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PopulationManager {

	static final boolean DEBUG = false;

	int populationSize = 500; 	// number of keyboard in a population (P)
	
	int survivalSize = 150; 	// default: 30% of P. determines how many 
								// unaletered species make it to next gen. (S)
	
	int mutationRate = populationSize - survivalSize; // determines how many 
								// keyboards will be mutants in the next gen. (M)
	
	static final double CROSSOVER_PROB = 0.2	; 
	static final double MUTATION_PROB = 0.2; // likelihood of invoking the mutation operator
	static final double MUTATION_RATE = 0.05; // likelihood of a gene changing
	
	Map<Keyboard, Double> popList; 

	TextManager textManager;
	int leastFittestInGen = 0;
	Keyboard mostFittestInGen;//= Integer.MAX_VALUE; 
	
	public void initPopulation() {
		System.out.println("populationSize: " + populationSize);
		System.out.println("survivalSize: " + survivalSize);
		System.out.println("crossover prob: " + CROSSOVER_PROB);
		System.out.println("mutation prob: " + MUTATION_PROB);
		System.out.println("mutation rate: " + MUTATION_RATE);
		// init text
		textManager = new TextManager(); 
		textManager.loadBook(); 
		
		popList = new LinkedHashMap<Keyboard, Double>(); 
		
		mostFittestInGen = new Keyboard(true); 
		popList.put(mostFittestInGen, (double) mostFittestInGen.getFitness(textManager)); 
		
		for (int i = 0; i < populationSize; i++) {
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
		int gen = 0; 
		while (gen < 20) {
		//	AtomicReference<Double> fitnessSum0 = new AtomicReference<>(0.0);
			
		//	popList.replaceAll((k, v) -> v = (leastFittestInGen - v));
		//*	popList.forEach((k, v) -> fitnessSum0.accumulateAndGet(v, (x, y) -> x + y )); 
		//*	popList.replaceAll((k, v) -> v = v / fitnessSum0.get());
			
			//popList.forEach((k, v) -> fitnessSum1.accumulateAndGet(v, (x, y) -> x + y )); 
			
			// sort list from small to big, keeping only the limit(n) smallest entries
			popList = popList.entrySet().stream()
		        .sorted(Map.Entry.<Keyboard, Double>comparingByValue())
		     //   .limit(survivalSize)   
		        .collect(Collectors.toMap(
		        		Map.Entry::getKey, 
		        		Map.Entry::getValue, 
		        		(k, v) -> k,
		        		LinkedHashMap::new));
			
			double inc = 1 / (double)populationSize; 
			int i = 0; 
			//sortedMap.replaceAll((k, v) -> v = (++i) * inc);
			
			for (Map.Entry<Keyboard, Double> e : popList.entrySet()) {
				e.setValue(++i * inc); 
			}
			
			mostFittestInGen = popList.entrySet().iterator().next().getKey(); 
			plotter.addFittestInGen(mostFittestInGen.getFitness());
			
			if (DEBUG) {
				System.out.println("Population: \n");
				popList.forEach((k, v) -> System.out.println(k + " : "+ v));
			//	System.out.println("\nElites: \n");
			//	p.forEach((k, v) -> System.out.println(k + " : "+ v));
			}
			
			
			
			System.out.println("\nGeneration: " + gen);
			System.out.println("least fittest: " + leastFittestInGen);
			System.out.println("best fitness: " + mostFittestInGen.getFitness());
	
			LinkedHashMap<Keyboard, Double> nextGen = new LinkedHashMap<>(populationSize + 1);
			
			//sortedMap.forEach((k, v) -> nextGen.put(k, v));  
			Iterator<Map.Entry<Keyboard, Double>> it = popList.entrySet().iterator(); 
			for (i = 0; i < survivalSize; i++) {
				Map.Entry<Keyboard, Double> e = it.next();
				nextGen.put(e.getKey(), (double)e.getKey().getFitness(textManager)); 
			}
			
			System.out.println("nextgen size: " + nextGen.size());
			// fill remaining gen with new bois
			while (nextGen.size() < populationSize) {
				
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
				//	System.out.println("cumuSum: " + cumuSum);
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
				
				if (Math.random() <= MUTATION_PROB) {
					mutate(son); 
				}
				
				int fit = son.getFitness(textManager); 
				nextGen.put(son, (double)fit); 
				
			/*	if (fit > leastFittestInGen)
					leastFittestInGen = fit; 
				if (fit < mostFittestInGen)
					mostFittestInGen = fit; */
			}
			
			popList = nextGen; 
			gen++; 
			mostFittestInGen.graphicsShow();
			plotter.plotFittestInGen();
		}

	}
	
	public void mutate(Keyboard board) {
		for (int i = 0; i < Keyboard.NUM_ABCKEYS; i++) {
			if (Math.random() < MUTATION_RATE) {
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
			if (Math.random() < CROSSOVER_PROB) {
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
				
				/*// update abcToKeyIndex lookup table
				father.updateAbcToIndex(motherKey.getMainChar(), i); 
				father.updateAbcToIndex(motherKey.getShifted(), i); 
				
				father.updateAbcToIndex(fatherKey.getMainChar(), fatherKeyIndex); 
				father.updateAbcToIndex(fatherKey.getShifted(), fatherKeyIndex); 
				
				mother.updateAbcToIndex(fatherKey.getMainChar(), i); 
				mother.updateAbcToIndex(fatherKey.getShifted(), i); 
				
				mother.updateAbcToIndex(motherKey.getMainChar(), motherKeyIndex);
				mother.updateAbcToIndex(motherKey.getShifted(), motherKeyIndex);*/
				
			}
		}
		
		// Crossover special keys
		/*for (int i = 0; i < Keyboard.NUM_SPECIALKEYS; i++) {
			if (Math.random() < CROSSOVER_PROB) {
				char fatherKey = father.keys[i].getShifted(); 
				char motherKey = mother.keys[i].getShifted();
				int fatherKeyIndex = father.asciiToIndex(motherKey); 
				int motherKeyIndex = mother.asciiToIndex(fatherKey); 
				
				// switch primary keys at i 
				father.keys[i].setShifted(motherKey); 	// set father to mother at i 
				mother.keys[i].setShifted(fatherKey); 	// set mother to father at i 
				
				// exchange other keys so that no duplicates remain
				father.keys[fatherKeyIndex].setShifted(fatherKey); 	// 
				mother.keys[motherKeyIndex].setShifted(motherKey); 
				
				// update abcToKeyIndex lookup table
				father.populateAbcToIndex(motherKey, i); 
				father.populateAbcToIndex(fatherKey, fatherKeyIndex); 
				
				mother.populateAbcToIndex(fatherKey, i); 
				mother.populateAbcToIndex(motherKey, motherKeyIndex);
			}
		}*/
		
		return father; 
	}
}
