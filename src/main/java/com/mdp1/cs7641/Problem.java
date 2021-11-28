package com.mdp1.cs7641;

import com.mdp1.cs7641.Trap.TrapType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Problem {
    private double defaultReward;
    private double goalReward;
    private Positions start;
    private Positions goal;
    private int[][] matrix;
    private List<Trap> Traps;
    private HashMap<TrapType, Double> TrapRewards;
    private HashMap<Algorithm.AlgorithmType, Integer> numIterations;

    public Problem(String[] map, HashMap<Algorithm.AlgorithmType, Integer> numIterations, double defaultReward, double goalReward, HashMap<TrapType, Double> TrapRewards) {
        this.numIterations = numIterations;
        this.defaultReward = defaultReward;
        this.goalReward = goalReward;
        this.TrapRewards = TrapRewards;

        this.matrix = new int[map.length][map.length];
        this.Traps = new ArrayList<>();

        /*
         * There's really not much to talk about here. Well, actually, there's something important:
         * notice how the code below inverts the matrix before feeding it to BURLAP. If you don't
         * invert the matrix, you'll have to invert your display to properly read the output of
         * BURLAP. Believe me, it gets really annoying.
         */
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length(); j++) {
                int x = j;
                int y = getSize() - 1 - i;

                this.matrix[x][y] = 0;
                if (map[i].charAt(j) == '1') {
                    this.matrix[x][y] = 1;
                } else if (map[i].charAt(j) == 'X') {
                    this.start = new Positions(x, y);
                } else if (map[i].charAt(j) == 'G') {
                    this.goal = new Positions(x, y);
                } else if (this.TrapRewards != null) {
                    if (map[i].charAt(j) == 'S') {
                        this.Traps.add(new Trap(x, y, this.TrapRewards.get(Trap.TrapType.SMALL), Trap.TrapType.SMALL));
                    } else if (map[i].charAt(j) == 'L') {
                        this.Traps.add(new Trap(x, y, this.TrapRewards.get(Trap.TrapType.LARGE), Trap.TrapType.LARGE));
                    }
                }
            }
        }
    }

    public Positions getStart() {
        return this.start;
    }

    public Positions getGoal() {
        return this.goal;
    }

    public int[][] getMatrix() {
        return this.matrix;
    }

    public int getSize() {
        return this.matrix.length;
    }

    public List<Trap> getTraps() {
        return this.Traps;
    }

    public double getDefaultReward() {
        return this.defaultReward;
    }

    public double getGoalReward() {
        return this.goalReward;
    }

    public int getNumberOfIterations(Algorithm.AlgorithmType algorithm) {
        if (this.numIterations != null && this.numIterations.containsKey(algorithm)) {
            return this.numIterations.get(algorithm);
        }

        return 100;
    }

    public static Problem createProblem1() {
        /*
         * The surface can be described as follows:
         *
         * X — The starting point of the agent.
         * 0 — Represents a safe cell where the agent can move.
         * 1 — Represents a wall. The agent can't move to this cell.
         * G — Represents the goal that the agent wants to achieve.
         * S — Represents a small Trap. The agent will be penalized.
         * L — Represents a large Trap. The agent will be penalized.
         */
        String[] map = new String[]{
                "X001111001",
                "01000S1010",
                "0101110S00",
                "00S000L100",
                "0111101010",
                "00L010S001",
                "0S00100000",
                "000000S10L",
                "0101110S00",
                "00S000L10G",
        };

        /*
         * Make sure to specify the specific number of iterations for each algorithm. If you don't
         * do this, I'm still nice and use 100 as the default value, but that wouldn't make sense
         * all the time.
         */
        HashMap<Algorithm.AlgorithmType, Integer> numIterationsHashMap = new HashMap<Algorithm.AlgorithmType, Integer>();
        numIterationsHashMap.put(Algorithm.AlgorithmType.ValueIteration, 120);
        numIterationsHashMap.put(Algorithm.AlgorithmType.PolicyIteration, 20);
        numIterationsHashMap.put(Algorithm.AlgorithmType.QLearning, 400);

        /*
         * These are the specific rewards for each one of the Traps. Here you can be creative and
         * play with different values as you see fit.
         */
        HashMap<TrapType, Double> TrapRewardsHashMap = new HashMap<TrapType, Double>();
        TrapRewardsHashMap.put(TrapType.SMALL, -3.0);
        TrapRewardsHashMap.put(TrapType.LARGE, -10.0);

        /*
         * Notice how I specify below the specific default reward for cells with nothing on them (we
         * want regular cells to have a small penalty that encourages our agent to find the goal),
         * and the reward for the cell representing the goal (something nice and large so the agent
         * is happy).
         */
        return new Problem(map, numIterationsHashMap, -0.5, 100, TrapRewardsHashMap);
    }

    public static Problem createProblem2() {
        String[] map = new String[]{
                "1101101110011110011110001",
                "0X10100010100010001000010",
                "00101001101L0010111001011",
                "1000001S00001010100000000",
                "11100010101111S1101001101",
                "0000101000001000000000010",
                "0011101S10101011101010010",
                "1000101010100010001000101",
                "1010101010110110100010101",
                "1010000010001000100001101",
                "1110101L110010L1101010101",
                "0000101000101000001001010",
                "101000101010101111S010000",
                "1000100010100010100001000",
                "0110001010101110101010010",
                "0010000010100010001001010",
                "00101001101L0010101000011",
                "1000001S00001010010010000",
                "1010101101101010100010101",
                "10100L0000100010000011010",
                "0111001111001110110110000",
                "10111S101L1010S1100000011",
                "10001S1010001000101010010",
                "11100S10101111S1101011100",
                "00001S10000010011G0000000",
        };

        HashMap<Algorithm.AlgorithmType, Integer> numIterationsHashMap = new HashMap<>();
        numIterationsHashMap.put(Algorithm.AlgorithmType.ValueIteration, 390);
        numIterationsHashMap.put(Algorithm.AlgorithmType.PolicyIteration, 30);
        numIterationsHashMap.put(Algorithm.AlgorithmType.QLearning, 1000);

        HashMap<TrapType, Double> TrapRewardsHashMap = new HashMap<TrapType, Double>();
        TrapRewardsHashMap.put(TrapType.SMALL, -3.0);
        TrapRewardsHashMap.put(TrapType.LARGE, -10.0);

        return new Problem(map, numIterationsHashMap, -0.5, 100, TrapRewardsHashMap);
    }
}
