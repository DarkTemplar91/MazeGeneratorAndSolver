package MazeSolverClasses;

import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Class that implements the Breadth-First Search. Inherits from MazeSolver.
 */
public class BfsSolver extends MazeSolver{

    /**
     * The 2D array of our maze
     */
    private MazeCell[][] maze;

    /**
     * Stack containing the path from the end point
     */
    private  Stack<MazeCell> reversePath;
    /**
     * A Queue containing the path from the source to the end point.
     */
    private Queue<MazeCell> path;

    public BfsSolver(MazeMainPanel mf, int t){
        super(mf,t);
    }

    /**
     * Implements the BFS algorithm.
     * First it searches through all the cells which have a depth level of one from the source.
     * Then one step deeper, then deeper. Continuing until the destination is found
     * @param maze a 2D array of the maze
     * @return a
     */
    @Override
    public Queue<MazeCell> solveMaze(MazeCell[][] maze) {

        //Path to the destination
        //Also used as our current queueu
        path=new LinkedList<>();
        this.maze=maze;
        //Open the entrance and exit walls
        MazeCell source=maze[0][0];
        MazeCell destination = Arrays.stream(maze[maze.length-1])
                .filter(x -> (x.getOpenWalls() & 0x4)==0x4)
                .findFirst().get();
        source.setVisited(true);
        path.add(source);
        MazeCell current=null;
        while(!path.isEmpty()){
            if(solverDelay>0) {
                try {
                    Thread.sleep(solverDelay);
                    mf.repaint();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //Check the cell that was entered first
            current=path.poll();
            //If it is the destination break.
            if(current.equals(destination))
                break;
            //Return all the neighbours, so they can be added to the queueu next.
            List<MazeCell> valid=getValidNeighbours(maze,current.getX(),current.getY());
            for(MazeCell cell : valid) {
                //Only do that if the cells are yet to be visited. Otherwise, we have already been there.
                if (!cell.isVisited()) {
                    cell.setVisited(true);
                    cell.assignPrevCell(current); //Assigns the previous cell from which we came from.
                    path.add(cell);
                }
            }
        }

        //Build the path be iterating through the cells from the destination until the source is reached,
        //adds the cells to the stack at each step,
        //then reverse the order by adding it to the queue.
        reversePath=new Stack<>();
        current=destination;
        while(!current.equals(source)){
            reversePath.add(current);
            current=current.getPrevCell();
            if(solverDelay>0) {
                try {
                    Thread.sleep(solverDelay);
                    mf.repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        path.clear();
        path.add(maze[0][0]);
        while(!reversePath.isEmpty()){

            path.add(reversePath.pop());
        }

        if(solverDelay>0){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Sets all the cells to unvisited, so it can be solved again.
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                maze[row][column].setVisited(false);
            }
        }
        reversePath.clear();

        return path;
    }

    @Override
    public BfsSolver clone() throws CloneNotSupportedException {
        return new BfsSolver(mf, solverDelay);
    }

    @Override
    public void draw(Graphics g) {
        if (maze != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.DARK_GRAY);
            int borderSize = MazeMainPanel.getBorderSize();
            int offset = MazeMainPanel.getOffset();
            for (int row = 0; row < maze.length; row++) {
                for (int column = 0; column < maze[0].length; column++) {
                    if (maze[row][column].isVisited()) {

                        if (reversePath!=null && reversePath.contains(maze[row][column]))
                            g2d.setColor(Color.GREEN);
                        else if(path.contains(maze[row][column]))
                            g2d.setColor(Color.RED);
                        else
                            g2d.setColor(Color.DARK_GRAY);
                        int walls = maze[row][column].getOpenWalls();
                        if ((walls & 0x1) == 0x1) {
                            g2d.fillRect(offset * column+borderSize, (offset) * (row), offset - borderSize, offset);
                        }
                        if ((walls & 0x2) == 0x2) {
                            g2d.fillRect(offset * column + borderSize, (offset) * (row) + borderSize, offset, offset - borderSize);
                        }
                        if ((walls & 0x4) == 0x4) {
                            g2d.fillRect(offset * column +borderSize, (offset) * (row)+borderSize, offset - borderSize, offset);
                        }
                        if ((walls & 0x8) == 0x8) {
                            g2d.fillRect(offset * column, (offset) * (row) + borderSize, offset, offset - borderSize);

                        }

                    }
                }
            }
        }

    }

    /**
     * Resets the maze if it needs to be regenerated or the solution redrawn.
     */
    @Override
    public void reset() {
        reversePath.clear();
        path.clear();
        maze=null;
    }
}
