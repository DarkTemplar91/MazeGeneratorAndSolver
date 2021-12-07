package MazeGeneratorClasses;

import Maze.Directions;
import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.io.Serializable;
import java.util.*;

/**
 * This implement the Recursive backtracker algorithm.
 * As the most basic of the maze generation algorithm, this will be the base class of many other classes.
 */
public class MazeGenerator implements Cloneable, Serializable {
    /**
     * The 2D array, that stores our maze.
     */
    MazeCell[][] maze;
    /**
     * The MazeMainPanel on which the maze will be drawn on.
     */
    protected MazeMainPanel mf;
    /**
     * The amount of time the thread will sleep after each sub-step.
     */
    protected int sleepDrawTime=0;

    public MazeGenerator(MazeCell[][] maze, MazeMainPanel mf){
        this.maze=maze;
        this.mf=mf;
        mf.setMaze(maze);
    }
    public MazeGenerator(MazeCell[][] maze, MazeMainPanel mf, int sleepDrawTime){
        this(maze,mf);
        this.sleepDrawTime=sleepDrawTime;
    }

    /**
     * Sets the sleep time to the given value
     * @param t
     */
    public void setSleepDrawTime(int t){
        sleepDrawTime=t;
    }

    /**
     *
     * @return {@link #sleepDrawTime}, which indicates the amount of time the thread has to sleep after each sub-step.
     */
    public int getSleepDrawTime() {
        return sleepDrawTime;
    }

    /**
     * It generates a maze using the algorithm.
     */
    public void generateMaze(){
        //Stack used for the backtracking algorithm
        Stack<MazeCell> stack=new Stack<>();
        //The starting cell is pushed to the stack.
        maze[0][0].setVisited(true);
        stack.push(maze[0][0]);
        MazeCell current=maze[0][0];
        //We open up the northern wall of the starting cell
        current.openWall(Directions.North.getBValue());
        Random rnd=new Random();


        while(!stack.isEmpty()){

            //It returns all the valid, non-visited neighbours
            List<MazeCell> n=returnValidNeighbours(current.getX(), current.getY());

            //If there are none, this is a dead end
            //We remove the cell from the stack and backtrack
            if(n.isEmpty()){
                stack.pop();
                if(!stack.isEmpty()) {
                    current = stack.peek();
                }
                continue;
            }

            //We choose the next cell randomly
            int next=0;
            if(n.size()>1)
                next=rnd.nextInt(n.size());

            //We open up the walls between the two cells
            int c= Directions.getOffsetDirection(current.getX(),current.getY(),n.get(next).getX(),n.get(next).getY()).getBValue();
            current.openWall(c);
            n.get(next).setVisited(true);
            //n.get(next).assignPrevCell(current);
            n.get(next).openOppositeWall(c);
            current=n.get(next);

            if(sleepDrawTime>0) {
                try {
                    Thread.sleep(sleepDrawTime);
                    mf.repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(!stack.contains(current)){
                stack.push(current);
            }

        }
        //Open up a southern wall in the last row in a random cell as a destination.
        maze[maze.length-1][rnd.nextInt(maze[0].length)].openWall(0x4);



    }
    public MazeGenerator clone(){
        return new MazeGenerator(maze,mf,sleepDrawTime);
    }

    /**
     * Returns a list of MazeCell objects that have not been visited and are adjacent to the current maze cell.
     * @param x The x coordinate of the cell
     * @param y The y coordinate of the cell
     * @return a list of MazeCell objects that are neighbouring to the current one, but have yet to be visited.
     */
    public List<MazeCell> returnValidNeighbours(int x, int y){
        List<MazeCell> neighbours=new ArrayList<>();
        for(Directions d: Directions.values()){
            if(x+d.getXOffset() >= 0 && x+d.getXOffset() < maze[0].length &&
                    y+d.getYOffset()>=0 && y+d.getYOffset() < maze.length &&
                    !maze[y + d.getYOffset()][x + d.getXOffset()].isVisited()){
                neighbours.add(maze[y+d.getYOffset()][x+d.getXOffset()]);
            }
        }

        return neighbours;
    }

    //Returns all the neighbours of the cell at the given position
    //Same as the MazeGenerator.returnValidNeighbours except it does not check if the cell has been visited
    public List<MazeCell> returnAllNeighbours(int x, int y){
        List<MazeCell> neighbours=new ArrayList<>();
        for(Directions d: Directions.values()){
            if(x+d.getXOffset() >= 0 && x+d.getXOffset() < maze[0].length &&
                    y+d.getYOffset()>=0 && y+d.getYOffset() < maze.length){
                neighbours.add(maze[y+d.getYOffset()][x+d.getXOffset()]);
            }
        }

        return neighbours;
    }

    /**
     * Sets all the cells in the maze to unvisited.
     */
    public void setAllToUnvisited(){
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                maze[row][column].setVisited(false);
            }
        }
    }

    /**
     * Resets the maze to its original state, meaning no cell has been visited and all walls are closed.
     */
    public void resetMaze(){
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                maze[row][column].setVisited(false);
                maze[row][column].closeWall(maze[row][column].getOpenWalls());

            }
        }
    }


    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != this.getClass())
            return false;
        final MazeGenerator other = (MazeGenerator) obj;
        if (!Objects.equals(this.maze, other.maze))
            return false;
        if (!this.mf.equals(other.mf))
            return false;
        if(this.sleepDrawTime!=(other.sleepDrawTime))
            return false;


        return true;
    }
}
