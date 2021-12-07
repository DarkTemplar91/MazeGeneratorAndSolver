package MazeGeneratorClasses;

import Maze.Directions;
import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.util.Random;

/**
 * The RecursiveDivisionGenerator implements the Recursive Division algorithm for maze generation.
 * It inherits from the base class MazeGenerator.
 */
public class RecursiveDivisionGenerator extends MazeGenerator{


    public RecursiveDivisionGenerator(MazeCell[][] maze, MazeMainPanel mf){
        super(maze, mf);
        //This is the only maze generation algorithm implemented in this program, where we do not start off
        //with all cells closed completely, but rather with all walls open with the exception of borders.
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                maze[row][column].openAllWalls();
                if(row==0)
                    maze[row][column].closeWall(Directions.North.getBValue());
                if(row==maze.length-1)
                    maze[row][column].closeWall(Directions.South.getBValue());
                if(column==0)
                    maze[row][column].closeWall(Directions.West.getBValue());
                if(column==maze[0].length-1)
                    maze[row][column].closeWall(Directions.East.getBValue());
            }

        }
    }
    public RecursiveDivisionGenerator(MazeCell[][] maze, MazeMainPanel mf, int t){
        this(maze, mf);
        this.sleepDrawTime=t;
    }

    /**
     * Overrides the base class's generateMaze() method. It implements the Recursive Division Algorithm.
     */
    @Override
    public void generateMaze(){
        generateRecursive(0,0, maze[0].length, maze.length); //The first call to the recursive method.
        maze[0][0].openWall(Directions.North.getBValue()); //After the maze is generated, we open our staring cell's northern wall.

        //We choose a random cell from the last row. That cell will be the exit.
        Random rnd=new Random();
        maze[maze.length-1][rnd.nextInt(maze[0].length)].openWall(Directions.South.getBValue());

    }

    /**
     * This method should be called recursively.
     * In each step, it chooses whether to draw horizontally or vertically, then chooses a random fix index of the column or row.
     * It draws the line, then open up a passage
     * @param startX The starting x coordinate of the new sub-maze.
     * @param startY The starting y coordinate of the new sub-maze.
     * @param endX The last x coordinate of the new sub-maze.
     * @param endY The last y coordinate of the new sub-maze.
     */
    public void generateRecursive(int startX, int startY, int endX, int endY){
        //The stop condition.
        //If the current sub-maze has a width or height of one, we stop this branch,
        if(endX-startX<2 || endY-startY<2)
            return;

        //Used for drawing the sub-steps
        if(sleepDrawTime>0) {
            try {
                Thread.sleep(sleepDrawTime);
                mf.repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        Random rnd=new Random();
        //Random boolean that determines if we should split the maze horizontally or vertically.
        Boolean horizontal=rnd.nextBoolean();
        //It selects an index based on the direction of the split. This is the row/column of the first sub-maze
        int id=horizontal ? rnd.nextInt(endY-startY-1)+startY :rnd.nextInt(endX-startX-1)+startX;
        //Selects one cell that should be left open.
        int except=!horizontal ? rnd.nextInt(endY-startY)+startY :rnd.nextInt(endX-startX)+startX;

        //Splits the maze int two parts (draws in the walls).
        for(int i=horizontal ? startX : startY; i < (horizontal ? endX : endY);i++){
            if(horizontal && except!=i){
                maze[id][i].closeWall(Directions.South.getBValue());
                if(id+1<endY)
                    maze[id+1][i].closeWall(Directions.North.getBValue());
            }
            else if(!horizontal && except!=i){
                maze[i][id].closeWall(Directions.East.getBValue());
                if(id+1<endX)
                    maze[i][id+1].closeWall(Directions.West.getBValue());
            }

        }
        //If we split the maze horizontally
        if(horizontal) {

            //Create and run two threads, both of them calling the current method, with the appropriate indexes, so the algorithm runs
            //for the newly created two sub-mazes
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    generateRecursive(startX, startY, endX, id + 1);
                }
            });
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    generateRecursive(startX, id + 1, endX, endY);
                }
            });
            t1.start();
            t2.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //If we split the maze vertically
        else{
            //Create new threads again, calling the method.
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    generateRecursive(startX,startY,id+1,endY);
                }
            });
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    generateRecursive(id+1,startY,endX,endY);
                }
            });
            t1.start();
            t2.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        return;
    }

    /**
     * As the only maze with initially no walls beside the border, we have to override the method.
     */
    @Override
    public final void resetMaze(){
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                maze[row][column].openAllWalls();
                if(row==0)
                    maze[row][column].closeWall(Directions.North.getBValue());
                if(row==maze.length-1)
                    maze[row][column].closeWall(Directions.South.getBValue());
                if(column==0)
                    maze[row][column].closeWall(Directions.West.getBValue());
                if(column==maze[0].length-1)
                    maze[row][column].closeWall(Directions.East.getBValue());
            }

        }
    }


    @Override
    public RecursiveDivisionGenerator clone(){
        return new RecursiveDivisionGenerator(maze,mf,sleepDrawTime);
    }
}
