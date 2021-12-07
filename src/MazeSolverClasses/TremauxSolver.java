package MazeSolverClasses;

import Maze.Directions;
import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class implements the Tremaux Algorithm. It extends the MazeSolver base class.
 * A variant of DFS-algorithm
 * It's main use is for non-simple mazes, where the walls are not necessarily connected.
 */
public class TremauxSolver extends MazeSolver{


    public TremauxSolver(MazeMainPanel mf){
        super(mf,0);
    }
    public TremauxSolver(MazeMainPanel mf, int t){
        this(mf);
        this.solverDelay=t;
    }

    /**
     * The 2D array of our maze, with markings
     */
    private MarkedCell[][] cells;
    /**
     * The current cell
     */
    private MarkedCell current;

    /**
     * return the 2D array of the marked cells
     * @return {@link #cells}
     */
    public MarkedCell[][] getCells() {
        return cells;
    }

    /**
     * Returns the current marked cell
     * @return {@link #current}
     */
    public MarkedCell getCurrent(){
        return current;
    }

    @Override
    public TremauxSolver clone(){
        return new TremauxSolver(mf,solverDelay);
    }

    @Override
    public void draw(Graphics g) {
        if(cells!=null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(5));
            int offset=MazeMainPanel.getOffset();
            for (int row = 0; row < cells.length; row++) {
                for (int column = 0; column < cells[0].length; column++) {
                    if (cells[row][column].marking > 0 || current.equals(cells[row][column])) {
                        if (current.equals(cells[row][column]))
                            g2d.setColor(Color.RED);
                        else if (cells[row][column].getMarking() == 1)
                            g2d.setColor(Color.green);
                        else if (cells[row][column].getMarking() == 2)
                            g2d.setColor(Color.black);
                        g2d.fillOval(column * offset + offset / 2- offset/6, row * offset + offset / 2-offset/6, offset / 3, offset / 3);


                    }
                }
            }
        }
    }

    @Override
    public void reset() {
        cells=null;
        current=null;
    }

    /**
     * Solves the maze using the Tremaux algorithm.
     * The Tremaux algorithm is used for non-simple mazes, where the wall-follower would not work,
     * as there are multiple sets of walls that are not connected to each other.
     * @param maze a 2D array of the maze
     * @return a queue with the path from source to destination cell
     */
    public Queue<MazeCell> solveMaze(MazeCell[][] maze){

        Queue<MarkedCell> path=new LinkedList<>();
        //Create a new 2D array with MarkedCells, so we can store the amount of time we have been there.
        cells=new MarkedCell[maze.length][maze[0].length];
        for(int row=0;row<maze.length;row++){
            for(int column=0;column<maze[0].length;column++){
                cells[row][column]=new MarkedCell(maze[row][column]);
            }
        }
        //Sets current cell
        current=cells[0][0];
        //Labels starting cell as already visited.
        current.setVisited(true);
        MarkedCell start=current;
        MarkedCell destination = Arrays.stream(cells[cells.length-1])
                .filter(x -> (x.getOpenWalls() & 0x4)==0x4)
                .findFirst().get();
        //Initial direction we want to go
        Directions d=Directions.South;

        //loop stops when we are at the destination cell
        while(!current.equals(destination)){
            //adds the current cell to the path
            path.add(current);
            //increments the amount of times we have been at this particular cell.
            current.marking++;

            //Traverse the corridor
            if(current.openWallNumber()==2){
                //If our current direction is not valid, we turn
                if((current.getOpenWalls() & d.getBValue()) !=d.getBValue()){
                    Directions from=Directions.turnSideways(d,true);
                    from=Directions.turnSideways(from,true);
                    for(Directions dir: Directions.values()){
                        if((!from.equals(dir)) && ((current.getOpenWalls() & dir.getBValue()) == dir.getBValue())){
                            d=dir;
                        }
                    }
                }
                //set the next current cell
                current=cells[current.getY()+d.getYOffset()][current.getX()+d.getXOffset()];

            }
            //Dead-end
            else if(current.openWallNumber()==1){
                //Mark it as a dead-end
                current.marking=2;
                current.setVisited(true);
                //Turn around
                d=Directions.turnSideways(d,true);
                d= Directions.turnSideways(d,true);
                current=cells[current.getY()+d.getYOffset()][current.getX()+d.getXOffset()];
            }
            //If it is a junction
            else if(current.openWallNumber()>2){
                current.setVisited(true);
                //If this is the second time at this junction, that means that we had to turn back due to a dead-end or loop.
                //if that happens, we set the marking back to one.
                if(current.marking==2)
                    current.marking=1;

                //Store the direction where we came from (180° of our current one)
                Directions from=Directions.turnSideways(d,true);
                from= Directions.turnSideways(from,true);
                //List of valid cells
                List<MarkedCell> valid=current.getValidNeighbours(cells);
                //The amount of cells in the junction, which are yet to be visited
                long count = valid.stream().filter(c -> c.marking==0).count();

                Random rnd=new Random();
                int idx=0;

                //We must trace back, as we exhausted all our other options
                if(count==0 && valid.size()==1){
                    current.marking=2;
                    d=Directions.getOffsetDirection(current.getX(),current.getY(),valid.get(idx).getX(),valid.get(idx).getY());
                    current=valid.get(0);
                }
                //If there is only one way we can go, and we can go there.
                else if(count==1 && valid.size()==count){
                    d=Directions.getOffsetDirection(current.getX(),current.getY(),valid.get(idx).getX(),valid.get(idx).getY());
                    current=valid.get(0);
                }
                //This case could only exist if the previous cell was also a junction.
                //In that case we do not want to go the way we came from, but choose one of the other unvisited cells
                else if(count==valid.size()){
                    //If the prev cell based on the current direction is valid, we remove the cell where we came from
                    if(valid.size()!=1 && current.getY()+from.getYOffset()>=0 && current.getY()+from.getYOffset()<cells.length&&
                            current.getX()+from.getXOffset()>=0 && current.getX()+from.getXOffset()<cells[0].length){
                        final MarkedCell prevRef=cells[current.getY()+ from.getYOffset()][current.getX()+from.getXOffset()];
                        valid.removeIf(c->c.equals(prevRef));

                    }
                    idx=rnd.nextInt(valid.size());
                    d=Directions.getOffsetDirection(current.getX(),current.getY(),valid.get(idx).getX(),valid.get(idx).getY());
                    current=valid.get(idx);
                }
                //If there are only unvisited corridors (and where we came from)
                else if(count==valid.size()-1 || (count==valid.size() && current.getX()+from.getXOffset()<=0 && current.getY()+from.getYOffset()<=0 &&
                        current.getX()+from.getXOffset()>=cells[0].length && current.getY()+from.getYOffset()>= cells.length)){
                    //we remove the cell from which we came from
                    if(valid.size()!=1)
                        valid.removeIf(c->c.marking==1);
                    //we select a cell randomly
                    idx=valid.size()==1 ? 0 :rnd.nextInt(valid.size());
                    d=Directions.getOffsetDirection(current.getX(),current.getY(),valid.get(idx).getX(),valid.get(idx).getY());
                    current=valid.get(idx);


                }
                //If one or more of the paths have already been visited
                else{
                    final MarkedCell prevRef=cells[current.getY()+ from.getYOffset()][current.getX()+from.getXOffset()];
                    //If the previous cell has only been visited once, and we arrive at a junction where we have already been, we must go back
                    //This can happen, if there are loops in the maze, or if we had to backtrace.
                    if(prevRef.marking==1){
                        current=prevRef;
                        d=from;
                    }
                    //Else we choose the path with the lowest marking (if they are equal, then the last one
                    else{
                        idx=0;
                        for(int i=0;i<valid.size();i++){
                            if(valid.get(idx).marking>valid.get(i).marking){
                                idx=i;
                            }
                        }
                        d=Directions.getOffsetDirection(current.getX(),current.getY(),valid.get(idx).getX(),valid.get(idx).getY());
                        current=valid.get(idx);
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

        }


        path.add(start);
        Iterator<MarkedCell> iter = path.iterator();
        while (iter.hasNext()) {
            MarkedCell temp = iter.next();

            if (temp.marking==2)
                iter.remove();
        }
        path.add(destination);
        Set<MazeCell> set=new LinkedHashSet<>(path); //removes duplicates
        return new LinkedList<>(set);

    }

    /**
     * Nested class that extends from MazeCell.
     * It is used to store additional information regarding the amount
     * of times the cell has been visited.
     */
    public class MarkedCell extends MazeCell{
        int marking=0;

        public int getMarking() {
            return marking;
        }
        public MarkedCell(MazeCell cell){
            this.setX(cell.getX());
            this.setY(cell.getY());
            this.openWall(cell.getOpenWalls());

        }

        /**
         * Returns all the valid neighbours using the static base method of the MazeSolver. Then it casts it, so it returns a MarkedCell list.
         * @param maze the 2D array of our maze made up of MarkedCells
         * @return a list of MarkedCells that are neighbours of the current one.
         */
        public List<MarkedCell> getValidNeighbours(MarkedCell[][] maze){
            return new java.util.ArrayList<>(MazeSolver.getValidNeighbours(maze,this.getX(),this.getY()).stream().filter(c->c.marking<2).toList());
        }
    }
}
