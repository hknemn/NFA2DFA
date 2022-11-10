# Conversion of NFA to DFA
***Theory of Computation | Regular Expression | Equivalence of NFAS and DFAS***

### Abbreviations
* Nondeterministic Finite Automata (NFA)
* Deterministic Finite Automaton (DFA)

### What's Idea?
The idea is to convert the NFA into an equivalent DFA that simulates the NFA.

### Equivalence of Automata
* Deterministic and nondeterministic finite automata recognize the same class of languages. 
* Such equivalence is both surprising and useful. 
* It is surprising because NFAs appear to have more power than DFAs, so we might expect that NFAs recognize more languages. 
* It is useful because describing an NFA for a given language sometimes is much easier than describing a DFA for that language.
* Say that two machines are equivalent if they recognize the same language.
* Every nondeterministic finite automaton has an equivalent deterministic finite automaton.

### Project Explanation
* An example txt file is given in the project.
* In the example txt file, a distinction is made with spaces and tab characters.
* The values in the δ transition function are separated from each other by the tab character.
* The number of states, the names of the states, the number of symbols in the alphabet and symbols may vary. The console application is dynamic to support them.
* The console application converts the NFA machine it reads from the txt file to DFA, deletes the unaffecting (unnecessary) states after conversion and prints the formal definition of the final DFA machine.
* It then asks the user to enter a sample string and prints whether the DFA machine accepts the user's string.

### Formal Definition of a Finite Automaton
A finite automaton is a 5-tuple (Q, Σ, δ, q0, F), where
1. Q is a finite set called the states,
2. Σ is a finite set called the alphabet,
3. δ: Q × Σ → Q is the transition function,
4. q0 ∈ Q is the start state, and
5. F ⊆ Q is the set of accept states.
