package com.tziegler.keyboard;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Keyboard {
	
	static boolean DEBUG = false;
	static boolean BENCHMARK = false;
	static final int NUM_KEYS = 38; 
	static final int NUM_SPECIALKEYS = 5; 
	static final int NUM_ABCKEYS = 33; 
	long execTime; 

	/*final Key[] qwertyKeys = {
		new Key('q'), new Key('a'), new Key('z'),
		new Key('w'), new Key('s'), new Key('x'),
		new Key('e'), new Key('d'), new Key('c'),
		new Key('r'), new Key('f'), new Key('v'),
		new Key('t'), new Key('g'), new Key('b'),
		new Key('y'), new Key('h'), new Key('n'),
		new Key('u'), new Key('j'), new Key('m'),
		new Key('i'), new Key('k'), new Key(',', '<'),
		new Key('o'), new Key('l'), new Key('.', '>'),
		new Key('p'), new Key(';', ':'), new Key('(', '['), 
		new Key(')', ']'), new Key('\'', '"'), new Key('/', '?')
	};*/

	final Key[] qwertyKeys = {
		new Key('q'), new Key('w'), new Key('e'),
		new Key('r'), new Key('t'), new Key('y'),
		new Key('u'), new Key('i'), new Key('o'),
		new Key('p'), new Key('('), new Key(')'), // leaving out []
		new Key('a'), new Key('s'), new Key('d'),
		new Key('f'), new Key('g'), new Key('h'),
		new Key('j'), new Key('k'), new Key('l'),
		new Key(';', ':'), new Key('\'', '"'), new Key('z'),
		new Key('x'), new Key('c'), new Key('v'),
		new Key('b'), new Key('n'), new Key('m'), 
		new Key(',', '!'), new Key('.', '-'), new Key('/', '?') // replaced <, > with !, -
	};
	
	final Key[] dvorakKeys = {
			new Key('\'', '"'), new Key(',', '('), new Key('.', ')'),
			new Key('p'), new Key('y'), new Key('f'),
			new Key('g'), new Key('c'), new Key('r'),
			new Key('l'), new Key('/', '?'), new Key('!'), // leaving out []
			new Key('a'), new Key('o'), new Key('e'), new Key('u'),
			new Key('i'), new Key('d'), new Key('h'),
			new Key('t'), new Key('n'), new Key('s'),
			new Key('-', '_'), new Key(';', ':'), new Key('q'),
			new Key('j'), new Key('k'), new Key('x'),
			new Key('b'), new Key('m'), new Key('w'), 
			new Key('v'), new Key('z') // replaced <, > with !, -
		};
	
	final int[] qwertySpecialKeys = {
		21, 22, 32, 31, 30
	};
	
	final int[] dvorakSpecialKeys = {
			0, 10, 22, 23, 1, 2
	};
	
	// stores the indexes of the keyboard keys in abc order. For example:
	// alphabetIndex		keyboardIndex
	// 0	(a) 			12
	// 1	(b)				27
	// 2 	(c)				25
	// ...
	// 25	(z)				5
	// 26	(!) 
	// 27	(") 
	// 28 	(')
	// 29	(()
	// 30	())
	// 31	(,)
	// 32 	(-)
	// 33 	(.)
	// 34 	(/)
	// 35	(:)
	// 36 	(;)
	// 37 	(?)
	public int[] abcToKeyIndex; 
	int[] specialKeys; // indexes for keys array that have shift entries
	
	private Key[] keys; 
	
	Integer fitness = null; 
	
	Keyboard() {
		keys = dvorakKeys; 
		specialKeys = dvorakSpecialKeys;
		/*keys = dvorakD; 
		specialKeys = qwertySpecialKeys; */
		
		abcToKeyIndex = new int[NUM_KEYS]; 
	}
	
	Keyboard(boolean random) {
		this(); 
		if (random) 
			shuffleKeys(); 
	}
	
	void setKeysDvorak() {
		keys = dvorakKeys; 
		specialKeys = dvorakSpecialKeys;
		populateAbcToIndex();
	}
	
	void setKeysQWERTY() {
		keys = qwertyKeys;
		specialKeys = qwertySpecialKeys; 
		populateAbcToIndex();
	}
	
	public void setKey(int i, Key k) {
		keys[i] = k; 
		fitness = null; 
	}
	
	public Key getKey(int i) {
		return keys[i];
	}
	
	public Key[] getKeys() {
		return keys; 
	}
	
	public Keyboard clone() {
		Keyboard myClone = new Keyboard(); 
		int  i= 0; 
		for (Key k : keys) {
			myClone.keys[i++] = k.clone(); 
		}
		i = 0; 
		for (int k : abcToKeyIndex) {
			myClone.abcToKeyIndex[i++] = k; 
		}
		i = 0; 
		for (int k : specialKeys) {
			myClone.specialKeys[i++] = k; 
		}
		myClone.fitness = fitness; 
		
		return myClone; 
	}
	
	// Implementing Fisher–Yates shuffle
	// to randomly shuffle keys and special keys
	// returns the execution time of the function in ns
	long shuffleKeys() {
		long tstart = System.nanoTime();
		Key a; 
		int index; 
		Random rnd = ThreadLocalRandom.current();
		fitness = null; 
		
		// shuffle non-special keys
		for (int i = NUM_ABCKEYS - 1; i > 0; i--) {
			index = rnd.nextInt(i + 1);
			// Simple swap
			a = keys[index];
			keys[index] = keys[i];
			keys[i] = a;
			// look for symbol keys and add them to symbol array
		}
		
		int j = 0; 
		// iterate through all keys, store special key indeces
		for (int i = 0; i < NUM_ABCKEYS;  i++) {
			if (keys[i].hasShift())
				specialKeys[j++] = i;
		}
		
		// shuffle special keys
		char c; 
		for (int i = j-1; i > 0; i--) {
			index = rnd.nextInt(i + 1);
			// Simple swap
			c = keys[specialKeys[index]].getShifted();
			keys[specialKeys[index]].setShifted(keys[specialKeys[i]].getShifted());
			keys[specialKeys[i]].setShifted(c);
			// look for symbol keys and add them to symbol array
		}
		
		populateAbcToIndex(); 
		
		long tend = System.nanoTime();
		return tend - tstart; 
	}
	
	public void graphicsShow() {
		GraphicKeyboard.showKeyboard(keys);
	}
	
	public void graphicsShow(String title, boolean newWin) {
		GraphicKeyboard.showKeyboard(keys, title, newWin);
	}
	
	// returns the key that holds the ascii character c
	int asciiToIndex(char c) {
		if (c >= 'a' && c <= 'z') 
			return abcToKeyIndex[c - 'a'];
		else if (c > 'A' && c < 'Z')
			return abcToKeyIndex[c - 'A'];
		
		// hard coded defaults for qwerty keyboard
		if (c == '[') return 10; 
		else if (c == ']') return 11; 
		
		switch (c) {
		case '!':
		case '"':
			return abcToKeyIndex[c - 33 + 26];
		case '\'':
		case '(':
		case ')':
			return abcToKeyIndex[c - 39 + 26 + 2];
		case ',':
		case '-':
		case '.':
		case '/':
			return abcToKeyIndex[c - 44 + 26 + 2 + 3];
		case ':':
		case ';':
			return abcToKeyIndex[c - 58 + 26 + 2 + 3 + 4];
		case '?':
			return abcToKeyIndex[c - 63 + 37];
		default:
			//DSystem.out.print("char not recognized");
			return 0;
		}
	}
	
	// sets character in abcToIndex array to given keyboard index i
	// refer to ascToKeyIndex comment on top with example table
	void updateAbcToIndex(char c, int i) {
		// abc
		if (c >= 'a' && c <= 'z') {
			abcToKeyIndex[c - 'a'] = i;
			return;
		}
		// symbols
		switch (c) {
		case '!':
		case '"':
			abcToKeyIndex[c - 33 + 26] = i; 
			return;
		case '\'':
		case '(':
		case ')':
			abcToKeyIndex[c - 39 + 26 + 2] = i;
			return;
		case ',':
		case '-':
		case '.':
		case '/':
			abcToKeyIndex[c - 44 + 26 + 2 + 3] = i;
			return;
		case ':':
		case ';':
			abcToKeyIndex[c - 58 + 26 + 2 + 3 + 4] = i;
			return;
		case '?':
			abcToKeyIndex[c - 63 + 37] = i; 
		}
	}
	
	void populateAbcToIndex() {
		// populate lookup table
		for (int i = 0; i < 33; i++) {
			updateAbcToIndex(keys[i].getMainChar(), i);
		
			if (keys[i].hasShift()) {
				updateAbcToIndex(keys[i].getShifted(), i);
			}
		}
	}
	
	// determines if a character key press needs shifting
	// like for symbol key, for example
	private boolean needShift(char c) {
		// uppercase
		if (c >= 'A' && c <= 'Z')
			return true; 
		
		// try to bypass having to iterate through for loop
		else if (c >= 'a' && c < 'z')
			return false;
		
		// check each shifted value of special keys against input c
		for (int i = 0; i < specialKeys.length; i++) {
			if (keys[specialKeys[i]].getShifted() == c) 
				return true;
		}
		return false;
	}
	
	// returns the relative fitness of the keyboard, given a text
	// manager that stores various english texts.
	public int computeFitness(TextManager manager) {
		if (BENCHMARK)  {
			execTime = System.currentTimeMillis();
		}
		
		Fingers fingers = new Fingers(); 
		//byte[] text = "My name is Jeff.".getBytes();
		byte[] text = manager.getBook();
		
		if (DEBUG) {
			System.out.println("------------[char]--------");
			System.out.println("[finger] action  [from] -> [to]");
		}
		
		long totalTime = 0; 
		int dtime = 0; 
		char c; 
		for (byte b : text) {
			c = (char) b;
			
			if (DEBUG) System.out.println("------------[" + c + "]--------");
	
			dtime = fingers.getMotionEventTime(asciiToIndex(c), needShift(c), c);
			
			if (DEBUG) System.out.println("    dt: \t" + dtime + "ms");
			
			totalTime += dtime; 
		}
		
		if (BENCHMARK) {
			System.out.println(String.format("keyboard fitness: %3.2es", totalTime / 1000.0));
			//System.out.println("keyboard fitness: " +  + "s");
			System.out.println("benchmark execution time: " + (System.currentTimeMillis() - execTime) + "ms");
		}
		
		return (int)(totalTime / 1000.0); 
	}
	
	// computes or returns the fitness of keyboard
	public Integer getFitness(TextManager manager) {
		if (fitness == null)
			fitness = computeFitness(manager); 
		
		return fitness; 
	}
	
	public Integer getFitness() {
		if (fitness == null)
			throw new java.lang.RuntimeException("Error: Requested Fitness without pre-computing it!"); 
		return fitness; 
	}
}
