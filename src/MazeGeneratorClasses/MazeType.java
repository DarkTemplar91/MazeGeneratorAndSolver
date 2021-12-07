package MazeGeneratorClasses;

/**
 * An enumerator that enumerates the types of maze generation algorithms
 */
public enum MazeType {
    AldousBroderGenerator("Aldous-Broder Algorithm"),
    MazeGenerator("Recursive Backtracker Algorithm"),
    RKruskalsGenerator("Randomized Kruskal's Algorithm"),
    RPrimsGenerator("Randomized Prim's Algorithm"),
    RecursiveDivision("Recursive Division Algorithm"),
    WilsonGenerator("Wilson's Algorithm");

    /**
     * The name of the algorithm the maze was generated with.
     */
    String name;
    MazeType(String name){
        this.name=name;
    }

    /**
     *
     * @return a string with the name of the object
     */
    @Override
    public String toString(){
        return name;
    }
}
