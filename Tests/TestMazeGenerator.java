import Maze.Maze;
import MazeApplicationUI.MazeMainPanel;
import MazeGeneratorClasses.MazeGenerator;
import MazeGeneratorClasses.MazeType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import Maze.MazeCell;
import Maze.Directions;

import java.util.List;
import java.util.Random;

public class TestMazeGenerator {

    MazeGenerator generator;
    Maze maze;

    @Before
    public void setUp(){
        maze=new Maze(5,5);
        generator=new MazeGenerator(maze.getMaze(),new MazeMainPanel(maze.getMaze(),0));
    }
    @Test
    public void testClone(){
        Assert.assertEquals(generator,generator.clone());
    }
    @Test
    public void testSetSleepDrawnTime(){
        Random rnd=new Random();
        int t=rnd.nextInt(1000);
        generator.setSleepDrawTime(t);
        Assert.assertEquals(t,generator.getSleepDrawTime());
    }
    @Test
    public void testValidNeighbours(){
        MazeCell[][] temp=maze.getMaze();
        temp[0][1].setVisited(true);
        List<MazeCell> valid=generator.returnValidNeighbours(0,0);
        Assert.assertEquals(1,valid.size());
    }

    @Test
    public void testAllNeighbours(){
        MazeCell[][] temp=maze.getMaze();
        List<MazeCell> valid=generator.returnAllNeighbours(0,0);
        Assert.assertEquals(2,valid.size());
        Assert.assertEquals(temp[0][1],valid.get(0));

        valid=generator.returnAllNeighbours(2,2);
        Assert.assertEquals(4,valid.size());
        Assert.assertEquals(temp[1][2],valid.get(0));
    }

    @Test
    public void testSetAllToUnvisited(){
        MazeCell[][] temp=maze.getMaze();
        temp[0][0].setVisited(true);
        Assert.assertTrue(temp[0][0].isVisited());
        generator.setAllToUnvisited();
        Assert.assertFalse(temp[0][0].isVisited());
    }

    @Test
    public void testResetMaze(){
        final MazeCell[][] temp=maze.getMaze();
        temp[0][0].setVisited(true);
        temp[0][0].openWall(Directions.South.getBValue());
        temp[0][0].openWall(Directions.East.getBValue());
        temp[2][2].openWall(0xf);
        generator.resetMaze();
        Assert.assertEquals(temp[0][0].getOpenWalls(),maze.getMaze()[0][0].getOpenWalls());
    }
    @Test
    public void testGenerateMaze(){
        MazeCell[][] temp=new MazeCell[maze.getMaze().length][maze.getMaze()[0].length];
        for(int w=0;w<temp.length;w++){
            for(int h=0;h<temp[0].length;h++){
                temp[h][w]=new MazeCell();
                temp[h][w].setX(w);
                temp[h][w].setY(h);
            }
        }
        generator.generateMaze();
        Assert.assertNotEquals(temp[0][0].getOpenWalls(),maze.getMaze()[0][0].getOpenWalls());

    }

}
