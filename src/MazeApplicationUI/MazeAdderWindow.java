package MazeApplicationUI;

import Maze.Maze;
import MazeGeneratorClasses.AldousBroderGenerator;
import MazeGeneratorClasses.MazeGenerator;
import MazeGeneratorClasses.MazeType;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A dialog window used for adding new Maze Generators to the Main frame
 */
public class MazeAdderWindow extends JDialog implements ActionListener {

    /**
     * The width of the maze
     */
    private int mazeWidth=0;
    /**
     * The height of the maze
     */
    private int mazeHeight=0;
    /**
     * The field in which the user can enter the width of the maze.
     */
    private JTextField widthField;
    /**
     * The field in which the user can enter the height of the maze.
     */
    private JTextField heightField;
    /**
     * This is where our error messages go
     */
    private JLabel errorLabel;
    /**
     * The type of maze generation algorithm that the user wants to use.
     */
    private MazeType mazeType;
    /**
     * This is how the user selects the algorithm
     */
    private JComboBox<MazeType> typeSelector;

    private JButton okButton;
    private JButton exitButton;
    /**
     * This is how much the generator will wait in each pass, so the user can look at the generation process.
     */
    private int delay=-1;
    /**
     * The field in which we enter the delay of the maze generation
     */
    private JTextField delayField;

    /**
     *
     * @return {@link #delay}, the amount of time the generator will wait before each step
     */
    public int getDelay(){
        return delay;
    }

    /**
     *
     * @return {@link #mazeType}, which stores the type of maze the user wants to create
     */
    public MazeType getMazeType() {
        return mazeType;
    }

    public MazeAdderWindow(JFrame parent) {
        super(parent, ModalityType.APPLICATION_MODAL);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screenSize.width;
        int h = screenSize.height;
        this.setSize(w / 5, h / 5+10);
        this.setResizable(false);
        widthField = new JTextField(3);
        heightField = new JTextField(3);

        this.setTitle("Add Maze");


        JPanel flowPanel = new JPanel();
        JLabel wLabel = new JLabel("Maze Width:");
        JLabel hLabel = new JLabel("Maze Height:");
        flowPanel.add(wLabel);
        flowPanel.add(widthField);
        flowPanel.add(hLabel);
        flowPanel.add(heightField);
        errorLabel = new JLabel("",JLabel.CENTER);


        typeSelector = new JComboBox<>(MazeType.values());
        typeSelector.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                value = ((MazeType) value).toString();
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        delayField=new JTextField("0",4);
        JPanel boxPanel=new JPanel();
        JLabel tLabel = new JLabel("Maze Generation Algorithm:",SwingConstants.CENTER);
        tLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        flowPanel.add(tLabel);
        boxPanel.setLayout(new BoxLayout(boxPanel,BoxLayout.Y_AXIS));
        boxPanel.add(flowPanel);
        boxPanel.add(tLabel);
        JPanel temp1=new JPanel();
        temp1.add(typeSelector);
        boxPanel.add(temp1);
        JPanel temp2=new JPanel();
        temp2.add(new JLabel("Time Delay: "));
        temp2.add(delayField);
        boxPanel.add(temp2);


        this.add(boxPanel, BorderLayout.CENTER);

        JLabel addMazeLabel = new JLabel("Add New Maze:");
        JPanel textPanel = new JPanel();
        textPanel.add(addMazeLabel, BorderLayout.CENTER);
        this.add(textPanel, BorderLayout.NORTH);

        exitButton = new JButton("Exit");
        okButton = new JButton("Add Maze");
        JPanel bottomFlow = new JPanel();
        JPanel bottomBox = new JPanel();
        bottomBox.setLayout(new BoxLayout(bottomBox, BoxLayout.Y_AXIS));
        errorLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        bottomBox.add(errorLabel,BoxLayout.X_AXIS);

        bottomBox.add(bottomFlow);
        bottomFlow.add(exitButton);
        bottomFlow.add(okButton);
        this.add(bottomBox, BorderLayout.SOUTH);

        exitButton.addActionListener(this);
        okButton.addActionListener(this);

        //We have to set a new custom document filter for our field, so only the correct values can be entered
        PlainDocument heightDoc=(PlainDocument) heightField.getDocument();
        heightDoc.setDocumentFilter(new DocumentFilter() {
            public void replace(FilterBypass fb, int offs, int length,
                                String str, AttributeSet a) throws BadLocationException {

                String text = fb.getDocument().getText(0,
                        fb.getDocument().getLength());
                text += str;
                if ((fb.getDocument().getLength() + str.length() - length) <= 2
                        && text.matches("^[1-9][0-9]??$")) {
                    super.replace(fb, offs, length, str, a);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            public void insertString(FilterBypass fb, int offs, String str,
                                     AttributeSet a) throws BadLocationException {

                String text = fb.getDocument().getText(0,
                        fb.getDocument().getLength());
                text += str;
                if ((fb.getDocument().getLength() + str.length()) <= 2
                        && text.matches("^[1-9][0-9]??$")) {
                    super.insertString(fb, offs, str, a);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });

        PlainDocument widthDoc=(PlainDocument) widthField.getDocument();
        widthDoc.setDocumentFilter(new DocumentFilter(){
            public void replace(DocumentFilter.FilterBypass fb, int offs, int length,
            String str, AttributeSet a) throws BadLocationException {

                String text = fb.getDocument().getText(0,
                        fb.getDocument().getLength());
                text += str;
                if ((fb.getDocument().getLength() + str.length() - length) <= 2
                        && text.matches("^[1-9][0-9]??$")) {
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
                if ((fb.getDocument().getLength() + str.length()) <= 2
                        && text.matches("^[1-9][0-9]??$")) {
                    super.insertString(fb, offs, str, a);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });


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
        this.validate();


    }

    /**
     *
     * @return {@link #mazeHeight}, the vertical size of the maze
     */
    public int getMazeHeight() {
        return mazeHeight;
    }

    /**
     *
     * @return {@link #mazeWidth}, the horizontal size of the maze
     */
    public int getMazeWidth() {
        return mazeWidth;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(okButton.getActionCommand())){

            if(!heightField.getText().isEmpty())
                mazeHeight=Integer.parseInt(heightField.getText());
            if(!widthField.getText().isEmpty())
                mazeWidth=Integer.parseInt(widthField.getText());
            if(!delayField.getText().isEmpty())
                delay=Integer.parseInt(delayField.getText());
            mazeType=(MazeType)typeSelector.getSelectedItem();
            if(mazeHeight>0 && mazeWidth>0 && delay>-1)
                this.setVisible(false);
            else{
                errorLabel.setForeground(Color.RED);
                errorLabel.setText("Invalid input!");
            }
        }
        else if(e.getActionCommand().equals(exitButton.getActionCommand())){
            this.dispose();
        }

    }
}
