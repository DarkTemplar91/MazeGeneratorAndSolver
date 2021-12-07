package MazeGeneratorClasses;

import Maze.Directions;
import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class that implements the Randomized Kruskal's algorithm for maze generation. Extends the RPrimGenerator class.
 * As opposed to other methods, this algorithm (just like Prim's) works with the idea of walls, instead of cells.
 */
public class RKruskalsGenerator extends RPrimsGenerator{

    public RKruskalsGenerator(MazeCell[][] maze, MazeMainPanel mf){
        super(maze,mf);
    }
    public RKruskalsGenerator(MazeCell[][] maze, MazeMainPanel mf, int t){
        super(maze,mf,t);
    }
    public RKruskalsGenerator clone(){
        return new RKruskalsGenerator(maze, mf, sleepDrawTime);
    }

    /**
     * Generates a maze based on the Randomized Kruskal's algorithm.
     * It works be selecting sets of walls, checking if the walls are in the same set, and if not, joining the two sets.
     * Two distinct walls are in the same set if there is a path between them.
     */
    @Override
    public void generateMaze() {
        //Opens up the entrance to the maze
        MazeCell current=maze[0][0];
        current.openWall(Directions.North.getBValue());

        //A list of sets. The sets contain the cells which can be accessed from one another.
        List<List<MazeCell>> sets=new LinkedList<>();
        //All the walls in the maze
        List<InnerWall> wallList=new LinkedList<>();
        //Initially create a set for each cell
        //Also add the walls to the list
        for(int row=0;row< maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                sets.add(Arrays.asList(maze[row][column]));
                List<MazeCell> valid=returnValidNeighbours(maze[row][column].getX(), maze[row][column].getY());
                for(int i=0;i<valid.size();i++){
                    wallList.add(new InnerWall(maze[row][column], valid.get(i)));
                }
            }
        }
        //Now we have as many wall sets as maze cells, each containing four walls.

        Random rnd=new Random();
        int idx;
        //Loops until only one set remains, meaning all cells can be reached from any cell
        while(sets.size()!=1){
            //Select a random wall
            idx=wallList.size()>1 ? rnd.nextInt(wallList.size()) : 0;
            InnerWall w= wallList.get(idx);
            //The two neighbouring cells
            MazeCell c1=w.parent;
            MazeCell c2=w.connected;

            //Their relative directions
            Directions d=Directions.getOffsetDirection(c1.getX(),c1.getY(),c2.getX(),c2.getY());

            //Check if the two cells are already connected
            if((c1.getOpenWalls() % d.getBValue())==d.getBValue())
                continue;

            //Two new sets
            List<MazeCell> set1=null;
            List<MazeCell> set2=null;
            //We get the sets
            boolean b1,b2;
            b1=b2=false;
            //We loop through the set of sets looking for the two which contains the two cells
            for(List<MazeCell> list : sets) {
                if(list.contains(c1)){
                    set1=list;
                    b1=true;
                }
                if(list.contains(c2)) {
                    set2=list;
                    b2=true;
                }
                if(b1 && b2)
                    break;
            }
            //If the two sets are disjoint then we join them together
            if(set1!=null && set2!=null && !set1.equals(set2)){

                //We connect the two cells
                c1.openWall(d.getBValue());
                c2.openOppositeWall(d.getBValue());

                if(sleepDrawTime>0) {
                    try {
                        Thread.sleep(sleepDrawTime);
                        mf.repaint();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //Join the two sets
                List<MazeCell> temp= Stream.concat(set1.stream(), set2.stream())
                        .collect(Collectors.toList());
                sets.remove(set2);
                sets.remove(set1);
                sets.add(temp);
            }
            //We remove the wall from the list
            wallList.remove(w);
            //As the walls are double-sided, two cells share a wall, we have to find the opposite cell's wall in the list
            InnerWall oppositeWall=wallList.stream().filter((x)->x.parent==w.connected).findFirst().orElse(null);
            wallList.remove(oppositeWall);


        }
        //We open an exit in the last row.
        maze[maze.length-1][rnd.nextInt(maze[0].length)].openWall(Directions.South.getBValue());

    }
}
