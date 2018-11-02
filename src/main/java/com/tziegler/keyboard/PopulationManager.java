package com.tziegler.keyboard;

import java.awt.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	public static EvolutionParams loadEvoParams() {
		File params = new File("EvoParams.xml");
		EvolutionParams staticEvoParams = null; 
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(EvolutionParams.class);
		    Unmarshaller m = context.createUnmarshaller(); 
		    
		    System.out.println("Reading from: " + params.getAbsolutePath());
		    if (!params.exists()) { 
		    	staticEvoParams = new EvolutionParams(); 
			    Marshaller ma = context.createMarshaller();
			    ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				   
			    System.out.println("EvoParams.xml not found, writing defaults to: " + params.getAbsolutePath());
				    
				params.createNewFile();
				FileOutputStream out = new FileOutputStream(params); 
				    
				ma.marshal(staticEvoParams, out);
		    }
		    else {
		    	staticEvoParams = (EvolutionParams) m.unmarshal(params); 
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return staticEvoParams;
	}
	
	public void printParams() {
		System.out.println("populationSize: " + evoParams.POP_SIZE);
		System.out.println("survivalSize: " + evoParams.SURVIVAL_SIZE);
		System.out.println("crossover prob: " + evoParams.CROSSOVER_PROB);
		System.out.println("mutation prob: " + evoParams.MUTATION_PROB);
		System.out.println("mutation rate: " + evoParams.MUTATION_RATE);
	}

	public void initPopulation(EvolutionParams p) {
		evoParams = p;
		startTime = System.currentTimeMillis(); 
		System.out.println("Timestamp Start: " + System.currentTimeMillis());
		printParams();
		
		// init text
		textManager = new TextManager(evoParams.BOOK_STRING); 
		
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
		
		// generation count
		int gen = 0;
		while (gen < evoParams.GENERATIONS) {
	
			// sort list from small to big
			popList = popList.entrySet().stream()
		        .sorted(Map.Entry.<Keyboard, Double>comparingByValue())
		        .collect(Collectors.toMap(
		        		Map.Entry::getKey, 
		        		Map.Entry::getValue, 
		        		(k, v) -> k,
		        		LinkedHashMap::new));
			
			// find average fitness
			OptionalDouble avg = popList.entrySet().stream().mapToInt(a -> (int)(double)a.getValue()).average(); 
			
			// add best and average fitnesses to be shown & plotted
			mostFittestInGen = popList.entrySet().iterator().next().getKey(); 
			mostFittestInGen.graphicsShow();
			plotter.addFittestInGen(mostFittestInGen.getFitness());
			plotter.addAvgFitInGen((int)avg.getAsDouble()); 
			plotter.plotFittestInGen(true, "" + startTime);
		
			// print debug info
			System.out.println("\nGeneration: " + gen);
			System.out.println("least fittest: " + leastFittestInGen);
			System.out.println("best fitness: " + mostFittestInGen.getFitness());
			System.out.println("avg fitness: " + avg.getAsDouble());
		
			// Benchmarking
			double dTime = (System.currentTimeMillis() - genStartTime) / 1000.0; 
			System.out.println("performance: " + evoParams.POP_SIZE + " keyboards in " + dTime + " seconds");
			genStartTime = System.currentTimeMillis();
			
			/**********************************************/
			// INITIALIZATION -- create next geneartion
			LinkedHashMap<Keyboard, Double> nextGen = new LinkedHashMap<>(evoParams.POP_SIZE + 1);
			
			// Below loop: start Rank selection and do Elitism
			double inc = 1 / (double)evoParams.POP_SIZE; 
			double sum = 0.0;
			Iterator<Map.Entry<Keyboard, Double>> it = popList.entrySet().iterator(); 
			
			// loop through population from fittest to least fittest
			for (int i = 0; it.hasNext(); i++) {
				Map.Entry<Keyboard, Double> entry = it.next();
				
				// ELITISM
				// if indivdual is within the top elitists, copy it directly to next gen
				if (i < evoParams.SURVIVAL_SIZE)
					nextGen.put(entry.getKey(), (double)entry.getKey().getFitness(textManager)); 
				
				// RANK SELECTION -- ASSIGNING SLIVER SIZES
				// rank individual and assign it probability of selection based on its rank
				// biggest sliver size of the roullette wheel corresponds to biggest probability 
				// of being selected. Max sliver = 1, min sliver = 1 / POP_SIZE
				double sliverSize = 1 - (i * inc); 
				sum += sliverSize;
				entry.setValue(sliverSize);
				//System.out.println("Entry fit: " + entry.getKey().getFitness(textManager) + " gets "  + sliverSize);
			}
			
			//System.out.println("Population:");
			//popList.forEach((k, v) -> System.out.println(k + " : "+ v));
			
			// fill remaining N - S population with new solutions (offspring)
			while (nextGen.size() < evoParams.POP_SIZE) {
				Keyboard mother = null, father = null, son = null; 
				
				// ROULLETE WHEEL SELECTION 
				// randFather and randMother are random positions on the roullete wheel
				double randFather = Math.random() * sum; // [0, 1)
				double randMother = Math.random() * sum; // [0, 1)
					
				double cumuSum = 0.0; // cumulative sum
				
				
				for (Map.Entry<Keyboard, Double> entry : popList.entrySet()) {

					//System.out.println("entryKey: " + entry.getKey() + "; entryVal: " + entry.getValue());

					cumuSum += entry.getValue(); 	// add fitness to cumusum
					//System.out.println("cumuSum: " + cumuSum);
					//System.out.println("randMother: " + randMother + "; randFather: " + randFather);
					if (cumuSum > randMother && mother == null)
						mother = entry.getKey(); 
					
					if (cumuSum > randFather && father == null) 
						father = entry.getKey(); 
					
					if (mother != null && father != null)
						break;
				}
					
				//System.out.println("selected Father: " + father + "; mother: " + mother);
				if (mother == father) {
					System.out.println("prevented incest, mother = " + mother);
					continue;
				}
				
				if ((mother == null) || (father == null)) {
					System.out.println("Warning: null parents! Continuing...");
					continue; 
				}
				
				// genetically cross father and mother
				son = reproduce(father, mother); 
				
				// child has random chance to be mutated
				if (Math.random() <= evoParams.MUTATION_PROB)
					mutate(son); 
				
				// put son into next gen
				nextGen.put(son, (double)son.getFitness(textManager)); 
			}
			
			popList = nextGen; 
			gen++; 
		}
		
		try {
			if (!Files.exists(Paths.get("./out")))
				Files.createDirectories(Paths.get("./out"));
			String fname = "./out/run_" + startTime;
			plotter.writeToFile(fname);
			System.out.println("Wrote data to: " + fname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		printParams();
		System.out.println("Timestamp Start: " + startTime);
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
	public Keyboard reproduce(Keyboard dad, Keyboard mom) {
		
		if (dad == mom)
			System.out.println("Warning: Incest has occurred!");
		
		// father and mother will exchange genetic material with each other,
		// so don't edit original
		Keyboard father = dad.clone(); 
		Keyboard mother = mom.clone(); 

		// copy a random amount of this keyboard to the child, 
		// then copy a random amount of the mother's keyboard
		// into this child.
		for (int i = 0; i < Keyboard.NUM_ABCKEYS; i++) {
			if (Math.random() < evoParams.CROSSOVER_PROB) {
				
				// get key objects at index i 
				Key fatherKey = father.getKey(i); 
				Key motherKey = mother.getKey(i);
				
				// get key indexes of where to switch keys to
				int fatherKeyIndex = father.asciiToIndex(motherKey.getMainChar()); 
				int motherKeyIndex = mother.asciiToIndex(fatherKey.getMainChar()); 

				// update first set of keys
				father.setKey(i, motherKey); 
				mother.setKey(i, fatherKey); 
				
				// update other set of keys
				father.setKey(fatherKeyIndex, fatherKey);
				mother.setKey(motherKeyIndex, motherKey); 
				
				// re-calculate key indexes for asciiToIndex()
				father.populateAbcToIndex(); 
				mother.populateAbcToIndex();
			}
		}
		
		// return one child
		return father; 
	}
}
