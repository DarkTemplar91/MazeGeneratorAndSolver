package MazeApplicationUI;

import MazeGeneratorClasses.MazeType;
import MazeSolverClasses.AStarSolver;
import MazeSolverClasses.SolverType;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is a class that extends the JDialog class. It is used for adding a Maze Solver to the maze.
 * Interaction between the user and the program.
 */
public class SolverDialogWindow extends JDialog implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(addButton.getActionCommand())){

            if(!delayField.getText().isEmpty()){
                delay=Integer.parseInt(delayField.getText());

            }
            solverType=(SolverType)typeSelector.getSelectedItem();
            if(delay>-1){
                this.setVisible(false);
            }
            else{
                errorLabel.setForeground(Color.RED);
                errorLabel.setText("Value Entered must be at least 0!");
            }
        }
        else if(e.getActionCommand().equals(exitButton.getActionCommand())){
            this.dispose();
        }
        else if(e.getActionCommand().equals(typeSelector.getActionCommand())){
            if(typeSelector.getSelectedItem().equals(SolverType.AStarSolver)){
                CardLayout cl = (CardLayout)(card.getLayout());
                cl.show(card, "card");
                this.repaint();
            }
            else if(typeSelector.getSelectedItem().equals(SolverType.WallFollower)){
                CardLayout cl = (CardLayout)(card.getLayout());
                cl.show(card, "handRule");
                this.repaint();
            }
            else{
                CardLayout cl = (CardLayout)(card.getLayout());
                cl.show(card,"empty");
            }
        }

    }


    JButton addButton;
    JButton exitButton;
    /**
     * With this, the user can select the type of solver they want to add.
     */
    JComboBox<SolverType> typeSelector;
    /**
     * If the selected type is A*, the user can select a Heuristic function as well.
     */
    JComboBox<AStarSolver.Heuristic> heuristicSelector;
    /**
     * JPanel with card layout, used for having different contents for different solver types
     */
    JPanel card;
    /**
     * The field where we can enter the amount of time the solver will sleep after each sub-step.
     */
    JTextField delayField;
    /**
     * An enum variable, that stores the type of solver the user selected from the JComboBox.
     */
    SolverType solverType;
    int delay=-1;
    JLabel errorLabel;
    JRadioButton rightButton;
    JRadioButton leftButton;


    /**
     *
     * @return {@link #solverType}, which is the type of Maze Solver, the user wishes to add to the maze.
     */
    public SolverType getSolverType(){
        return (SolverType)typeSelector.getSelectedItem();
    }

    /**
     * @return the currently selected heuristic function for A*.
     */
    public AStarSolver.Heuristic getHeuristicType(){
        return (AStarSolver.Heuristic) heuristicSelector.getSelectedItem();
    }

    /**
     * @return {@link #delay}, the amount of time the thread sleeps in ms.
     */
    public int getSolverDelay(){
        return delay;
    }

    /**
     * If the wallFollower is selected, it will return the hand that will be used for the WallFollower
     * @return a boolean, if true the right hand was selected, otherwise the left.
     */
    public boolean getHand(){
        if(rightButton.isSelected())
            return true;
        return false;
    }

    public SolverDialogWindow(JFrame parent){
        super(parent,ModalityType.APPLICATION_MODAL);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screenSize.width;
        int h = screenSize.height;
        this.setSize(w / 5, h / 5);
        this.setResizable(false);
        this.setTitle("Select a solver:");

        typeSelector = new JComboBox<>(SolverType.values());
        typeSelector.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                value = ((SolverType) value).toString();
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        JLabel label=new JLabel("Solver algorithm:",JLabel.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton=new JButton("Add solver to maze");
        exitButton=new JButton("Exit");
        JPanel bottomButtons=new JPanel();
        bottomButtons.add(addButton);
        bottomButtons.add(exitButton);
        this.add(bottomButtons, BorderLayout.SOUTH);
        JPanel selectorPanel=new JPanel();
        JPanel flowMiddle=new JPanel();
        flowMiddle.add(selectorPanel);
        selectorPanel.setLayout(new BoxLayout(selectorPanel,BoxLayout.Y_AXIS));
        selectorPanel.add(label);
        JLabel label2=new JLabel("Solver Delay (in ms): ",JLabel.CENTER);
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);
        delayField=new JTextField("0",4);

        JPanel delayPanel=new JPanel();
        delayPanel.add(label2);
        delayPanel.add(delayField);
        selectorPanel.add(typeSelector);
        selectorPanel.add(delayPanel);
        errorLabel=new JLabel(" ",SwingConstants.CENTER);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectorPanel.add(errorLabel);
        card=new JPanel(new CardLayout());
        heuristicSelector=new JComboBox<>(AStarSolver.Heuristic.values());
        heuristicSelector.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                value = ((AStarSolver.Heuristic) value).toString();
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        rightButton=new JRadioButton("Right-hand rule");
        leftButton=new JRadioButton("Left-hand rule");
        JPanel handPanel=new JPanel();
        handPanel.add(rightButton);
        handPanel.add(leftButton);
        ButtonGroup group = new ButtonGroup();
        group.add(rightButton);
        group.add(leftButton);
        rightButton.setSelected(true);
        card.add(new JLabel(),"empty");
        JPanel aPanel=new JPanel();
        aPanel.add(new JLabel("Heuristic Function: "));
        aPanel.add(heuristicSelector);
        card.add(aPanel,"card");
        card.add(handPanel,"handRule");
        selectorPanel.add(card);


        exitButton.addActionListener(this);
        addButton.addActionListener(this);
        typeSelector.addActionListener(this);

        PlainDocument solverDoc=(PlainDocument) delayField.getDocument();
        solverDoc.setDocumentFilter(new DocumentFilter(){
            public void replace(DocumentFilter.FilterBypass fb, int offs, int length,
                                String str, AttributeSet a) throws BadLocationException {

                String text = fb.getDocument().getText(0,
                        fb.getDocument().getLength());
                text += str;
                if ((fb.getDocument().getLength() + str.length() - length) <= 4
                        && text.matches("^[1-9][0-9]{0,3}?$")) {
                    super.replace(fb, offs, length, str, a);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
            public void insertString(DocumentFilter.FilterBypass fb, int offs, String str,
                                     AttributeSet a) throws BadLocationException {

                String text = fb.getDocument().getText(0,
                        fb.getDocument().getLength());
                text += str;
                if ((fb.getDocument().getLength() + str.length()) <= 4
                        && text.matches("^[1-9][0-9]{0,3}?$")) {
                    super.insertString(fb, offs, str, a);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });

        this.add(flowMiddle,BorderLayout.CENTER);


    }
}
