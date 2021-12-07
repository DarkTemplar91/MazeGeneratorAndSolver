package MazeSolverClasses;

/**
 * Enumerator that stores the type of Maze Solver Algorithms that the program implements, so the user can select from them.
 */
public enum SolverType {
    BFSSolver("Breadth-First Search"),DFSSolver("Depth-First Search"), DeadEndFillingSolver("Dead-End Filler"), AStarSolver("A*"),
    LeeRoutingAlgortihm("Lee's Routing Algorithm"), RecursiveSolver("Recursive Solver"), TremauxSolver("Tremaux Algorithm"), WallFollower("Wall Follower");

    /**
     * The name of the algorithm used for finding the solution.
     */
    String name;
    SolverType(String name){
        this.name=name;
    }

    /**
     * Overrides the toString() method so it returns the name of the object.
     * @return string with the name of the object
     */
    @Override
    public String toString(){
        return name;
    }
}
