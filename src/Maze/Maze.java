package Maze;

import MazeExceptions.NoMazeGeneratorFoundException;
import MazeExceptions.NoMazeSolverFoundException;
import MazeGeneratorClasses.MazeGenerator;
import MazeSolverClasses.MazeSolver;

import java.io.Serializable;
import java.util.Queue;

/**
 * A class that represent a single maze. It stores the 2D array in which our actual maze is stored,
 * the generator which generates the maze and a solver that can solve it.
 */
public final class Maze implements Serializable{
    /**
     * The 2D array in which we store the cells that make up our maze
     */
    private MazeCell[][] maze;
    /**
     * The MazeSolver object which will handle the solving
     */
    private MazeSolver solver=null;
    /**
     * A MazeGenerator object that generates the maze
     */
    private MazeGenerator generator=null;

    /**
     * @return the 2D array {@link Maze#maze} that represents our maze
     */
    public MazeCell[][] getMaze(){
        return maze;
    }
    /**
     * @return the MazeSolver {@link Maze#solver}
     */
    public MazeSolver getSolver(){
        return solver;
    }
    /**
     * @return the MazeGenerator {@link Maze#generator}
     */
    public MazeGenerator getGenerator(){
        return generator;
    }


    public Maze(int width, int height){
        maze=new MazeCell[height][width];
        for(int w=0;w<width;w++){
            for(int h=0;h<height;h++){
                maze[h][w]=new MazeCell();
                maze[h][w].setX(w);
                maze[h][w].setY(h);
            }
        }
    }
    public <T extends MazeGenerator> void addGenerator(T g){
        generator=g.clone();
    }

    /**
     * Adds a MazeSolver to our maze
     * @param solver The solver that is added to our maze
     * @param <T> A class that must extend the abstract base class MazeSolver
     */
    public <T extends MazeSolver> void addSolver(T solver){
        try {
            this.solver=solver.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calls the MazeGenerator's generateMaze method.
     * It will generate our maze
     * @throws NoMazeGeneratorFoundException
     */
    public void generateMaze() throws NoMazeGeneratorFoundException {
        if(generator!=null){
            generator.resetMaze();
            generator.generateMaze();
        }
        else{
            throw new NoMazeGeneratorFoundException();
        }
    }

    /**
     * Calls the MazeSolver's solve method to solve the Maze
     * @return a queue that stores the cells that make up the path to the exit
     * @throws NoMazeSolverFoundException
     */
    public Queue<MazeCell> solveMaze() throws NoMazeSolverFoundException {
        if(solver!=null)
        {
            Queue<MazeCell> path= solver.solveMaze(maze);
            return path;
        }
        else{
            throw new NoMazeSolverFoundException();
        }
    }

}
