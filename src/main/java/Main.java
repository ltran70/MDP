import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldRewardFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.common.SinglePFTF;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import com.mdp1.cs7641.*;

public class Main {


    /*
     * Set this constant to the specific problem you want to execute. The code currently has two
     * different problems, but you can add more problems and control which one runs by using this
     * constant.
     */
//    private int problemNumber = -1;

    /*
     * This class runs one algorithm at the time. You can set this constant to the specific
     * algorithm you want to run.
     */
//    private final static Algorithm algorithm = Algorithm.ValueIteration;

    /*
     * If you set this constant to false, the specific GUI showing the grid, rewards, and policy
     * will not be displayed. Honestly, I never set this to false, so I'm not even sure why I added
     * the constant, but here it is anyway.
     */
//    private final static boolean SHOW_VISUALIZATION = false;

    /*
     * This is a very cool feature of BURLAP that unfortunately only works with learning algorithms
     * (so no ValueIteration or PolicyIteration). It is somewhat redundant to the specific analysis
     * I implemented for all three algorithms (it computes some of the same stuff), but it shows
     * very cool charts and it lets you export all the data to external files.
     *
     * At the end, I didnt't use this much, but I'm sure some people will love it. Keep in mind that
     * by setting this constant to true, you'll be running the QLearning experiment twice (so double
     * the time).
     */

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("You did not provide enough input for problem and algorithm you want to run.\n" +
                    "Example: for problem 1 with policy iteration -Dexec.args=\"1 policy\"");
            return;
        }
        int problemNumber = Integer.parseInt(args[0]);
        Algorithm.AlgorithmType algorithm = Algorithm.AlgorithmType.fromName(args[1]);
        boolean showVisualization = false;

        if (args.length > 2) {
            showVisualization = Boolean.parseBoolean(args[2]);
        }


        // Problem problem = problemNumber == 1 ? Problem.createProblem1() : Problem.createProblem2();
        Problem problem = null;

        if (problemNumber == 1) {
            problem = Problem.createProblem1();
        } else {
            problem = Problem.createProblem2();
        }

        GridWorldDomain gridWorldDomain = new GridWorldDomain(problem.getSize(), problem.getSize());
        gridWorldDomain.setMap(problem.getMatrix());
        gridWorldDomain.setProbSucceedTransitionDynamics(0.7);



        /*
         * This makes sure that the algorithm finishes as soon as the agent reaches the goal. We
         * don't want the agent to run forever, so this is kind of important.
         *
         * You could set more than one goal if you wish, or you could even set Traps that end the
         * game (and penalize the agent with a negative reward). But this is not this code...
         */
        TerminalFunction terminalFunction = new SinglePFTF(PropositionalFunction.findPF(gridWorldDomain.generatePfs(), GridWorldDomain.PF_AT_LOCATION));

        GridWorldRewardFunction rewardFunction = new GridWorldRewardFunction(problem.getSize(), problem.getSize(), problem.getDefaultReward());

        /*
         * This sets the reward for the cell representing the goal. Of course, we want this reward
         * to be positive and juicy (unless we don't want our agent to reach the end, which will
         * probably be mean).
         */
        rewardFunction.setReward(problem.getGoal().x, problem.getGoal().y, problem.getGoalReward());

        /*
         * This sets up all the rewards associated with the different Traps specified on the
         * surface of the grid.
         */
        for (Trap Trap : problem.getTraps()) {
            rewardFunction.setReward(Trap.getLocation().x, Trap.getLocation().y, Trap.getReward());
        }

        gridWorldDomain.setTf(terminalFunction);
        gridWorldDomain.setRf(rewardFunction);

        OOSADomain domain = gridWorldDomain.generateDomain();

        /*
         * This sets up the initial position of the agent, and the goal.
         */
        GridWorldState initialState = new GridWorldState(new GridAgent(problem.getStart().x, problem.getStart().y), new GridLocation(problem.getGoal().x, problem.getGoal().y, "loc0"));

        SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();

        Analysis analysis = new Analysis();

        /*
         * Depending on the specific algorithm that we want to run, I call the magic method and
         * specify the corresponding planner.
         */
        switch (algorithm) {

            case QLearning:
                Algorithm.runAlgorithm(analysis, problem, domain, hashingFactory, initialState, new CommonPlanner() {

                    @Override
                    public Planner createCommonPlanner(int episodeIndex, SADomain domain, HashableStateFactory hashingFactory, SimulatedEnvironment simulatedEnvironment) {
                        QLearning agent = new QLearning(domain, 0.99, hashingFactory, 0., 1);
                        for (int i = 0; i < episodeIndex; i++) {
                            agent.runLearningEpisode(simulatedEnvironment);
                            simulatedEnvironment.resetEnvironment();
                        }

                        agent.initializeForPlanning(1);

                        return agent;
                    }
                }, algorithm, showVisualization);
                break;

            case PolicyIteration:
                int x = problem.getNumberOfIterations(Algorithm.AlgorithmType.PolicyIteration);
                Algorithm.runAlgorithm(analysis, problem, domain, hashingFactory, initialState, new CommonPlanner() {

                    @Override
                    public Planner createCommonPlanner(int episodeIndex, SADomain domain, HashableStateFactory hashingFactory, SimulatedEnvironment simulatedEnvironment) {

                        /*
                         * For PolicyIteration we need to specify the number of iterations of
                         * ValueIteration that the algorithm will use internally to compute the
                         * corresponding values. By default, the code is using the same number of
                         * iterations specified for the ValueIteration algorithm.
                         *
                         * A recommended modification is to change this value to the actual number
                         * of iterations that it takes ValueIteration to converge. This will
                         * considerably improve the runtime of the algorithm (assuming that
                         * ValueIteration converges faster than the number of configured
                         * iterations).
                         */
                        return new PolicyIteration(domain, 0.99, hashingFactory, 0.01, x, episodeIndex);
                    }
                }, algorithm, showVisualization);
                break;

            case ValueIteration:
                Algorithm.runAlgorithm(analysis, problem, domain, hashingFactory, initialState, new CommonPlanner() {

                    @Override
                    public Planner createCommonPlanner(int episodeIndex, SADomain domain, HashableStateFactory hashingFactory, SimulatedEnvironment simulatedEnvironment) {
                        return new ValueIteration(domain, 0.99, hashingFactory, 0.01, episodeIndex);
                    }
                }, algorithm, showVisualization);


                break;


            default:
                System.out.println("Unknown Algorithm. You should not get here.");
        }

        analysis.print();
    }


}
