package com.tziegler.keyboard;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EvolutionParams {
	public int POP_SIZE = 10; 	// number of keyboard in a population (P)
	
	public int SURVIVAL_SIZE = 2; 	// default: 30% of P. determines how many 
								// unaletered species make it to next gen. (S)
	public double CROSSOVER_PROB = 0.5	; 
	public double MUTATION_PROB = 0.02; // likelihood of invoking the mutation operator
	public double MUTATION_RATE = 0.1; // likelihood of a gene changing
	public int GENERATIONS = 2; 
	public String BOOK_STRING = "res/books/short.txt";
	
}
