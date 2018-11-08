# The Keyboard Problem
Every computer nowadays uses the QWERTY Keyboard Layout... which is, in fact, surprisingly inefficient. QWERTY was designed for typewriters that frequently jammed to prevent typists from typing _too_ fast. The problem: how can we find a faster keyboard layout? 

# The Genetic Keyboard Solution
This project aims to design a layout faster than QWERTY by using a Genetic Algorthm to optimize the layout. By treating a keyboard like an individual within a population, and letting the fittest reproduce over successive generations, the algorithm will eventually converge to a near-optimal keyboard layout. 

# Evaluating the fitness (effectivness) of a Keyboard
To gauge the relative effectiveness, or fitness, of a keyboard class, typing text on the keyboard is simulated with Finger motion events by moving from key to key. The sum of the time it takes to type the text is the fitness, and the smaller the fitness, the faster the keyboard layout. So in general, the fitness for a keyboard object, k, given a text T that consists of n(T) characters and T_i is the i_th character, is 

![equation](http://latex.codecogs.com/gif.latex?f%28k%2CT%29%3D%5Csum%5E%7Bn%28T%29-1%7D_%7Bi%3D1%7DeventTime%28k%2C%20T_i%2CT_%7Bi&plus;1%7D%29)

Where eventTime(k, c1, c2) is the function that returns the time (or weight) it takes to move a finger from character c1 to character c2 on the keyboard layout k. 

# The Algorithm
The Genetic Algorithm intends to find an individual, i, that minimizes the function, f(i, T), for some text, T. For computing the fitness function, over 10mb (10 million characters) of English short stories, books, poetry, and political documents from Project Gutenberg were used. 

```
I. Initialize random population of size N
II. Allocate a nextGen List
III. Copy the S Elite individuals from pastGen into nextGen
IV. For the remaining (N-S) individuals, 
	A. Rank each individual and conduct Rank Selection to select two Parents
	B. Crossover Parents to create Child
	C. Child has P% chance of mutating
		1. If mutating, each key has M% chance of being swapped with another key
		2. If not mutating, continue
	D. Place Child into nextGen
	E. Repeat step IV. until the size of nextGen equals N
V. Plot fittest individuals in nextGen
VI. Set pastGen equal to nextGen. 
VII. Go to step II until it has been repeated G times.


Variable 	Definition			Example
------------------------------------------------------------
N		Population Size			800
S		Elitist Survival Size		80
P		Mutation Probability		10%
M		Mutation Rate			2%
G		Number of Generations		60
```
# Source Code
The implementation of the above algorithm is found in /GeneticKeyboard/src/main/java/com/tziegler/keyboard/PopulationManager.java

# Thanks
A special thanks to my IB school for allowing me to turn this project into my Extended Essay, and a thank you to my Physics teacher, Mr. Braden for mentoring my EE. 
