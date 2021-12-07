package MazeSolverClasses;

import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Recursive Solver Algorithm. Extends the MazeSolver abstract class.
 */
public class RecursiveSolver extends MazeSolver{

    /**
     * Thread safe collection of cells.
     * They are the current "heads" of the search.
     */
    private final List<MazeCell> heads=Collections.synchronizedList(new LinkedList<>());

    /**
     * Stacj that stores the path from destination to source
     */
    Stack<MazeCell> path=new Stack<>();
    public RecursiveSolver(MazeMainPanel mf, int t){
        super(mf, t);
    }

    /**
     * Wrapper static class that has a boolean that indicate if a path has been found or not,
     * so other threads can stop, if this value is set to true.
     */
    private static class Wrapper{
        /**
         * Indicate if a solution has been found.
         */
        private static boolean found=false;

        public static boolean isFound() {
            return found;
        }
        public static void setFound(boolean found) { Wrapper.found = found;
        }
    }

    /**
     * Returns the current reversed path of the solver.
     * @return {@link #path}
     */
    public Stack<MazeCell> getPath(){
        return path;
    }

    /**
     * The 2D array of our maze.
     */
    private MazeCell[][] maze;

    /**
     * Implement a recursive maze solving algorithm. When the solution has been found, it build the path,
     * @param maze a 2D array of the maze
     * @return
     */
    @Override
    public Queue<MazeCell> solveMaze(MazeCell[][] maze) {
        this.maze=maze;
        path.clear();
        heads.clear();
        MazeCell destination = Arrays.stream(maze[maze.length-1])
                .filter(x -> (x.getOpenWalls() & 0x4)==0x4)
                .findFirst().get();
        mf.repaint();
        //Initial call to the recursive method
        recursiveSolve(maze,maze[0][0],destination);

        MazeCell current=maze[0][0];
        //We reverse the stack by iterating through it and enqueueing all the cells
        Queue<MazeCell> solution=new LinkedList<>();
        while(!path.isEmpty()){
            solution.add(path.pop());
        }
        Wrapper.setFound(false);
        this.maze=maze;

        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                maze[row][column].setVisited(false);
            }
        }

        return solution;
    }

    /**
     * The recursive implementation itself.
     * @param maze the 2D array of the maze.
     * @param current the current cell
     * @param destination the exit cell, where we want to get to.
     * @return boolean indicating if the solution is on this branch
     */
    public boolean recursiveSolve(MazeCell[][] maze,MazeCell current, MazeCell destination) {
        //Labels this cell as visited
        current.setVisited(true);
        //Adds it to the heads list, so it can be differentiated when the sub-steps are drawn.
        heads.add(current);

        if(solverDelay>0) {
            try {
                Thread.sleep(solverDelay);
                mf.repaint();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Stop condition
        if (destination.equals(current)) {
            path.add(current);
            return true;
        } else {
            //We return all the surrounding nodes, then remove the ones we have already visited
            List<MazeCell> valid = getValidNeighbours(maze, current.getX(), current.getY());
            valid.removeIf(c -> c.isVisited());


            //Wrapper object, if one of the neighboring nodes return true, this gets set to true
            //This is used, so we can concurrently check all neighboring nodes, but return the valid value.
            var w = new Object() {
                public boolean properWay = false;
            };
            //Calls the method on the neighbouring valid cells
            //Runs in parallel
            valid.parallelStream().forEach(cell -> {
                //Removes the current from the heads cells, as its childs are the new heads.
                heads.remove(current);
                //It there is no solution yet, we call the method recursively
                if (!Wrapper.isFound()) {

                    //If on this branch, we reached the solution
                    if (recursiveSolve(maze, cell, destination)) {
                        //Add the current cell to the path, as we reached the destination through it.
                        path.add(current);
                        //Sets the boolean to true, so no new thread will be created.
                        Wrapper.setFound(true);
                        //sets the boolean to true, so if any one of the cells if a good solution it returns true.
                        w.properWay = true;
                    }

                }


            });
            {
                if(solverDelay!=0 ) {
                    try {
                        if(w.properWay)
                            Thread.sleep(solverDelay);
                        mf.repaint();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(!w.properWay)
                    heads.remove(current);
                //returns false or true based on if any of the cells are part of the path
                return w.properWay;
            }

        }

    }

    @Override
    public RecursiveSolver clone() throws CloneNotSupportedException {
        return new RecursiveSolver(mf,solverDelay);
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

                        if (path.contains(maze[row][column]))
                            g2d.setColor(Color.GREEN);
                        else if(heads.contains(maze[row][column]))
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
    @Override
    public void reset(){
        maze=null;
        path.clear();
        Wrapper.found=false;
    }
}
