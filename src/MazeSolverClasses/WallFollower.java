package MazeSolverClasses;

import Maze.Directions;
import Maze.MazeCell;
import MazeApplicationUI.MazeMainPanel;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.*;

public class WallFollower extends MazeSolver{

    boolean right=true; //Will follow the right-hand path by default, otherwise the left.
    //Queue that stores the path
    Queue<MazeCell> path=new LinkedList<>();

    public Queue<MazeCell> getPath(){
        return path;
    }


    public WallFollower(boolean right, MazeMainPanel mf){
        super(mf, 0);
        this.right=right;
    }
    public WallFollower(MazeMainPanel mf){
        super(mf,0);
    }
    public WallFollower(boolean right, MazeMainPanel mf, int t){
        super(mf, t);
        this.right=right;
    }
    public WallFollower(MazeMainPanel mf, int t){this(true,mf,t);}

    @Override
    public WallFollower clone(){
        return new WallFollower(right,mf,solverDelay);
    }

    @Override
    public void draw(Graphics g) {


        if (path.size() > 0) {
            int offset = MazeMainPanel.getOffset();
            int borderSize = MazeMainPanel.getOffset();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 255, 0));
            g2d.setStroke(new BasicStroke(5));
            Queue<MazeCell> currentPath = new LinkedList<>(path);
            MazeCell last = currentPath.peek();
            GeneralPath gpath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

            gpath.moveTo( offset/2, offset/2);
            while (currentPath.size() > 0) {

                MazeCell current = currentPath.poll();
                gpath.lineTo(current.getX() * offset + offset/2, current.getY() * offset +offset/2);
                if (currentPath.size() == 1)
                    last = currentPath.peek();

            }
            g2d.draw(gpath);
            g2d.setColor(Color.RED);
            g2d.fillOval(last.getX() * offset + offset / 2  - offset / 6, last.getY() * offset + offset / 2 - offset / 6, offset / 3, offset / 3);
        }
    }

    @Override
    public void reset() {
        path.clear();
    }


    public void setFollowingHand(boolean right){
        this.right=right;
    }

    public Queue<MazeCell> solveMaze(MazeCell[][] maze){

        path.clear();
        //Starting cell
        MazeCell solver=maze[0][0];
        //It finds the destination cell.
        //The destination cell is the cell in the last row that has an open south wall.
        MazeCell destination = Arrays.stream(maze[maze.length-1])
                .filter(x -> (x.getOpenWalls() & 0x4)==0x4)
                .findFirst().get();
        path.add(solver);

        Directions currentDirection=Directions.South;
        //If there is no wall at our chosen hand in the beginning, we turn until there is.
        //In practice this means that we started in the top left corner with a left-hand solver
        //If we chose another starting position besides [0][0], this is needed!
        for(Directions d:Directions.values()){
            if((solver.getOpenWalls() & d.getBValue())==d.getBValue())
                currentDirection=d;
        }
        while(!solver.equals(destination)){

            //If there is an open wall in front of the solver
            if((currentDirection.getBValue() & solver.getOpenWalls()) == currentDirection.getBValue()){
                //If we only need to take a step, as no wall is open at our right/left hand at the next step, we will do just that.
                if((maze[solver.getY()+currentDirection.getYOffset()][solver.getX()+ currentDirection.getXOffset()].getOpenWalls() &
                        Directions.turnSideways(currentDirection,right).getBValue())
                        !=Directions.turnSideways(currentDirection,right).getBValue()) {
                    solver = maze[solver.getY() + currentDirection.getYOffset()][solver.getX() + currentDirection.getXOffset()];
                    path.add(solver);

                    if (solverDelay > 0) {
                        try {
                            Thread.sleep(solverDelay);
                            mf.repaint();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
                //Otherwise, we will follow our right/left hand as long as the next step will result in an open right/left-hand wall.
                else{

                    while (!solver.equals(destination) &&(maze[solver.getY() + currentDirection.getYOffset()][solver.getX() + currentDirection.getXOffset()].getOpenWalls() &
                            Directions.turnSideways(currentDirection, right).getBValue()) == Directions.turnSideways(currentDirection, right).getBValue()) {

                        solver = maze[solver.getY() + currentDirection.getYOffset()][solver.getX() + currentDirection.getXOffset()];
                        path.add(solver);
                        currentDirection = Directions.turnSideways(currentDirection, right);
                        if(solverDelay>0) {
                            try {
                                Thread.sleep(solverDelay);
                                mf.repaint();

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }
            //If there is a wall in front of the solver turn
            if((currentDirection.getBValue() & solver.getOpenWalls()) == 0){
                //If it is a dead end, we turn by an additional 90
                if((solver.getOpenWalls() & ((currentDirection.getBValue() << 2) % 15)) == solver.getOpenWalls())
                    currentDirection=Directions.turnSideways(currentDirection,!right);

                //Turn 90 degrees to one side
                currentDirection=Directions.turnSideways(currentDirection,!right);

                //If the next step would not result in a forced turn, we step forward.
                if( !solver.equals(destination) &&
                        (maze[solver.getY()+currentDirection.getYOffset()][solver.getX()+ currentDirection.getXOffset()].getOpenWalls() &
                                Directions.turnSideways(currentDirection,right).getBValue())
                                !=Directions.turnSideways(currentDirection,right).getBValue())
                {
                    solver = maze[solver.getY() + currentDirection.getYOffset()][solver.getX() + currentDirection.getXOffset()];
                    path.add(solver);
                }
                try {
                    Thread.sleep(solverDelay);
                    mf.repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return path;
    }

}
