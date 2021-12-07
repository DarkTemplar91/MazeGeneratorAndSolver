package MazeApplicationUI;

import Maze.Maze;
import Maze.MazeCell;
import MazeExceptions.NoMazeGeneratorFoundException;
import MazeExceptions.NoMazeSolverFoundException;
import MazeGeneratorClasses.*;
import MazeSolverClasses.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

/**
 * The main frame of the application.
 * This is where the user can add new mazes, solvers and generators and save or load them.
 */
public class MainMenuFrame extends JFrame implements ActionListener {

    /**
     * Button used for opening the add maze dialog window.
     */
    JButton mazeAddButton;
    /**
     * Button used for opening the add solver dialog window
     */
    JButton solverAddButton;
    /**
     * The amount of mazes we currently have.
     * Max value is 3.
     */
    int mazeCount=0;
    /**
     * This is the panel which we draw our mazes.
     * Its layout is GridLayout
     */
    JPanel paintArea;
    /**
     * JComboBox used for selecting the maze to which we want to assign a solver.
     */
    JComboBox<Integer> box;
    /**
     * List of MazeMainPanels where we draw our mazes.
     */
    List<MazeMainPanel> panels;
    /**
     * Button used for regenerating the mazes.
     */
    JButton regenerate;
    /**
     * Button used for starting the solving process of the mazes.
     */
    JButton solveButton;
    /**
     * Button used for opening the load dialog window.
     */
    JButton loadButton;
    /**
     * Button used for opening the save dialog window
     */
    JButton saveButton;

    /**
     * The open/save dialog window changes this value if a change was made, notifying the frame, that
     * certain operations can be executed.
     * This is basically used, so nothing executes if the dialog window was closed before selecting a file.
     */
    boolean change=false;



    public MainMenuFrame(){

        panels=new LinkedList<>();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(false);
        this.setSize(screenSize);

        regenerate=new JButton("Regenerate All Mazes");
        solveButton=new JButton("Solve Mazes");
        solveButton.addActionListener(this);
        regenerate.addActionListener(this);


        loadButton=new JButton("Load Mazes");
        saveButton=new JButton("Save Mazes");

        loadButton.addActionListener(this);
        saveButton.addActionListener(this);


        mazeAddButton = new JButton("Add Maze");

        JPanel northPanel=new JPanel();
        northPanel.add(loadButton);
        northPanel.add(mazeAddButton);
        northPanel.add(saveButton);

        this.add(northPanel, BorderLayout.NORTH);
        mazeAddButton.addActionListener(this);



        JPanel bottom=new JPanel();
        bottom.setLayout(new BoxLayout(bottom,BoxLayout.X_AXIS));
        JPanel bottomFlow=new JPanel();
        bottomFlow.setLayout(new FlowLayout());
        bottomFlow.add(bottom);
        JPanel bottomBorder=new JPanel(new BorderLayout());
        bottomBorder.add(regenerate,BorderLayout.WEST);
        bottomBorder.add(bottomFlow,BorderLayout.CENTER);
        bottomBorder.add(solveButton,BorderLayout.EAST);
        solverAddButton=new JButton("Add Solver to maze no.:");
        bottom.add(solverAddButton);
        solverAddButton.addActionListener(this);
        box=new JComboBox<>();
        bottom.add(box);
        this.add(bottomBorder,BorderLayout.SOUTH);


        paintArea=new JPanel(new GridLayout(1,3));
        this.add(paintArea, BorderLayout.CENTER);

        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(mazeAddButton.getActionCommand())) {
            if (mazeCount > 2) {
                //TODO error message
            } else {
                Thread t1=new Thread(this::addMaze);
                t1.start();
            }
        } else if (e.getActionCommand().equals(solverAddButton.getActionCommand())) {
            //If we have mazes, it opens up the add solver dialog window, where we add a solver to the selected maze
            if (mazeCount > 0) {
                if (box.getSelectedItem() != null)
                    addSolverTo((Integer) box.getSelectedItem() - 1);
            }
        } else if (e.getActionCommand().equals(regenerate.getActionCommand())) {
            //Regenerates all of our mazes.
            for (int i = 0; i < MazeApplication.mazeList.size(); i++) {
                if(panels.get(i).isFinished()) {
                    if(MazeApplication.mazeList.get(i).getSolver()!=null)
                        MazeApplication.mazeList.get(i).getSolver().reset();
                    final int index = i;
                    Thread t1 = new Thread(() -> {
                        try {
                            MazeApplication.mazeList.get(index).generateMaze();
                            panels.get(index).setFinished(true);

                        } catch (NoMazeGeneratorFoundException ex) {
                            ex.printStackTrace();
                        }
                        if (panels.get(index).getPath() != null)
                            panels.get(index).getPath().clear();
                        panels.get(index).repaint();
                    });
                    t1.start();

                }
                this.validate();
            }

        } else if (e.getActionCommand().equals(solveButton.getActionCommand())) {
            //For each maze, if it has a solver, it solves the maze in a new thread.
            for (int i = 0; i < MazeApplication.mazeList.size(); i++) {
                if(panels.get(i).isFinished()) {
                    panels.get(i).setFinished(false);
                    final int index = i;
                    Thread t1 = new Thread(() -> {
                        try {
                            if (MazeApplication.mazeList.get(index).getSolver() != null) {
                                panels.get(index).setPath(MazeApplication.mazeList.get(index).solveMaze());
                                panels.get(index).setFinished(true);
                                panels.get(index).repaint();
                            }

                        } catch (NoMazeSolverFoundException ex) {
                            ex.printStackTrace();
                        }
                    });
                    t1.start();
                }

            }
            this.validate();
        } else if (e.getActionCommand().equals(saveButton.getActionCommand())) {
            //Opens up a save dialog window.
                try {
                    IOWindow loadWindow = new IOWindow(this, true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                change=false;


        } else if (e.getActionCommand().equals(loadButton.getActionCommand())) {
            //Opens up a save dialog window.
            try {
                IOWindow loadWindow = new IOWindow(this, false);
                //Only execute if a file was actually loaded
                if (change) {
                    //Removes the numbers from the JComboBox
                    box.removeAllItems();
                    //Removes all panels of the previous mazes.
                    panels.clear();
                    //Removes all JComponent from the paint area.
                    paintArea.removeAll();
                    //Resets it to its original state, where it can store 3 panels.
                    paintArea = new JPanel(new GridLayout(1, 3));

                    //Creates new panels for the mazes to be drawn on and adds the correct number to the ComboBox
                    mazeCount = 0;
                    for (int i = 0; i < MazeApplication.mazeList.size(); i++) {

                        panels.add(mazeCount, new MazeMainPanel(MazeApplication.mazeList.get(i).getMaze(), mazeCount));
                        JScrollPane scrollPane = new JScrollPane(panels.get(i));
                        scrollPane.createHorizontalScrollBar();
                        paintArea.add(scrollPane, mazeCount);
                        box.addItem(++mazeCount);
                    }
                    this.add(paintArea);
                }
                change=false;

            } catch (IOException ex) {
                ex.printStackTrace();
            }



        }
    }

    /**
     * Opens up an add maze dialog window, waits for its results.
     * If a new maze was added, we get the necessary variables and generate a new Maze.
     */
    public void addMaze() {
        MazeAdderWindow adderWindow = new MazeAdderWindow(this);
        adderWindow.setVisible(true);
        //If the dialog window was not disposed
        if (adderWindow.getMazeType()!=null && adderWindow.getMazeWidth()!=0 && adderWindow.getMazeHeight()!=0) {
            box.addItem(mazeCount + 1);
            MazeApplication.mazeList.add(new Maze(adderWindow.getMazeWidth(), adderWindow.getMazeHeight()));
            MazeCell[][] mazeCells = MazeApplication.mazeList.get(mazeCount).getMaze();
            MazeGenerator generator;
            MazeMainPanel mf = new MazeMainPanel(mazeCells, mazeCount);
            panels.add(mf);
            int delay=adderWindow.getDelay();


            //Based of the type of Maze the user selected, it creates a new MazeGenerator object
            switch (adderWindow.getMazeType()) {
                case RPrimsGenerator -> generator = new RPrimsGenerator(mazeCells, mf,delay);
                case WilsonGenerator -> generator = new WilsonsGenerator(mazeCells, mf,delay);
                case RecursiveDivision -> generator = new RecursiveDivisionGenerator(mazeCells, mf,delay);
                case RKruskalsGenerator -> generator = new RKruskalsGenerator(mazeCells, mf,delay);
                case AldousBroderGenerator -> generator = new AldousBroderGenerator(mazeCells, mf,delay);
                default -> generator = new MazeGenerator(mazeCells, mf,delay);
            }

            //Adds our new generator to the maze.
            MazeApplication.mazeList.get(mazeCount).addGenerator(generator);
            JScrollPane scrollPane = new JScrollPane(mf);
            scrollPane.createHorizontalScrollBar();
            paintArea.add(scrollPane, mazeCount);

            paintArea.validate();
            scrollPane.setVisible(true);
            mf.setVisible(true);

            mazeCount++;
            //It tries to generate a maze
            try {
                MazeApplication.mazeList.get(mazeCount-1).generateMaze();

            } catch (NoMazeGeneratorFoundException e) {
                e.printStackTrace();
            }

            this.validate();
            mf.validate();
            this.repaint();
        }

    }

    /**
     * Opens up a dialog window where we can add a maze solver to one our mazes.
     * @param i is the number corresponding to the maze to which we want to add a new solver.
     */
    public void addSolverTo(int i){
        SolverDialogWindow solverDialogWindow=new SolverDialogWindow(this);
        solverDialogWindow.setVisible(true);

        //If the dialog window was not destroyed
        if(solverDialogWindow.getSolverType()!=null && solverDialogWindow.getSolverDelay()!=-1){
            //Gets the solver type that the user selected.
            SolverType solverType=solverDialogWindow.getSolverType();
            AStarSolver.Heuristic heuristic= AStarSolver.Heuristic.NONE;
            boolean right=true;
            //Gets the solverDelay
            int solverDelay=solverDialogWindow.getSolverDelay();
            //If the user selected the A* algorithm, we must also fetch the heuristic type that was selected.
            if(solverType.equals(SolverType.AStarSolver)){
                heuristic=solverDialogWindow.getHeuristicType();
            }
            else if(solverType.equals(SolverType.WallFollower)){
                right=solverDialogWindow.getHand();
            }
            MazeSolver solver;
            //Creates a solver based on the user input.
            switch (solverType) {
                case BFSSolver -> solver=new BfsSolver(panels.get(i),solverDelay);
                case DFSSolver -> solver=new DfsSolver(panels.get(i),solverDelay);
                case DeadEndFillingSolver -> solver=new DeadEndFillingSolver(panels.get(i),solverDelay);
                case AStarSolver -> solver=new AStarSolver(panels.get(i),solverDelay, heuristic);
                case LeeRoutingAlgortihm -> solver=new LeeRoutingAlgorithm(panels.get(i),solverDelay);
                case RecursiveSolver -> solver=new RecursiveSolver(panels.get(i),solverDelay);
                case TremauxSolver -> solver=new TremauxSolver(panels.get(i),solverDelay);
                default -> solver=new WallFollower(right,panels.get(i),solverDelay);
            }

            //Adds the solver
            MazeApplication.mazeList.get(i).addSolver(solver);
            panels.get(i).setSolver(MazeApplication.mazeList.get(i).getSolver());
        }
    }
}
