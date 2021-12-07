package MazeGeneratorClasses;

import Maze.Directions;
import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements Wilson's algorithm for maze generation.
 * Extends the base class MazeGenerator.
 */
public class WilsonsGenerator extends MazeGenerator{

    public WilsonsGenerator(MazeCell[][] maze, MazeMainPanel mf){
        super(maze, mf);
    }
    public WilsonsGenerator(MazeCell[][] maze, MazeMainPanel mf, int t){
        this(maze,mf);
        this.sleepDrawTime=t;
    }

    /**
     * Overrides the base class's method.
     * Wilson's algorithm works by selecting two cells and performing a random walk between them.
     * If it succeeded or the only possible step brings us to an already visited cell, we backtrack
     */
    @Override
    public void generateMaze(){
        Random rnd=new Random();

        //We put all the cells from the 2D maze array into a List<>
        List<MazeCell> unvisitedCells= Arrays.stream(maze)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
        //Chose a random cell and remove it from the unvisited list
        int idx=rnd.nextInt(unvisitedCells.size());

        MazeCell current=unvisitedCells.get(idx);
        current.setVisited(true);
        unvisitedCells.remove(current);

        Stack<MazeCell> path=new Stack<>();

        while(!unvisitedCells.isEmpty()){
            //Choose another random cell
            idx=rnd.nextInt(unvisitedCells.size());
            current=unvisitedCells.get(idx);
            path.add(current);


            //Random walk
            while(!current.isVisited()){
                List<MazeCell> neighbours=returnAllNeighbours(current.getX(), current.getY());
                idx=rnd.nextInt(neighbours.size());
                current=neighbours.get(idx);
                //If we have already been here, backtrack
                if(path.contains(current)){
                    while(current!=path.peek())
                        path.pop();
                    current=path.peek();
                }
                //Add to the path
                else
                    path.add(current);
            }
            MazeCell next=current;
            while(!path.isEmpty()){
                current=path.pop();
                if(!path.isEmpty()){
                    next=path.peek();
                }
                else{
                    current.setVisited(true);
                    unvisitedCells.remove(current);
                    break;
                }

                Directions d=Directions.getOffsetDirection(current.getX(), current.getY(), next.getX(), next.getY());
                current.openWall(d.getBValue());
                current.setVisited(true);
                next.openOppositeWall(d.getBValue());
                unvisitedCells.remove(current);
            }

        }
        //Opens up the exit and entrance.
        maze[0][0].openWall(Directions.North.getBValue());
        maze[maze.length-1][rnd.nextInt(maze[0].length)].openWall(Directions.South.getBValue());
        //Sets all cells to unvisited, as other algorithms or maze solvers might use them.
        setAllToUnvisited();


    }

    @Override
    public WilsonsGenerator clone(){
        return new WilsonsGenerator(maze,mf,sleepDrawTime);
    }
}
