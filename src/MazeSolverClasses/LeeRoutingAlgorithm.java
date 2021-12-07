package MazeSolverClasses;

import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Implements Lee's algorithm, one of the Routing algorithms. Inherits from MazeSolver.
 */
public class LeeRoutingAlgorithm extends MazeSolver{

    /**
     * Stores each cell's distance from the source
     */
    private int[][] distances;



    public LeeRoutingAlgorithm(MazeMainPanel mf, int t){
        super(mf, t);
    }

    /**
     * Implements Lee's algorithm.
     * It makes use of the fact that a neighboruing cell has a distance +-1 from the current one.
     * We check the neighbours of the cells, if they are yet to be visited we assign the incremented value.
     * @param maze a 2D array of the maze
     * @return a queue with the path to the exit
     */
    @Override
    public Queue<MazeCell> solveMaze(MazeCell[][] maze) {
        //We create a 2d array of ints to store the distances
        distances=new int[maze.length][maze[0].length];
        //initialize the distances 2D array by assigning a value of -1 to each, except the source cell.
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                if(column==0 && row==0)
                    distances[row][column]=0;
                else
                    distances[row][column]=-1;
                maze[row][column].setVisited(false);
            }
        }
        //The stack that stores the reverse of the path.
        Stack<MazeCell> path=new Stack<>();
        //The source cell
        MazeCell source=maze[0][0];
        //The destination we want to reach.
        MazeCell destination = Arrays.stream(maze[maze.length-1])
                .filter(x -> (x.getOpenWalls() & 0x4)==0x4)
                .findFirst().get();
        MazeCell current=source;
        source.setVisited(true);
        int currentDist=0;

        //If the destination has been found, break is called with this label.
        outerLoop:
        while(true){
            //It returns all the cells with the current distance.
            List<MazeCell> getAllWithDist=getAllWithDist(maze,distances,currentDist++);
            //If we have already considered the neighbours of that cell, we remove it from the list.
            getAllWithDist.removeIf(c->!c.isVisited());

            if(solverDelay>0) {
                try {
                    Thread.sleep(solverDelay / 2);
                    mf.repaint();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //For all the cells with the given distance
            for(MazeCell curr:getAllWithDist){
                current=curr;
                //We select the neighbours, then remove the already visited ones
                List<MazeCell> valid=MazeSolver.getValidNeighbours(maze, current.getX(),current.getY());
                valid.removeIf(c->c.isVisited());

                //Propagation
                //We set the distance of those cells to +1 of the current cell.
                for(MazeCell cell : valid){
                    distances[cell.getY()][cell.getX()]=distances[curr.getY()][curr.getX()]+1;
                    cell.setVisited(true);
                    if(cell.equals(destination))
                        break outerLoop;
                }
            }


        }

        //Builds path
        current=destination;
        while(!current.equals(maze[0][0])){

            path.add(current);
            List<MazeCell> valid=MazeSolver.getValidNeighbours(maze, current.getX(),current.getY());
            Iterator<MazeCell> iter=valid.iterator();
            while(iter.hasNext()){
                MazeCell cell=iter.next();
                if(distances[cell.getY()][cell.getX()]<0)
                    iter.remove();
            }
            int maxDist=distances[current.getY()][current.getX()];
            int idx=0;
            for(int i=0;i<valid.size();i++){
                if(maxDist>distances[valid.get(i).getY()][valid.get(i).getX()]){
                    idx=i;
                    maxDist=distances[valid.get(i).getY()][valid.get(i).getX()];
                }

            }
            current=valid.get(idx);
        }

        Queue<MazeCell> solution=new LinkedList<>();

        path.add(maze[0][0]);
        while(!path.isEmpty()){
            solution.add(path.pop());
        }


        return solution;

    }

    /**
     * Looks at the 2D array and returns all the MazeCells in it with a distance of i.
     * @param maze the 2D maze we are looking at
     * @param dist the corresponding distacne values of each cell
     * @param i the distance we are looking for
     * @return list of cells with a distance of i.
     */
    public List<MazeCell> getAllWithDist(MazeCell[][] maze, int[][] dist ,int i){
        List<MazeCell> valid=new LinkedList<>();
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                if(dist[row][column]==i){
                    valid.add(maze[row][column]);
                }
            }
        }
        return valid;
    }

    @Override
    public LeeRoutingAlgorithm clone() throws CloneNotSupportedException {
        return new LeeRoutingAlgorithm(mf,solverDelay);
    }

    @Override
    public void draw(Graphics g) {
        if (distances != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setFont(new Font("default", Font.BOLD, 12));
                g2d.setColor(Color.DARK_GRAY);
                int offset = MazeMainPanel.getOffset();
                int dim = distances[0].length + distances.length;
                for (int row = 0; row < distances.length; row++) {
                    for (int column = 0; column < distances[0].length; column++) {
                        if (distances[row][column] > 0) {
                            int dist = distances[row][column];
                            if (dist > dim * 4 / 5)
                                g2d.setColor(Color.RED);
                            else if (dist > dim * 3 / 5) {
                                g2d.setColor(Color.ORANGE);
                            } else if (dist > dim * 2 / 5) {
                                g2d.setColor(Color.YELLOW);
                            } else {
                                g2d.setColor(Color.green);
                            }
                            g2d.drawString(Integer.toString(distances[row][column]), (offset) * (column) + offset / 2 - 5, (offset) * (row) + offset / 2 + 3);

                        }

                    }
                }
            }
        }

    @Override
    public void reset() {
        distances=null;
    }
}
