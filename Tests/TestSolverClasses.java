import Maze.Maze;
import MazeApplicationUI.MazeMainPanel;
import MazeGeneratorClasses.MazeGenerator;
import MazeGeneratorClasses.MazeType;
import MazeSolverClasses.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import Maze.MazeCell;

import java.util.Queue;

//Tests the return values of all solvers and the methods of the AStarSolver
public class TestSolverClasses {

    Maze maze;
    MazeMainPanel mazeMainPanel;
    MazeGenerator g;

    @Before
    public void setUp(){
        maze=new Maze(5,5);
        mazeMainPanel=new MazeMainPanel(maze.getMaze(),0);
        g=new MazeGenerator(maze.getMaze(),mazeMainPanel);
        g.generateMaze();
    }

    //Checks all Solvers (except wall follower) and their corresponding paths
    @Test
    public void testPaths(){
        AStarSolver aStarSolver=new AStarSolver(mazeMainPanel,0, AStarSolver.Heuristic.MANHATTAN_DISTANCE);
        BfsSolver bfsSolver=new BfsSolver(mazeMainPanel,0);
        DfsSolver dfsSolver=new DfsSolver(mazeMainPanel,0);
        DeadEndFillingSolver deadEndFillingSolver=new DeadEndFillingSolver(mazeMainPanel,0);
        LeeRoutingAlgorithm leeRoutingAlgorithm=new LeeRoutingAlgorithm(mazeMainPanel,0);
        RecursiveSolver recursiveSolver=new RecursiveSolver(mazeMainPanel,0);
        TremauxSolver tremauxSolver=new TremauxSolver(mazeMainPanel,0);

        Queue<MazeCell> q1=aStarSolver.solveMaze(maze.getMaze());
        g.setAllToUnvisited();
        Queue<MazeCell> q2=bfsSolver.solveMaze(maze.getMaze());
        g.setAllToUnvisited();
        Queue<MazeCell> q3=dfsSolver.solveMaze(maze.getMaze());
        g.setAllToUnvisited();
        Queue<MazeCell> q4=deadEndFillingSolver.solveMaze(maze.getMaze());
        g.setAllToUnvisited();
        Queue<MazeCell> q5=leeRoutingAlgorithm.solveMaze(maze.getMaze());
        g.setAllToUnvisited();
        Queue<MazeCell> q6=recursiveSolver.solveMaze(maze.getMaze());
        g.setAllToUnvisited();
        Queue<MazeCell> q7=tremauxSolver.solveMaze(maze.getMaze());

        Assert.assertEquals(q1.size(),q2.size());
        Assert.assertEquals(q2.size(),q3.size());
        Assert.assertEquals(q3.size(),q4.size());
        Assert.assertEquals(q4.size(),q5.size());
        Assert.assertEquals(q5.size(),q6.size());
        Assert.assertEquals(q6.size(),q7.size());

        while(!q1.isEmpty()){
            Assert.assertEquals(q1.peek(),q2.peek());
            Assert.assertEquals(q2.peek(),q3.peek());
            Assert.assertEquals(q3.peek(),q4.peek());
            Assert.assertEquals(q4.peek(),q5.peek());
            Assert.assertEquals(q5.peek(),q6.peek());
            Assert.assertEquals(q6.peek(),q7.peek());
            q1.poll();
            q2.poll();
            q3.poll();
            q4.poll();
            q5.poll();
            q6.poll();
            q7.poll();

        }
    }

    @Test
    public void cloneAstar(){
        AStarSolver aStarSolver=new AStarSolver(mazeMainPanel,0, AStarSolver.Heuristic.MANHATTAN_DISTANCE);
        AStarSolver clone;
        try {
            clone=aStarSolver.clone();
            clone.solveMaze(maze.getMaze());
            aStarSolver.solveMaze(maze.getMaze());
            Assert.assertEquals(clone.getCells().length,aStarSolver.getCells().length);
            Assert.assertEquals(clone.getCells()[0].length,aStarSolver.getCells()[0].length);
            Assert.assertEquals(clone.getHType(),aStarSolver.getHType());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }

}
