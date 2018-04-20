package com.tziegler.keyboard;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EvolutionParams {
	public int POP_SIZE = 700; 	// number of keyboard in a population (P)
	
	public int SURVIVAL_SIZE = 100; 	// default: 30% of P. determines how many 
								// unaletered species make it to next gen. (S)
	public double CROSSOVER_PROB = 0.5	; 
	public double MUTATION_PROB = 0.08; // likelihood of invoking the mutation operator
	public double MUTATION_RATE = 0.2; // likelihood of a gene changing
	public int GENERATIONS = 40; 
	
}
