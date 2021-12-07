package MazeApplicationUI;
import Maze.Maze;

import java.util.LinkedList;
import java.util.List;

/**
 * This is our main application. It simply creates our main frame and sets it to visible.
 */
public class MazeApplication {

    public static List<Maze> mazeList=new LinkedList<>();
    public static void main(String[] args){
        MainMenuFrame menuFrame=new MainMenuFrame();
        menuFrame.setVisible(true);

    }

}
