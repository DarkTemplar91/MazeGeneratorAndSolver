package MazeSolverClasses;

import Maze.Directions;
import Maze.Maze;
import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * An abstract class from which all maze solvers will inherit.
 */
public abstract class MazeSolver implements Serializable {

    /**
     * The amount of time in ms that a thread must wait after each sub-step
     */
    protected int solverDelay;
    /**
     * The panel where the maze is drawn.
     */
    protected MazeMainPanel mf;


    public MazeSolver(MazeMainPanel mf1, int solverDelay) {
        this.mf = mf1;
        this.solverDelay = solverDelay;
    }

    /**
     * Abstract method that the child class must define.
     * It is used for solving the maze.
     * @param maze a 2D array of the maze
     * @return a queue containing the cells that make up the path to the exit
     */
    abstract public Queue<MazeCell> solveMaze(MazeCell[][] maze);

    abstract public MazeSolver clone() throws CloneNotSupportedException;

    /**
     * Sets the solver delay to the given amount
     * @param solverDelay
     */
    public void setSolverDelay(int solverDelay) {
        this.solverDelay = solverDelay;
    }

    /**
     *
     * @return {@link #solverDelay}, the amount of time a thread waits after each sub-step.
     */
    public int getSolverDelay() {
        return solverDelay;
    }

    /**
     * A stastic mehtod that returns all the valid neighbours of the cell, in the given matrix.
     * @param maze a 2D array that represents our maze
     * @param x the x coordinate of the cell which neighbours we must return
     * @param y the y coordinate of the cell which neighbours we must retun
     * @param <T> a cell type that must extend MazeCell
     * @return a list with all the cells that are valid neighbours of the cell in the given coordinate
     */
    public static <T extends MazeCell> List<T> getValidNeighbours(T[][] maze, int x, int y) {
        List<T> valid = new LinkedList<>();

        //We iterate through the directions
        for (Directions d : Directions.values()) {
            //We check if in that direction, we are still in valid territory, and if we have an open wall between the two cells.
            if (x + d.getXOffset() >= 0 && x + d.getXOffset() < maze[0].length &&
                    y + d.getYOffset() >= 0 && y + d.getYOffset() < maze.length &&
                    (maze[y][x].getOpenWalls() & d.getBValue()) == d.getBValue()) {
                valid.add(maze[y + d.getYOffset()][x + d.getXOffset()]);
            }
        }
        return valid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != this.getClass())
            return false;

        final MazeSolver other = (MazeSolver) obj;
        if (this.solverDelay != other.solverDelay)
            return false;
        if (!this.mf.equals(other.mf))
            return false;

        return true;

    }

    /**
     * Abstract method that each class must define.
     * It is used for drawing the sub-steps of a solution for better visualization.
     * @param g Graphics object used to draw.
     */
    abstract public void draw(Graphics g);

    /**
     * Abstract method that each class must define.
     * Sets all parameters of the class back to its original state, so if it needs be
     * the maze can be regenerated or the solution redrawn.
     */
    abstract public void reset();
}
