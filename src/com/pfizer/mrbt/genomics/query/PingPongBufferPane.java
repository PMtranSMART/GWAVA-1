package com.pfizer.mrbt.genomics.query;

import com.pfizer.mrbt.genomics.Singleton;
import com.pfizer.mrbt.genomics.webservices.DbSnpSourceOption;
import com.pfizer.mrbt.genomics.webservices.GeneSourceOption;
import com.pfizer.mrbt.genomics.webservices.RetrievalException;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * <p>Title: PingPongBufferDialog.java</p>
 *
 * <p>Description: Generates the dialog box for the left and right or select and
 * deselects features (both row and column) and returns the output.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Pfizer, Inc.</p>
 *
 * @author Peter V. Henstock
 * @version 1.0
 */
public class PingPongBufferPane extends JComponent {

    private DefaultListModel leftModel = new DefaultListModel();
    private DefaultListModel rightModel = new DefaultListModel();
    private JList leftList;
    private JList rightList;
    private Collection rightListInput;
    private Collection leftListInput;
    private String left2rightStr;
    private String right2leftStr;
    private String left2rightAllStr;
    private String right2leftAllStr;
    private String dialogName;
    private AbstractButton filterButton;
    private AbstractButton showAllButton;
    private JTextField filterField;
    private ArrayList<String> allData = new ArrayList<String>();
    private JComponent geneSnpOptionPane;
    private java.util.List<DbSnpSourceOption> dbSnpOptions = new ArrayList<DbSnpSourceOption>();
    private java.util.List<GeneSourceOption> geneSourceOptions = new ArrayList<GeneSourceOption>();
    public final static String PROTOTYPE_VALUE = "23MeNeuro - Attention deficit-hyperactivity disorder (ADHD)";
    
    public final static int LIST_WIDTHS = 13;
    public final static int OK_RETURNED = JOptionPane.OK_OPTION;
    public final static int CANCEL_RETURNED = JOptionPane.CANCEL_OPTION;
    private final static int STUDY_WIDTH = 500;
    private final static int STUDY_HEIGHT = 200;

    private JComboBox snpAnnotationComboBox;
    private JComboBox geneAnnotationComboBox;

    
    /**
     * Create ping pong buffer with the initial left and right columns set
     * with collections. The dialog box has the title set with dialogName.
     * The strings that right the buttons left to right are
     * user-specified.
     *
     * @param _rightListInput Collection
     * @param _leftListInput Collection
     * @param _dialogName String
     * @param _leftToRightString String
     * @param _rightToLeftString String
     * @param _leftToRightAllString String
     * @param _rightToLeftAllString String
     */
    public PingPongBufferPane(Collection _rightListInput, Collection _leftListInput,
                              String _dialogName,
                              String _leftToRightString, String _rightToLeftString,
                              String _leftToRightAllString, String _rightToLeftAllString) {
        super();
        setLayout(new GridBagLayout());
        rightListInput = _rightListInput;
        leftListInput  = _leftListInput;
        dialogName     = _dialogName;
        left2rightStr  = _leftToRightString;
        right2leftStr  = _rightToLeftString;
        left2rightAllStr  = _leftToRightAllString;
        right2leftAllStr  = _rightToLeftAllString;

        initializeAllData(_leftListInput, _rightListInput);
        computeLeftRightLists();
        leftList = new JList(leftModel);
        leftList.setPrototypeCellValue(PROTOTYPE_VALUE);
        leftList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) { 
                if(me.getClickCount()==2 && leftList.getSelectedIndices().length==1) {
                    getRightArrow().doClick();
                }
            }
        });
        
        rightList = new JList(rightModel);
        rightList.setPrototypeCellValue(PROTOTYPE_VALUE);
        rightList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) { 
                if(me.getClickCount()==2 && rightList.getSelectedIndices().length==1) {
                    getLeftArrow().doClick();
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(getPane("Rows", leftModel, leftList, rightModel, rightList), gbc);
    }

    /**
     * Reused portion for the row and column panels that containt he
     * structure.
     *
     * @param title String is the displayed title of the pane
     * @param gbc GridBagConstraints are the constraints for the layout
     * @param leftModel DefaultListModel contains the model behind the
     * left
     * @param leftList JList contains the left entries
     * @param rightModel DefaultListModel contains model behidn the right
     * @param rightList JList contains the right entries
     * @return JComponent
     */
    private JComponent getPane(String title,
                               DefaultListModel leftModel, JList leftList,
                               DefaultListModel rightModel, JList rightList) {
        //JLabel titleLabel  = new JLabel(right2leftStr + "/" + left2rightStr + title);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        //gbc.gridx = 2;
        //gbc.gridy = 1;
        //gbc.weighty = 0.2;
        //panel.add(titleLabel, gbc);

        gbc.gridx = 10;
        gbc.gridy = 2;
        gbc.gridwidth = 9;
        gbc.fill = GridBagConstraints.BOTH;
        JLabel hiddenLabel = new JLabel("Available Models");
        Font font = hiddenLabel.getFont();
        hiddenLabel.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize() + 2));
        hiddenLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        panel.add(hiddenLabel, gbc);

        gbc.gridx = 30;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel rightLabel = new JLabel("Included Models for Query");
        rightLabel.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize() + 2));
        rightLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(rightLabel, gbc);

        gbc.gridx = 10;
        gbc.gridy = 4;
        gbc.gridheight = 5;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.gridwidth = 9;
        JScrollPane leftPane = new JScrollPane(leftList);
        //leftPane.setPreferredSize(new Dimension(STUDY_WIDTH,STUDY_HEIGHT));
        panel.add(leftPane, gbc);

        gbc.gridx = 30;
        gbc.gridy = 4;
        gbc.gridheight = 5;
        gbc.gridwidth = 1;
        JScrollPane rightPane = new JScrollPane(rightList);
        //rightPane.setPreferredSize(new Dimension(STUDY_WIDTH,STUDY_HEIGHT));
        panel.add(rightPane, gbc);

        gbc.gridx = 10;
        gbc.gridy = 9;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5,5,1,0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(getFilterField(), gbc);
        
        gbc.gridx = 15;
        gbc.gridy = 9;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(5,1,1,0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(getFilterButton(), gbc);
        
        
        gbc.gridx = 30;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(getGeneSnpOptionPane(), gbc);
        
        gbc.gridx = 17;
        gbc.gridy = 9;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(5,1,1,0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(getShowAllButton(), gbc);
        
        
        gbc.gridx = 20;
        gbc.gridy = 4;
        gbc.gridheight = 1;
        gbc.insets = new Insets(4,1,0,1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        panel.add(getRightArrow(), gbc);

        gbc.gridx = 20;
        gbc.gridy = 5;
        gbc.gridheight = 1;
        panel.add(getLeftArrow(), gbc);

        gbc.gridx = 20;
        gbc.gridy = 6;
        gbc.gridheight = 1;
        //gbc.anchor = GridBagConstraints.WEST;
        panel.add(getRightAllArrow(), gbc);

        gbc.gridx = 20;
        gbc.gridy = 7;
        gbc.gridheight = 1;
        //gbc.anchor = GridBagConstraints.EAST;
        panel.add(getLeftAllArrow(), gbc);
        
        gbc.gridx = 40;
        gbc.gridy = 6;
        gbc.gridheight = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        //panel.add(getUpArrow(), gbc);

        gbc.gridx = 40;
        gbc.gridy = 8;
        gbc.gridheight = 1;
        //panel.add(getDownArrow(), gbc);

        //adjustPanelWidths(rightPane, leftPane, rightModel, leftModel);
        //panel.setBorder(BorderFactory.createLineBorder(Color.RED));
        return panel;
    }

    /**
     * Sets the rightPane and leftPane widths to be identical based on the
     * amount of data present and the maximum width
     *
     * @param rightPane JScrollPane
     * @param leftPane JScrollPane
     * @param rightModel DefaultListModel
     * @param leftModel DefaultListModel
     */
    private void adjustPanelWidths(JScrollPane rightPane, JScrollPane leftPane,
                                   DefaultListModel rightModel, DefaultListModel leftModel) {
        int rightPaneWidth = rightPane.getPreferredSize().width;
        int leftPaneWidth = leftPane.getPreferredSize().width;
        leftPane.setMinimumSize(new Dimension(0, 0));
        rightPane.setMinimumSize(new Dimension(0, 0));
        rightPaneWidth = LIST_WIDTHS;
        leftPaneWidth = LIST_WIDTHS;
        if (rightModel.size() == 0) {
            rightPane.setPreferredSize(leftPane.getPreferredSize());
        } else if (leftModel.size() == 0) {
            leftPane.setPreferredSize(rightPane.getPreferredSize());
        } else if (rightPaneWidth > leftPaneWidth) {
            leftPane.setPreferredSize(rightPane.getPreferredSize());
        } else {
            rightPane.setPreferredSize(leftPane.getPreferredSize());
        }
    }

    /**
     * Takes the selected data in the right field and shifts it to the
     * left field.
     *
     * @return AbstractButton
     */
    private AbstractButton getLeftArrow() {
        java.net.URL leftArrowURL = this.getClass().getResource("/images/arrow.plain.left.jpg");
        JButton leftArrow = new JButton(right2leftStr, new ImageIcon(leftArrowURL));
        leftArrow.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                int[] selectedIndices = rightList.getSelectedIndices();
                int numSelected = selectedIndices.length;
                for (int i = 0; i < numSelected; i++) {
                    leftModel.addElement(rightModel.getElementAt(selectedIndices[i]));
                }
                for (int i = numSelected - 1; i >= 0; i--) {
                    rightModel.removeElementAt(selectedIndices[i]);
                }
            }
        });
        return leftArrow;
    }

    /**
     * Takes the selected data in the right field and shifts it to the
     * left field.
     *
     * @return AbstractButton
     */
    private AbstractButton getLeftAllArrow() {
        java.net.URL leftArrowURL = this.getClass().getResource("/images/arrow.plain.left.jpg");
        JButton leftArrow = new JButton(right2leftStr + " All", new ImageIcon(leftArrowURL));
        leftArrow.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                int numRight = rightModel.size();
                for (int i = 0; i < numRight; i++) {
                    leftModel.addElement(rightModel.getElementAt(i));
                }
                rightModel.clear();
            }
        });
        return leftArrow;
    }

    /**
     * Takes the selected data in the left field and shifts it to the
     * right field.
     *
     * @return AbstractButton
     */
    private AbstractButton getRightArrow() {
        java.net.URL rightArrowURL = this.getClass().getResource("/images/arrow.plain.right.jpg");
        JButton rightArrow = new JButton(left2rightStr, new ImageIcon(rightArrowURL));
        rightArrow.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                int[] selectedIndices = leftList.getSelectedIndices();
                int numSelected = selectedIndices.length;
                for (int i = 0; i < numSelected; i++) {
                    rightModel.addElement(leftModel.getElementAt(selectedIndices[i]));
                }
                for (int i = numSelected - 1; i >= 0; i--) {
                    leftModel.removeElementAt(selectedIndices[i]);
                }
            }
        });
        return rightArrow;
    }

    /**
     * Takes the selected data in the left field and shifts it to the
     * right field.
     *
     * @return AbstractButton
     */
    private AbstractButton getRightAllArrow() {
        java.net.URL rightArrowURL = this.getClass().getResource("/images/arrow.plain.right.jpg");
        JButton rightArrow = new JButton(left2rightStr + " All", new ImageIcon(rightArrowURL));
        rightArrow.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                int numHidden = leftModel.size();
                for (int i = 0; i < numHidden; i++) {
                    rightModel.addElement(leftModel.getElementAt(i));
                }
                leftModel.clear();
            }
        });
        return rightArrow;
    }

    private AbstractButton getUpArrow() {
        java.net.URL upArrowURL = this.getClass().getResource("/images/arrow.plain.up.gif");
        JButton upArrow = new JButton(new ImageIcon(upArrowURL));
        upArrow.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                shiftSelectedUp();
            }
        });
        return upArrow;
    }

    private AbstractButton getDownArrow() {
        java.net.URL downArrowURL = this.getClass().getResource("/images/arrow.plain.down.gif");
        JButton downArrow = new JButton(new ImageIcon(downArrowURL));
        downArrow.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                shiftSelectedDown();
            }
        });
        return downArrow;
    }
    
    private AbstractButton getFilterButton() {
        if(filterButton == null) {
            filterButton = new JButton("Filter");
            filterButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    filter(filterField.getText().trim());
                }
            });
        }
        return filterButton;
    }
    
    private AbstractButton getShowAllButton() {
        if(showAllButton == null) {
            showAllButton = new JButton("Show All");
            showAllButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    filter("");
                }
            });
        }
        return showAllButton;
    }
    
    private JTextField getFilterField() {
        if(filterField == null) {
            filterField = new JTextField(10);
            filterField.setText("");
            filterField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent ke) {
                    if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
                        filterButton.doClick();
                    }
                }
            });
        }
        return filterField;
    }

    private void shiftSelectedUp() {
        int startIndex = 0;
        int[] selectedEntries = rightList.getSelectedIndices();
        int numSelected = selectedEntries.length;
        if (selectedEntries[0] > 0) {
            while (startIndex < numSelected) {
                int endIndex = startIndex + 1;
                while (endIndex < numSelected
                       && selectedEntries[endIndex] == selectedEntries[endIndex - 1] + 1) {
                    endIndex++;
                }
                swapUp(rightModel, selectedEntries[startIndex],
                       selectedEntries[endIndex - 1]);
                startIndex = endIndex;
            }
            for (int i = 0; i < numSelected; i++) {
                selectedEntries[i] = selectedEntries[i] - 1;
            }
            rightList.setSelectedIndices(selectedEntries);
        } else {
            //System.out.println("No action already at top");
        }
    }

    private void shiftSelectedDown() {
        int startIndex = 0;
        int[] selectedEntries = rightList.getSelectedIndices();
        int numSelected = selectedEntries.length;
        if (selectedEntries[numSelected - 1] < rightModel.size() - 1) {
            while (startIndex < numSelected) {
                int endIndex = startIndex + 1;
                while (endIndex < numSelected
                       && selectedEntries[endIndex] == selectedEntries[endIndex - 1] + 1) {
                    endIndex++;
                }
                swapDown(rightModel, selectedEntries[startIndex],
                         selectedEntries[endIndex - 1]);
                startIndex = endIndex;
            }
            for (int i = 0; i < numSelected; i++) {
                selectedEntries[i] = selectedEntries[i] + 1;
            }
            rightList.setSelectedIndices(selectedEntries);
        } else {
            //System.out.println("No action already at bottom");
        }
    }

    /**
     * Takes the listModel and swaps the block of data line blockStart to
     * blockEnd with newLoc that is adjacent to the block.
     *
     * @param listModel DefaultListModel
     * @param blockStart int
     * @param blockEnd int
     * @param newLoc int
     */
    private void swapDown(DefaultListModel listModel, int blockStart,
                          int blockEnd) {
        Object newLocEntry = listModel.get(blockEnd + 1);
        listModel.remove(blockEnd + 1);
        listModel.add(blockStart, newLocEntry);
    }

    private void swapUp(DefaultListModel listModel, int blockStart, int blockEnd) {
        Object newLocEntry = listModel.get(blockStart - 1);
        listModel.remove(blockStart - 1);
        listModel.add(blockEnd, newLocEntry);
    }

    /**
     * Fills rightRows and leftRows with the rowNames that are being
     * displayed as determined by the DisplayState
     */
    public void computeLeftRightLists() {
        rightModel = new DefaultListModel();
        for (Iterator iter = rightListInput.iterator(); iter.hasNext();) {
            rightModel.addElement(iter.next());
        }

        leftModel = new DefaultListModel();
        for (Iterator iter = leftListInput.iterator(); iter.hasNext();) {
            leftModel.addElement(iter.next());
        }
    }
    
    /**
     * Takes all data in the leftListData and rightListData and puts it into a
     * sorted allData.
     */
    protected void initializeAllData(Collection leftListData, Collection rightListData) {
        allData.clear();
        for(Iterator iter = leftListInput.iterator(); iter.hasNext(); ) {
            allData.add((String) iter.next());
        }
        for(Iterator iter = rightListInput.iterator(); iter.hasNext(); ) {
            allData.add((String) iter.next());
        }
        Collections.sort(allData);
    }
    
    /**
     * Puts in leftModel all the entries that are not in rightModel and matched
     * by filter.  If filter is "" then it adds all the entries
     * @param filterStr 
     */
    protected void filter(String filterStr) {
        leftModel.clear();
        //System.out.println("Filtering [" + filterStr + "]");
        for(String allDataEntry : allData) {
            if(! rightModel.contains(allDataEntry)) {
                String tempStr = ".*(?i)" + filterStr + ".*";
                boolean result = allDataEntry.matches(tempStr);
                //System.out.println("[" + allDataEntry + "] [" + tempStr + "]\t" + result);
                if(filterStr.isEmpty() ||
                   allDataEntry.matches(".*(?i)" + filterStr + ".*")) {
                    leftModel.addElement(allDataEntry);
                }
            } else {
                //System.out.println("skipping " + allDataEntry);
            }
        }
        leftList.setModel(leftModel);
    }
    
    public ArrayList getRightList() {
        ArrayList myList = new ArrayList();
        int len = rightModel.size();
        for (int i = 0; i < len; i++) {
            myList.add(rightModel.get(i));
        }
        return myList;
    }

    public ArrayList getLeftList() {
        ArrayList myList = new ArrayList();
        int len = leftModel.size();
        for (int i = 0; i < len; i++) {
            myList.add(leftModel.get(i));
        }
        return myList;
    }
    
    protected JComponent getGeneSnpOptionPane() {
        if(geneSnpOptionPane == null) {
            geneSnpOptionPane = new JPanel();
            geneSnpOptionPane.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.NONE;

            gbc.gridx = 10;
            gbc.gridy = 10;
            gbc.weightx = 0.0;
            gbc.weighty = 0.0;
            gbc.anchor = GridBagConstraints.LINE_END;
            //JLabel snpLabel = new JLabel("<html><p align=right>SNP Annotation<br/>Source:</p></html>");
            JLabel snpLabel = new JLabel("SNP Src:");
            snpLabel.setHorizontalAlignment(JLabel.RIGHT);
            Font currFont = snpLabel.getFont();
            Font labelFont = new Font(currFont.getName(), Font.PLAIN, 13);
            snpLabel.setFont(labelFont);
            geneSnpOptionPane.add(snpLabel, gbc);

            gbc.gridx = 20;
            gbc.gridy = 10;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            geneSnpOptionPane.add(getSnpAnnotationComboBox(), gbc);

            gbc.gridx = 30;
            gbc.gridy = 10;
            gbc.insets = new Insets(0,10,0,0);
            gbc.anchor = GridBagConstraints.EAST;
            //JLabel geneLabel = new JLabel("<html><p align=right>Gene Annotation<br/>Source:</p></html>");
            JLabel geneLabel = new JLabel("Gene Src:");
            geneLabel.setHorizontalAlignment(JLabel.RIGHT);
            geneLabel.setFont(labelFont);
            geneSnpOptionPane.add(geneLabel, gbc);

            gbc.gridx = 40;
            gbc.gridy = 10;
            gbc.insets = new Insets(0,0,0,0);
            gbc.anchor = GridBagConstraints.NORTHWEST;
            geneSnpOptionPane.add(getGeneAnnotationComboBox(), gbc);
        }
        return geneSnpOptionPane;
    }
        
     protected JComboBox getSnpAnnotationComboBox() {
        if (snpAnnotationComboBox == null) {
            snpAnnotationComboBox = new JComboBox();
            try {
                dbSnpOptions = Singleton.getDataModel().getWebServices().getDbSnpSources();
            } catch(RetrievalException rex) {
                showLoadInitializationFailure(rex);
                System.exit(1);
            }
            for(DbSnpSourceOption dbSnpOption : dbSnpOptions) {
                snpAnnotationComboBox.addItem(dbSnpOption.toString());
            }
        }
        return snpAnnotationComboBox;
    }

    protected JComboBox getGeneAnnotationComboBox() {
        if (geneAnnotationComboBox == null) {
            geneAnnotationComboBox = new JComboBox();
            try {
                geneSourceOptions = Singleton.getDataModel().getWebServices().getGeneSources();
            } catch(RetrievalException rex) {
                showLoadInitializationFailure(rex);
                System.exit(1);
            }
            
            for(GeneSourceOption geneSourceOption : geneSourceOptions) {
                geneAnnotationComboBox.addItem(geneSourceOption.toString());
            }
        }
        return geneAnnotationComboBox;
    }

    public DbSnpSourceOption getSelectedDbSnpOption() {
        return dbSnpOptions.get(snpAnnotationComboBox.getSelectedIndex());
    }
    
    public GeneSourceOption getSelectedGeneSourceOption() {
        return geneSourceOptions.get(geneAnnotationComboBox.getSelectedIndex());
    }
    
    /**
     * Shows an error message and prints out the reason to stderr
     * @param rex 
     */
    protected void showLoadInitializationFailure(RetrievalException rex) {
        JOptionPane.showMessageDialog(
                this.getTopLevelAncestor(),
            //(JFrame) SwingUtilities.getWindowAncestor(QueryPanel.this),
            rex.getMessage(),
            "Initialization failed: " + rex.getRetrievalMethod().toString(),
            JOptionPane.ERROR_MESSAGE);
        System.err.println("Initialization failed");
        rex.printStackTrace();
        System.err.println(rex.toString());
    }


}
