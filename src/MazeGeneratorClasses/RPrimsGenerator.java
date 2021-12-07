package MazeGeneratorClasses;

import Maze.Directions;
import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class implements the Randomized Prim's algorithm. Extends the base class MazeGenerator
 */
public class RPrimsGenerator extends MazeGenerator {

    public RPrimsGenerator(MazeCell[][] maze, MazeMainPanel mf){
        super(maze, mf);
    }
    public RPrimsGenerator(MazeCell[][] maze, MazeMainPanel mf, int t){
        this(maze, mf);
        this.sleepDrawTime=t;
    }

    /**
     * A class that represents a wall, and stores the references to the two cells which it divides
     */
    protected class InnerWall{
        /**
         * The current cell from which the wall belongs
         */
        MazeCell parent;
        /**
         * The other side of the wall
         */
        MazeCell connected;
        public InnerWall(MazeCell parent, MazeCell neighbour){
            this.parent=parent;
            this.connected=neighbour;
        }
    }

    /**
     * Overrides the base class's method.
     * This class generates a maze based on the Randomized Prim's algorithm.
     * It selects walls randomly, and if it has yet to be visited, it opens up the walls between the two cells.
     */
    @Override
    public void generateMaze(){
        //The start of our maze
        MazeCell current=maze[0][0];
        current.setVisited(true);
        current.openWall(Directions.North.getBValue());

        Random rnd=new Random();
        //All the walls
        List<InnerWall> wallList=new LinkedList<>();

        //Returns the valid neighbours
        List<MazeCell> valid=returnValidNeighbours(current.getX(), current.getY());
        //Adds the current cell's walls to the list
        for(int i=0;i<valid.size();i++){
            wallList.add(new InnerWall(current, valid.get(i)));
        }
        //The loop runs as long as there are walls in the list
        while(!wallList.isEmpty()){
            if(sleepDrawTime>0) {
                try {
                    Thread.sleep(sleepDrawTime);
                    mf.repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //We chose a wall randomly; it can be from any of the visited cells
            int randomWallIndex=rnd.nextInt(wallList.size());
            InnerWall currentWall=wallList.get(randomWallIndex);
            //if the other side of the wall is yet to be visited
            if(!currentWall.connected.isVisited()){
                //we open up the wall between the two cells
                currentWall.connected.setVisited(true);
                Directions offset=Directions.getOffsetDirection(currentWall.parent.getX(),currentWall.parent.getY(),
                        currentWall.connected.getX(),currentWall.connected.getY());
                currentWall.parent.openWall(offset.getBValue());
                currentWall.connected.openOppositeWall(offset.getBValue());
                //we add the new walls to the list, if they are not in the list already
                valid=returnValidNeighbours(currentWall.connected.getX(),currentWall.connected.getY());
                for(MazeCell cell: valid){
                    if(!cell.equals(currentWall.parent)){
                        wallList.add(new InnerWall(currentWall.connected,cell));
                    }
                }
            }

            wallList.remove(currentWall);

        }
        //open up a wall in the last row as an exit
        maze[maze.length-1][rnd.nextInt(maze[0].length)].openWall(Directions.South.getBValue());
        //Reset all the walls to be unvisited.
        setAllToUnvisited();
    }
    @Override
    public RPrimsGenerator clone(){
        return new RPrimsGenerator(maze,mf,sleepDrawTime);
    }

}
