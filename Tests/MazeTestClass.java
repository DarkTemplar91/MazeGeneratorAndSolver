import Maze.Maze;
import MazeApplicationUI.MazeMainPanel;
import MazeExceptions.NoMazeGeneratorFoundException;
import MazeExceptions.NoMazeSolverFoundException;
import MazeGeneratorClasses.MazeGenerator;
import MazeGeneratorClasses.MazeType;
import MazeSolverClasses.MazeSolver;
import MazeSolverClasses.WallFollower;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import Maze.MazeCell;

public class MazeTestClass {

    Maze mazeTest;
    @Before
    public void setUp(){
        mazeTest = new Maze(1,1);
    }

    @Test
    public void testConstructor(){
        int testHeight=5;
        int testWidth=10;
        Maze mazeTest = new Maze(testWidth,testHeight);
        Assert.assertEquals(testHeight,mazeTest.getMaze().length);
        Assert.assertEquals(testWidth,mazeTest.getMaze()[0].length);
    }

    @Test
    public void testAddSolver(){
        MazeCell[][] temp=new MazeCell[1][1];
        MazeSolver solver=new WallFollower(new MazeMainPanel(temp,0));
        mazeTest.addSolver(solver);
        Assert.assertEquals(solver,mazeTest.getSolver());
    }
    @Test
    public void testAddGenerator(){
        MazeCell[][] temp=new MazeCell[1][1];
        MazeGenerator mg=new MazeGenerator(temp,new MazeMainPanel(temp,0));
        mazeTest.addGenerator(mg);
        Assert.assertEquals(mg,mazeTest.getGenerator());
    }

    @Test(expected = NoMazeGeneratorFoundException.class)
    public void testNoGeneratorException() throws NoMazeGeneratorFoundException {
        mazeTest.generateMaze();
    }

    @Test(expected = NoMazeSolverFoundException.class)
    public void testNoMazeSolverException() throws NoMazeSolverFoundException {
        mazeTest.solveMaze();
    }
}
