# The Keyboard Problem
Every computer nowadays uses the QWERTY Keyboard Layout... which is, in fact, surprisingly inefficient. QWERTY was designed for typewriters that frequently jammed to prevent typists from typing _too_ fast. The problem: how can we find a faster keyboard layout? 

# The Genetic Keyboard Solution
This project aims to design a layout faster than QWERTY by using a Genetic Algorthm to optimize the layout. By treating a keyboard like an individual within a population, and letting the fittest reproduce over successive generations, the algorithm will eventually converge to a near-optimal keyboard layout. 

# The Algorithm -- Pseudocode 
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


Variable	Definition				Example
-----------------------------------------------
N			Population Size			800
S			Elitist Survival Size	80
P			Mutation Probability	10%
M			Mutation Rate			2%
G			Number of Generations	60

```
