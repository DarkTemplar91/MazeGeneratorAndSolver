package MazeGeneratorClasses;

import Maze.Directions;
import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class implements the maze generation algorithm known as the Aldous-Broder Algorithm.
 * It inherits from the MazeGenerator base class.
 */
public class AldousBroderGenerator extends MazeGenerator{

    public AldousBroderGenerator(MazeCell[][] maze, MazeMainPanel mf){ super(maze, mf);}
    public AldousBroderGenerator(MazeCell[][] maze, MazeMainPanel mf, int t){
        this(maze, mf);
        this.sleepDrawTime=t;
    }

    @Override
    public void generateMaze(){

        MazeCell current=maze[0][0];
        current.setVisited(true);
        current.openWall(Directions.North.getBValue());

        Random rnd=new Random();

        //We put all the cells from the 2D maze array into a List<>
        List<MazeCell> unvisitedCells= Arrays.stream(maze)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        //As all cells in the list are unvisited, we take out the starting cell
        unvisitedCells.remove(current);
        while(!unvisitedCells.isEmpty()){


            //We select a neighbouring cell randomly
            List<MazeCell> neighbours=returnAllNeighbours(current.getX(), current.getY());
            int idx=rnd.nextInt(neighbours.size());
            MazeCell next=maze[neighbours.get(idx).getY()][neighbours.get(idx).getX()];

            //If the cell is unvisited
            if(!next.isVisited()) {
                //Draw
                if (sleepDrawTime > 0) {
                    try {
                        Thread.sleep(sleepDrawTime);
                        mf.repaint();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //We open up the wall between the two cells
                Directions d=Directions.getOffsetDirection(current.getX(), current.getY(), next.getX(), next.getY());
                current.openWall(d.getBValue());
                next.openOppositeWall(d.getBValue());
                //Mark it as visited and remove the cell from the list
                next.setVisited(true);
                unvisitedCells.remove(next);
            }
            current=next;
        }
        maze[maze.length-1][rnd.nextInt(maze[0].length)].openWall(Directions.South.getBValue());
        setAllToUnvisited();

    }

    @Override
    public AldousBroderGenerator clone(){
        return new AldousBroderGenerator(maze,mf,sleepDrawTime);
    }


}
