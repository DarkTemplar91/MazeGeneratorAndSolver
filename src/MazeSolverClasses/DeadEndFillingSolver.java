package MazeSolverClasses;

import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Class that implements the dead-end filling algorithm. Extends from MazeSolver
 * It works by iteratively finding all the dead-ends and marking them as unreachable.
 */
public class DeadEndFillingSolver extends MazeSolver{

    public DeadEndFillingSolver(MazeMainPanel mf, int delay){
        super(mf,delay);
    }

    /**
     * The cells that are currently viewed as dead-ends
     */
    List<MazeCell> deadEnds=new ArrayList<>();
    public List<MazeCell> getDeadEnds(){
        return deadEnds;
    }

    /**
     * The 2D array of our maze
     */
    private MazeCell[][] maze;

    /**
     * Implement the dead-end filling algorithm.
     * It works by labeling all dead-ends at each pass.
     * A cell is a dead-end, if it only has one valid, non-visited neighbour that is accessible from it.
     * By doing this many times, all dead-ends are marker and only the solution remains
     * @param maze a 2D array of the maze
     * @return
     */
    @Override
    public Queue<MazeCell> solveMaze(MazeCell[][] maze) {

        this.maze=maze;
        //Gets the initial dead-ends
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                if(maze[row][column].openWallNumber()==1){
                    deadEnds.add(maze[row][column]);
                    maze[row][column].setVisited(true);
                }
                else{
                    maze[row][column].setVisited(false);
                }
            }
        }

        //Start and destination cells
        MazeCell start=maze[0][0];
        MazeCell destination = Arrays.stream(maze[maze.length-1])
                .filter(x -> (x.getOpenWalls() & 0x4)==0x4)
                .findFirst().get();
        Queue<MazeCell> path=new LinkedList<>();
        //If there are no more dead-ends, the algorithm stops.
        while(!deadEnds.isEmpty()){
            if(solverDelay>0) {
                try {
                    Thread.sleep(solverDelay);
                    mf.repaint();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //We remove the previous dead-ends.
            Iterator<MazeCell> iter = deadEnds.iterator();
            while (iter.hasNext()) {
                MazeCell cell = iter.next();
                iter.remove();
            }

            //Gets the dead-ends or the ones which only have one non-visited neighbour
            for(int row=0;row<maze.length;row++){
                for(int column=0;column<maze[0].length;column++){
                    if(start.equals(maze[row][column]) || destination.equals(maze[row][column]) || maze[row][column].isVisited())
                        continue;
                    List<MazeCell> valid = getValidNeighbours(maze, column,row);
                    valid.removeIf(MazeCell::isVisited);
                    if(!maze[row][column].isVisited() && valid.size()==1){
                        deadEnds.add(maze[row][column]);

                    }
                }
            }
            //All of these nodes are set to visited, if they have yet to be.
            for(MazeCell cell : deadEnds){
                cell.setVisited(true);
            }
        }

        if(solverDelay>0) {
            try {
                Thread.sleep(solverDelay);
                mf.repaint();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Builds the solution
        MazeCell current=maze[0][0];
        MazeCell prev=null;
        while(!current.equals(destination)){

            path.add(current);
            List<MazeCell> valid = getValidNeighbours(maze, current.getX(), current.getY());
            if(prev!=null)
                valid.remove(prev);
            valid.removeIf(c->c.isVisited());
            prev=current;
            current=valid.get(0);

        }
        path.add(destination);
        if(solverDelay>0){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Resets the maze to unvisited
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                maze[row][column].setVisited(false);
            }
        }

        return path;

    }
    @Override
    public DeadEndFillingSolver clone(){
        return new DeadEndFillingSolver(mf,solverDelay);
    }

    @Override
    public void draw(Graphics g) {

        if (maze != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.DARK_GRAY);
            int offset = MazeMainPanel.getOffset();
            int borderSize = MazeMainPanel.getBorderSize();
            for (int row = 0; row < maze.length; row++) {
                for (int column = 0; column < maze[0].length; column++) {
                    if (maze[row][column].isVisited()) {

                        if (deadEnds.contains(maze[row][column]))
                            g2d.setColor(Color.GREEN);
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

    @Override
    public void reset() {
        deadEnds.clear();
        maze=null;
    }


}
