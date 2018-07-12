package coursework;

import java.util.ArrayList;
import java.util.Arrays;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 * 
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork} 
 * 
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {
	

	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */		
		
		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * tournament selection is used, with single point crossover
			 * 
			 */
			// Select 2 Individuals from the current population.

			Individual parent1 = selectTournament(); 
			Individual parent2 = selectTournament();

			// Generate a child by crossover. Not Implemented			
			//ArrayList<Individual> children = reproduceDefault(parent1, parent2);
			ArrayList<Individual> children = reproduce(parent1, parent2);		
			
			//mutate the offspring
			mutate(children);
			
			// Evaluate the children
			evaluateIndividuals(children);			

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest();
			
			// Implemented in NN class. 
			outputStats();
			
			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}
	




	/**
	 * Selection --
	 * this is a test uses tournament selection, tournament size = 2
	 * 
	 */

	private Individual selectTournament() {		
		// tournament selection picks x random chromosomes,
        // and returns the fittest one
		
		int tnSize = 2;
		
        
        // pick one
        Individual parent, bestParent;
		parent = population.get(Parameters.random.nextInt(Parameters.popSize));
		bestParent = parent.copy();

        // pick tnSize-1 more and then see which is the best
        for (int i = 0; i < tnSize - 1; i++)
        {
            parent = population.get(Parameters.random.nextInt(Parameters.popSize));
            if (parent.fitness < bestParent.fitness)
            {
                bestParent = parent.copy();
            }
        }
        return bestParent.copy();	// return index of best one
	}

	/**
	 * Crossover / Reproduction
	 * 
	 * has one-point cut method
	 */
	
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();

		// one point
        int length, cutPoint, i;
        
        
        //create empty child array
        Individual child = new Individual();
        length = child.chromosome.length;
        // pick cut point
    	cutPoint = Parameters.random.nextInt(child.chromosome.length);
        
        // genes from parent1
    	for (i=0; i<cutPoint; i++)
    		child.chromosome[i] += parent1.chromosome[i];
    	
    	// and genes from parent2
    	for (i=cutPoint; i<length; i++)
    		child.chromosome[i] += parent2.chromosome[i];
        
    	children.add(child.copy());
    	
		return children;
	} 
	
	
	
	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Individual getBest() {
		best = null;;
		for (Individual individual : population) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 * 
	 */
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	
	
	/**
	 * Mutation
	 * 
	 * 
	 */
	private void mutate(ArrayList<Individual> individuals) {		
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}		
	}

	/**
	 * 
	 * Replaces the worst member of the population 
	 * (regardless of fitness)
	 * 
	 */
	private void replace(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			int idx = getWorstIndex();		
			population.set(idx, individual);
		}		
	}

	

	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i; 
			}
		}
		return idx;
	}	

	@Override
	public double activationFunction(double x) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
		return Math.tanh(x);
	}
}
