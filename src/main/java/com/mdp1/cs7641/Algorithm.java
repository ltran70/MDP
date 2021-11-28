package com.mdp1.cs7641;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.auxiliary.common.ConstantStateGenerator;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;

public class Algorithm {
    /**
     * Here is where the magic happens. In this method is where I loop through the specific number
     * of episodes (iterations) and run the specific algorithm. To keep things nice and clean, I use
     * this method to run all three algorithms. The specific details are specified through the
     * PlannerFactory interface.
     * <p>
     * This method collects all the information from the algorithm and packs it in an Analysis
     * instance that later gets dumped on the console.
     */
    public static void runAlgorithm(Analysis analysis, Problem problem, SADomain domain, HashableStateFactory hashingFactory, State initialState, CommonPlanner commonPlanner, Algorithm.AlgorithmType algorithm, boolean showVisualization) {
        ConstantStateGenerator constantStateGenerator = new ConstantStateGenerator(initialState);
        SimulatedEnvironment simulatedEnvironment = new SimulatedEnvironment(domain, constantStateGenerator);
        Planner planner = null;
        Policy policy = null;
        // for (int episodeIndex = 1; episodeIndex <= problem.getNumberOfIterations(algorithm); episodeIndex++) {
        //     long startTime = System.nanoTime();
        //     planner = commonPlanner.createCommonPlanner(episodeIndex, domain, hashingFactory, simulatedEnvironment);
        //     policy = planner.planFromState(initialState);
            
            
        
        //     /*
        //      * If we haven't converged, following the policy will lead the agent wandering around
        //      * and it might never reach the goal. To avoid this, we need to set the maximum number
        //      * of steps to take before terminating the policy rollout. I decided to set this maximum
        //      * at the number of grid locations in our map (width * width). This should give the
        //      * agent plenty of room to wander around.
        //      *
        //      * The smaller this number is, the faster the algorithm will run.
        //      */
        //     int maxNumberOfSteps = problem.getSize() * problem.getSize();

        //     Episode episode = PolicyUtils.rollout(policy, initialState, domain.getModel(), maxNumberOfSteps);
            
        //     analysis.add(episodeIndex, episode.rewardSequence, episode.numTimeSteps(), (long) (System.nanoTime() - startTime) / 10 ^ 6);
        // }

        for (int episodeIndex = 0; episodeIndex <= problem.getNumberOfIterations(algorithm); episodeIndex += 20) {
            long startTime = System.nanoTime();
            planner = commonPlanner.createCommonPlanner(episodeIndex, domain, hashingFactory, simulatedEnvironment);
            policy = planner.planFromState(initialState);
            
            
        
            /*
             * If we haven't converged, following the policy will lead the agent wandering around
             * and it might never reach the goal. To avoid this, we need to set the maximum number
             * of steps to take before terminating the policy rollout. I decided to set this maximum
             * at the number of grid locations in our map (width * width). This should give the
             * agent plenty of room to wander around.
             *
             * The smaller this number is, the faster the algorithm will run.
             */
            int maxNumberOfSteps = problem.getSize() * problem.getSize();

            Episode episode = PolicyUtils.rollout(policy, initialState, domain.getModel(), maxNumberOfSteps);
            
            analysis.add(episodeIndex, episode.rewardSequence, episode.numTimeSteps(), (long) (System.nanoTime() - startTime) / 10 ^ 6);
        }


        // if (algorithm == Algorithm.AlgorithmType.QLearning) {
        //     learningExperimenter(problem, (LearningAgent) planner, simulatedEnvironment);
        // }

        if (showVisualization) {
            Analysis.visualize(problem, (ValueFunction) planner, policy, initialState, domain, hashingFactory, algorithm.getName());
        }
    }

    /**
     * Runs a learning experiment and shows some cool charts. Apparently, this is only useful for
     * Q-Learning, so I only call this method when Q-Learning is selected and the appropriate flag
     * is enabled.
     */
    private static void learningExperimenter(Problem problem, LearningAgent agent, SimulatedEnvironment simulatedEnvironment) {
        LearningAlgorithmExperimenter experimenter = new LearningAlgorithmExperimenter(simulatedEnvironment, 10, problem.getNumberOfIterations(Algorithm.AlgorithmType.QLearning), new LearningAgentFactory() {

            public String getAgentName() {
                return Algorithm.AlgorithmType.QLearning.getName();
            }

            public LearningAgent generateAgent() {
                return agent;
            }
        });

        /*
         * Try different PerformanceMetric values below to display different charts.
         */
        experimenter.setUpPlottingConfiguration(500, 250, 2, 1000, TrialMode.MOST_RECENT_AND_AVERAGE, PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE, PerformanceMetric.AVERAGE_EPISODE_REWARD);
        experimenter.startExperiment();
    }


    public enum AlgorithmType {
        ValueIteration("Value Iteration"),
        PolicyIteration("Policy Iteration"),
        QLearning("Q-Learning");

        public static AlgorithmType fromName(String name) {
            switch (name) {
                case "value":
                    return ValueIteration;
                case "policy":
                    return PolicyIteration;
                case "q":
                    return QLearning;
            }
            return null;
        }

        private String name;

        AlgorithmType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

}
