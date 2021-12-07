package MazeApplicationUI;


import Maze.MazeCell;
import MazeGeneratorClasses.MazeGenerator;
import MazeGeneratorClasses.MazeType;
import MazeSolverClasses.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The panel used to draw the maze.
 */
public class MazeMainPanel extends JPanel {

    /**
     * A 2D array that corresponds to our maze
     */
    private MazeCell[][] maze;
    /**
     * The solution of the maze
     */
    private Queue<MazeCell> path;
    /**
     * A static offset. This is the dimension of a single cell
     */
    private static int offset = 30;
    /**
     * The size of our brush. It corresponds to the size of the walls.
     */
    private static int borderSize = 5;
    /**
     * The index of the panel in its parent panel, that has a boxLayout
     */
    final int idx;
    /**
     * It indicates whether the panel is currently being drawn on.
     */
    private boolean finished=true;
    /**
     * The solver that was assigned to the maze. It will handle the drawing of the solution.
     */
    private MazeSolver solver;


    /**
     * Sets the solver
     * @param solver
     */
    public void setSolver(MazeSolver solver) {
        this.solver = solver;
    }

    /**
     * Sets the maze
     * @param maze
     */
    public void setMaze(MazeCell[][] maze){
        this.maze=maze;
    }

    /**
     * @return {@link #finished}, which is a boolean that indicates, if the drawing process has been finished or not.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Sets the finished boolean to the passed value.
     * @param finished
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }


    /**
     * Sets the solution of the maze.
     * @param path
     */
    public void setPath(Queue<MazeCell> path){
        this.path=path;
    }

    /**
     * @return {@link #path}, which is the solution to the maze.
     */
    public Queue<MazeCell> getPath() {
        return path;
    }

    public MazeMainPanel(MazeCell[][] maze, int count) {
        Dimension d = new Dimension((maze[0].length + 1) * offset, maze.length * offset);
        this.setPreferredSize(d);
        this.maze = maze;
        idx=count;

        this.setVisible(true);

    }

    //Not used
    public static void setOffset(int offset) {
        MazeMainPanel.offset = offset;
    }
    public static int getOffset(){
        return offset;
    }
    public static int getBorderSize() {
        return borderSize;
    }
    public static void setBorderSize(int borderSize) throws Exception {
        if (borderSize > offset / 2)
            throw new Exception("Invalid Border size!");
        MazeMainPanel.borderSize = borderSize;
    }

    /**
     * This method draws the maze. It will be called in the paintComponent method.
     * @param g Graphics object used for drawing.
     */
    public void drawMaze(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(borderSize));
        g2d.setColor(new Color(0, 0, 0));
        for (int h = 0; h < maze.length; h++) {
            for (int w = 0; w < maze[0].length; w++) {
                int walls = maze[h][w].getOpenWalls();
                if ((walls & 0x1) != 0x1) {
                    g2d.drawLine(offset * w + borderSize / 2, offset * h + borderSize / 2, offset * (w + 1) + borderSize / 2, offset * h + borderSize / 2);
                }
                if ((walls & 0x2) != 0x2) {
                    g2d.drawLine(offset * (w + 1) + borderSize / 2, offset * h + borderSize / 2, offset * (w + 1) + borderSize / 2, offset * (h + 1) + borderSize / 2);
                }
                if ((walls & 0x4) != 0x4) {
                    g2d.drawLine(offset * w + borderSize / 2, offset * (h + 1) + borderSize / 2, offset * (w + 1) + borderSize / 2, offset * (h + 1) + borderSize / 2);
                }
                if ((walls & 0x8) != 0x8) {
                    g2d.drawLine(offset * w + borderSize / 2, offset * h + borderSize / 2, offset * w + borderSize / 2, offset * (h + 1) + borderSize / 2);
                }
            }
        }

    }

    /**
     * Draws the solution to the maze.
     * @param g Graphics object used for drawing
     * @param path The solution to our maze.
     */
    public void drawSolution(Graphics g, Queue<MazeCell> path) {
        if (path.size() > 0) {

            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 255, 0));
            g2d.setStroke(new BasicStroke(5));
            Queue<MazeCell> currentPath = new LinkedList<>(path);
            MazeCell last = currentPath.peek();
            GeneralPath gpath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

            gpath.moveTo(offset / 2 , offset / 2 );
            while (currentPath.size() > 0) {

                MazeCell current = currentPath.poll();
                gpath.lineTo(current.getX() * offset + offset / 2, current.getY() * offset + offset / 2 );
                if (currentPath.size() == 1)
                    last = currentPath.peek();

            }
            g2d.draw(gpath);
            g2d.setColor(Color.RED);
            g2d.fillOval(last.getX() * offset + offset / 2  - offset / 6, last.getY() * offset + offset / 2 - offset / 6, offset / 3, offset / 3);
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMaze(g);
        if(solver!=null)
            solver.draw(g);
        //This method will only be called, after the solver finished the solving process.
        if (path!=null && finished) {
            drawSolution(g, path);
            this.validate();
        }
        this.validate();
    }
}
