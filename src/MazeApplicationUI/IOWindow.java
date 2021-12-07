package MazeApplicationUI;

import Maze.Maze;

import javax.swing.*;
import java.io.*;
import java.util.LinkedList;

/**
 * A JDialog window used for saving or loading mazes.
 */
public class IOWindow extends JDialog{

    /**
     * A JFileChooser object used for navigating the directories.
     */
    JFileChooser fileChooser;

    public IOWindow(MainMenuFrame parent, boolean save) throws IOException {
        //Sets the modality so it "blocks" other dialog boxes, frames etc.
        super(parent, ModalityType.APPLICATION_MODAL);

        this.setResizable(false);
        fileChooser=new JFileChooser();

        //Sets the current working directory.
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        //If we want to save a maze
        if(save) {
            //result stores which button was pressed.
            //It opens a save dialog window.
            int result = fileChooser.showSaveDialog(this);

            //If we clicked the save button
            if (result == JFileChooser.APPROVE_OPTION) {
                //It saves the maze to the given file
                saveFile(fileChooser.getSelectedFile());
                //Notifies the parent frame, that a save happened.
                parent.change=true;
            }

        }
        else{
            //result stores which button was pressed.
            //It opens an open dialog window.
            int result=fileChooser.showOpenDialog(this);
            //If we clicked the load button
            if(result==JFileChooser.APPROVE_OPTION){
                loadFile(fileChooser.getSelectedFile());
                parent.change=true;
            }
        }
        this.add(fileChooser);
    }

    /**
     * Saves the serialized list of mazes to the selected file.
     * @param selected
     * @throws IOException
     */
    private void saveFile(File selected) throws IOException {
        if(!MazeApplication.mazeList.isEmpty()) {
            FileOutputStream fout = new FileOutputStream(selected);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(MazeApplication.mazeList);
        }
    }

    /**
     * Tries to load the serialized mazes from the selected file
     * @param selected
     */
    private void loadFile(File selected){
        if(!MazeApplication.mazeList.isEmpty())
            MazeApplication.mazeList.clear();
        Object temp=new LinkedList<>();
        try (FileInputStream fis = new FileInputStream(selected);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            temp = ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //Casts it as a list of mazes and assigns it to the global maze list.
        MazeApplication.mazeList=(LinkedList<Maze>)temp;

    }
}
