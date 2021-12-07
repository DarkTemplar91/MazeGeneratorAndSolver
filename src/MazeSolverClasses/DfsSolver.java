package MazeSolverClasses;

import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Implements the depth-first search algorithm. Inherits from MazeSolver
 */
public class DfsSolver extends MazeSolver {

    /**
     * 2D array of the maze
     */
    private MazeCell[][] maze;
    /**
     * The current cell
     */
    MazeCell current;
    /**
     * Stack that stores the path from destination to source
     */
    Stack<MazeCell> reversePath;
    /**
     * A list of MazeCells that are the new, exploring cells.
     * Synchronized list so it is thread-safe.
     */
    private final List<MazeCell> heads=Collections.synchronizedList(new LinkedList<>());


    public DfsSolver(MazeMainPanel mf, int t) {
        super(mf, t);
    }


    /**
     * Wrapper class that stores a boolean.
     * This boolean indicates if a solution has been found.
     * If it has, no new thread can be created.
     */
    private static class Wrapper{
        /**
         * Indicates if a solution has been found
         */
        private static boolean found=false;

        public static boolean isFound() {
            return found;
        }
        public static void setFound(boolean found) { Wrapper.found = found;
        }
    }

    /**
     * Implements the DFS algorithm recursively
     * @param maze a 2D array of the maze
     * @return a queu with the path from the source to the destination
     */
    @Override
    public Queue<MazeCell> solveMaze(MazeCell[][] maze) {

        //We find our source and destination cells.
        this.maze = maze;
        reversePath = new Stack<>();
        Queue<MazeCell> path = new LinkedList<>();
        current=maze[0][0];
        MazeCell destination = Arrays.stream(maze[maze.length - 1])
                .filter(x -> (x.getOpenWalls() & 0x4) == 0x4)
                .findFirst().get();
        MazeCell source = maze[0][0];
        //Initial call to the recursive method.
        dfsRec(maze, current, destination);
        //We build the path by reverse by iterating from the end
        current = destination;
        while (!current.equals(source)) {
            reversePath.add(current);
            current = current.getPrevCell();
            if(solverDelay>0) {
                try {
                    Thread.sleep(solverDelay);
                    mf.repaint();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        reversePath.add(current);
        if(solverDelay>0){
            try {
                Thread.sleep(300);
                for(int row=0;row<maze.length;row++){
                    for(int column=0;column<maze[0].length;column++){
                        maze[row][column].setVisited(false);
                    }
                }
                mf.repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //We reverse the stack, and get back tha valid path from the source.
        path.clear();
        path.add(maze[0][0]);
        while (!reversePath.isEmpty()) {
            path.add(reversePath.pop());

        }

        return path;
    }

    /**
     * The recursive implementation of the dfs algorithm.
     * It calls itself on all of current's neighbours.
     * If the destination was reached it stops.
     * @param maze
     * @param current
     * @param destination
     */
    public void dfsRec(MazeCell[][] maze, MazeCell current, MazeCell destination) {

        //Sets current cell as visited
        current.setVisited(true);
        heads.add(current);

        if (solverDelay != 0) {
            try {
                Thread.sleep(solverDelay);
                mf.repaint();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Stop condition
        if(current.equals(destination)){
            //Wrapper static variable, so other threads will know if it is time to stop
            Wrapper.found=true;
            return;
        }
        //For all neighbouring cells, if they are yet to be visited and so far no solution has been found,
        //it calls itself, with the new cell as current.
        //DFS works by going as deep as it can, then backtracking if no solution has been found
        List<MazeCell> valid = getValidNeighbours(maze, current.getX(), current.getY());
        valid.parallelStream().forEach(cell ->{
            heads.remove(current);
            if (!cell.isVisited() && !Wrapper.isFound()) {
                cell.assignPrevCell(current);
                dfsRec(maze, cell,destination);

            }
        });

    }

    @Override
    public MazeSolver clone() throws CloneNotSupportedException {
        return new DfsSolver(mf, solverDelay);
    }

    @Override
    public void draw(Graphics g) {
        if (maze != null && reversePath!=null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.DARK_GRAY);
            int offset = MazeMainPanel.getOffset();
            int borderSize = MazeMainPanel.getBorderSize();
            for (int row = 0; row < maze.length; row++) {
                for (int column = 0; column < maze[0].length; column++) {
                    if (maze[row][column].isVisited()) {

                        if (reversePath.contains(maze[row][column]))
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
    public void reset() {
        reversePath.clear();
        current=null;
        maze=null;
    }
}

