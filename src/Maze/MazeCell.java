package Maze;

import MazeSolverClasses.MazeSolver;

import java.io.Serializable;

/**
 * Represents a single cell of a maze and all it's values.
 */
public class MazeCell implements Serializable {

    /**
     * Whether the cell has been visited or not.
     * Used both for the solving and generation process.
     */
    private boolean visited=false;
    /**
     * The coordinates of the cell in the maze.
     */
    private int x,y;
    /**
     * An integer that represents a 4 bit number.
     * Each bit represents a {@link Directions} and whether there is a wall there or not
     * Initially it is set to 0, which corresponds to the 0000 bit sequence, meaning there are no walls that are open.
     */
    private int walls=0x0;

    public MazeCell(){}
    public MazeCell(int x, int y){
        this.x=x;
        this.y=y;
    }

    /**
     * Sets the {@link MazeCell#visited} to the given value
     * @param visited
     */
    public void setVisited(boolean visited){
        this.visited=visited;
    }

    /**
     * References another MazeCell.
     * Used by a few solver algorithms.
     */
    private MazeCell prev=null;

    /**
     * Assigns the passed MazeCell to {@link MazeCell#prev}
     * @param prev
     */
    public void assignPrevCell(MazeCell prev){
        this.prev=prev;
    }

    /**
     * Returns a reference to the previous cell
     * @return the previous cell stored in {@link #prev}
     */
    public MazeCell getPrevCell(){return prev;}

    /**
     * Returns true if the cell has been visited, otherwise false
     * @return the boolean {{@link #visited}}
     */
    public boolean isVisited(){return visited;}
    /**
     * @return the  {{@link #x}} coordinate of the cell in the maze.
     */
    public int getX(){return x;}
    /**
     * @return the  {{@link #y}} coordinate of the cell in the maze.
     */
    public int getY(){return y;}

    /**
     * Sets the x coordinate of the cell
     * @param x
     */
    public void setX(int x){this.x=x;}

    /**
     * Sets the y coordinate of the cell
     * @param y
     */
    public void setY(int y){this.y=y;}

    /**
     * Opens up a wall in the cell.
     * If there is no wall in the direction represented by the integer value, then it "flips" the corresponding bit.
     * @param w is the int value corresponding to a direction
     */
    public void openWall(int w){
        if(((walls & w) != w) && w<0x10){
            walls+=w;
        }
    }

    /**
     * Opens up the wall in the opposite direction
     * @param w is the int value corresponding to a direction
     */
    public void openOppositeWall(int w){
        int o=(w<<2)%15; //Shifts the bits by two to the left. If needed, it "shifts in" the proper bits in the left
        if((walls & o) != o){
            walls+=o;
        }
    }

    /**
     * Opens up all the walls by setting it to its max possible value.
     * That is in decimal: 15
     * In hexadecimal: 0xf
     * In binary: 1111
     */
    public void openAllWalls(){
        walls=0xf;
    }

    /**
     * Closes the given wall if it is open.
     * @param w
     */
    public void closeWall(int w){
        if((walls & w)==0 || walls!=0)
            walls-=w;
    }

    /**
     *
     * @return {@link MazeCell#walls} that represent the directions in which there are open walls.
     */
    public int getOpenWalls(){
        return walls;
    }

    /**
     * Brian Kernighan’s Algorithm
     * Counts the number of 1 bits in a number
     * Used to determine how many walls are open
     * @return an int between 0 and 4 that represents the number of open walls
     */
    public int openWallNumber() {
        int n=walls;
        int count = 0;
        while (n > 0) {
            n &= (n - 1);
            count++;
        }
        return count;

    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        final MazeCell other = (MazeCell) obj;
        if (this.x != other.x)
            return false;
        if(this.y!=other.y)
            return false;
        if(this.walls!=other.walls)
            return false;

        return true;

    }



}
