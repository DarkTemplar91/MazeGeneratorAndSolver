package MazeSolverClasses;

import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 *A class that inheriths from MazeSolver base class and implement the A* algorithm for path finding.
 */
public class AStarSolver extends MazeSolver{

    /**
     * Nested class that extends from the MazeCell class.
     * It is used, so we can store the current total, heuristic and source distance of a cell.
     */
    public class DistMazeCell extends MazeCell implements Comparable<DistMazeCell>{

        /**
         * The distance from the source. Initialized as "infinity" everywhere except the source.
         */
        int distFromSource=Integer.MAX_VALUE;
        /**
         * The total remaining distance based on the heuristic function used.
         */
        int distHeuristic;


        public DistMazeCell(Heuristic h, MazeCell cell, MazeCell destination){
            super(cell.getX(),cell.getY());
            distHeuristic = Heuristic.getHeuristicDistance(h,this.getX(),this.getY(),destination.getX(),destination.getY());
            this.openWall(cell.getOpenWalls());
        }

        /**
         * It returns the total distance. This is the value on which we base what our next cell is going to be.
         * @return the sum of the two distances or infinity if the sum would overflow.
         */
        public int getTotalDistance(){
            return (distFromSource+distHeuristic)<0 ? distFromSource : distFromSource+distHeuristic;
        }

        /**
         * Return the distance from the source.
         * @return {@link #distFromSource}
         */
        public int getDistFromSource() {
            return distFromSource;
        }

        /**
         * Defined so the natural ordering is based on the total distance of two cells.
         * One cell is greater if the total distance of it is greater as well.
         * @param o
         * @return
         */
        @Override
        public int compareTo(AStarSolver.DistMazeCell o) {
            return Integer.compare(this.getTotalDistance(),o.getTotalDistance());
        }
    }

    /**
     * Nested enumerator used for keeping track of the heuristic functions.
     */
    public enum Heuristic {
        NONE, MANHATTAN_DISTANCE, EUCLIDEAN;

        /**
         * Calculates the heuristic distance of two cells based on the function.
         * @param h The type of heuristic function we should use for the calculation.
         * @param x0 The x coordinate of the source
         * @param y0 The y coordinate of the source
         * @param x1 The x coordinate of the target
         * @param y1 The y coordinate of the target
         * @return an integer that guesses the remaining distance between two nodes.
         */
        public static int getHeuristicDistance(Heuristic h, int x0, int y0, int x1, int y1) {
            switch (h) {
                case MANHATTAN_DISTANCE:
                    return Math.abs(x0 - x1) + Math.abs(y0 - y1);
                case EUCLIDEAN:
                    return (int) Math.round(Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1))); //Should be used in a grid where the player can move in 8 directions
                default:
                    return 0;

            }
        }
    }

    /**
     * The current heuristic used for this solver.
     */
    Heuristic hType=Heuristic.NONE; //If the Heuristic function is NONE, the A* algorithm will be the same as the Dijkstra

    public AStarSolver(MazeMainPanel mf, int delay, Heuristic h){
        super(mf, delay);
        this.hType=h;
    }

    /**
     * 2D array that represents are maze with all the distances
     */
    private DistMazeCell[][] cells;
    /**
     * 2D array of our maze
     */
    private MazeCell[][] maze;

    /**
     * @return {@link #cells}
     */
    public DistMazeCell[][] getCells(){
        return cells;
    }

    /**
     * Returns the type of heuristic function currently in use.
     * @return {@link #hType}
     */
    public Heuristic getHType(){
        return hType;
    }

    /**
     * Solves the maze based on the A* algorithm
     * @param maze a 2D array of the maze
     * @return
     */
    @Override
    public Queue<MazeCell> solveMaze(MazeCell[][] maze) {

        Stack<MazeCell> reversePath=new Stack<>();
        Queue<MazeCell> path=new LinkedList<>();

        this.maze=maze;
        //Priority queue store the visited nodes
        Queue<DistMazeCell> openSet=new PriorityQueue<>();
        //We create a new maze with our DistMazeCell 2D array, so we can store their distances as well.
        cells=new DistMazeCell[maze.length][maze[0].length];
        //The destination where we want to be.
        MazeCell destination = Arrays.stream(maze[maze.length-1])
                .filter(x -> (x.getOpenWalls() & 0x4)==0x4)
                .findFirst().get();

        //Initializes the values with the proper values.
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                cells[row][column]=new DistMazeCell(hType, maze[row][column],destination);
            }
        }
        DistMazeCell start=cells[0][0];
        DistMazeCell end=cells[destination.getY()][destination.getX()];
        DistMazeCell current;
        //The distance of the source from itself must be zero.
        start.distFromSource=0;

        //We add the source to our priority queue.
        openSet.add(start);
        while(!openSet.isEmpty()){
            //The priority queue always returns the cell with the smallest Total Distance value,
            // which is a guess based on the distance so far and the heuristic function
            current=openSet.poll();
            //Break condition if we reached the exit.
            //This is the guaranteed shortest path, as the priority queue always returns the cell with the smallest distance in it
            //and the distance between adjacent cells are equal.
            if(current.equals(end))
                break;

            //The neighbours of the current cell.
            List<DistMazeCell> neighbours=this.getValidNeighbours(maze,cells, current);
            neighbours.stream().forEach(n->{

            });
            //We check for all neighboring cells if the distance through the current cell
            //would be smaller than the distance it currently has.
            //In that case we assign the current cell as the parent of it and set the new distance.
            for(DistMazeCell n: neighbours){
                if(current.distFromSource+1< n.distFromSource){
                    n.distFromSource= current.distFromSource+1;
                    n.assignPrevCell(current);
                    if(!openSet.contains(n))
                        openSet.add(n);
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

        }
        if(solverDelay>0) {
            try {
                Thread.sleep(solverDelay);
                mf.repaint();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //We build the path
        DistMazeCell iter=end;
        while(iter.getPrevCell()!=null){
            reversePath.add((MazeCell)iter);
            iter=(DistMazeCell)iter.getPrevCell();
        }

        path.add(maze[0][0]);
        //Reverse the stack to get the path from the source
        while(!reversePath.empty()){
            path.add(reversePath.pop());
        }
        return path;
    }

    /**
     * Uses the static method of the base class, but returns them as DistMazeCell instead of MazeCell
     * @param maze the original maze in which we want to search
     * @param cells the new maze
     * @param current the current cell
     * @return a list of DistMazeCell objects that are valid neoighbours of the current cell
     */
    public List<DistMazeCell> getValidNeighbours(MazeCell[][] maze,DistMazeCell[][] cells,DistMazeCell current){
        List<MazeCell> original=MazeSolver.getValidNeighbours(maze,current.getX(),current.getY());
        List<DistMazeCell> valid=new LinkedList<>();
        for(MazeCell c: original){
            valid.add(cells[c.getY()][c.getX()]);
        }

        return valid;
    }

    @Override
    public AStarSolver clone() throws CloneNotSupportedException {
        return new AStarSolver(mf, solverDelay, hType);
    }

    @Override
    public void draw(Graphics g) {
        if (cells != null) {
            {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setFont(new Font("default", Font.BOLD, 12));
                g2d.setColor(Color.DARK_GRAY);
                int borderSize = MazeMainPanel.getBorderSize();
                int offset = MazeMainPanel.getOffset();
                int dim = cells[0].length + cells.length;
                for (int row = 0; row < cells.length; row++) {
                    for (int column = 0; column < cells[0].length; column++) {
                        if (cells[row][column].getTotalDistance() != Integer.MAX_VALUE) {
                            int dist = cells[row][column].getDistFromSource();
                            if (dist > dim * 4 / 5)
                                g2d.setColor(Color.RED);
                            else if (dist > dim * 3 / 5) {
                                g2d.setColor(Color.ORANGE);
                            } else if (dist > dim * 2 / 5) {
                                g2d.setColor(Color.YELLOW);
                            } else {
                                g2d.setColor(Color.green);
                            }

                            g2d.drawString(Integer.toString(cells[row][column].getDistFromSource()), (offset) * (column) + offset / 2 - 5, (offset) * (row) + offset / 2 + 3);

                        }

                    }
                }

            }
        }
    }

    /**
     * Sets the mazes to null. When solveMaze is called, they will be reinitialized.
     */
    @Override
    public void reset() {
        cells=null;
        maze=null;
    }
}
