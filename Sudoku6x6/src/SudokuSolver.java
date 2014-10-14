import java.awt.*;    
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;    
import javax.swing.table.*; 

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import CLIPSJNI.*;

/* TBD Allow tabbing between different grids. */
/* TBD Allow arrow keys to move between different grids. */
/* TBD Web links for techniques. */

/*

Notes:

This example creates just a single environment. If you create multiple environments,
call the destroy method when you no longer need the environment. This will free the
C data structures associated with the environment.

   clips = new Environment();
      .
      . 
      .
   clips.destroy();

Calling the clear, reset, load, loadFacts, run, eval, build, assertString,
and makeInstance methods can trigger CLIPS garbage collection. If you need
to retain access to a PrimitiveValue returned by a prior eval, assertString,
or makeInstance call, retain it and then release it after the call is made.

   PrimitiveValue pv1 = clips.eval("(myFunction foo)");
   pv1.retain();
   PrimitiveValue pv2 = clips.eval("(myFunction bar)");
      .
      .
      .
   pv1.release();

*/


public class SudokuSolver implements ActionListener, FocusListener
  {  
   JFrame jfrm;
   JPanel mainGrid;
   int mode = 0; //0:input txt, 1:input clp
   
   JButton clearButton;
   JButton resetButton;
   JButton solveButton;
   JButton techniquesButton;
   JButton browseButton;
   
   PuzzleParser6 puzzleParser;
   
   Object resetValues[][][] = new Object[6][2][3];
   
   boolean solved = false;
   
   ResourceBundle sudokuResources;
   
   public Environment clips;
   boolean isExecuting = false;
   Thread executionThread;
   JTable theSubGrid;
   
   /**************/
   /* SudokuDemo */
   /**************/
   SudokuSolver()
     {    
//      JTable theSubGrid;
      int r, c;
      
      /*====================================*/
      /* Load the internationalized string  */
      /* resources used by the application. */
      /*====================================*/
      
      try
        {
         sudokuResources = ResourceBundle.getBundle("resources.SudokuResources",Locale.getDefault());
        }
      catch (MissingResourceException mre)
        {
         mre.printStackTrace();
         return;
        }
       
      /*===================================*/
      /* Create the main JFrame container. */
      /*===================================*/
      
      jfrm = new JFrame(sudokuResources.getString("SudokuDemo"));  
      jfrm.getContentPane().setLayout(new BorderLayout());

      /*=============================================================*/
      /* Terminate the program when the user closes the application. */
      /*=============================================================*/
          
      jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
  
      /*=======================================================*/
      /* Create the JPanel which will contain the sudoku grid. */
      /*=======================================================*/
      
      mainGrid = new JPanel(); 
      
      GridLayout theLayout = new GridLayout(3,2);
      theLayout.setHgap(-1);
      theLayout.setVgap(-1); 

      mainGrid.setLayout(theLayout);   
      mainGrid.setOpaque(true);
      
      /*=================================================*/
      /* Create a renderer based on the default renderer */
      /* that will center the text within the cell.      */
      /*=================================================*/
      
      DefaultTableCellRenderer renderer = 
         new DefaultTableCellRenderer()
           {
            public Component getTableCellRendererComponent(
              JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column)
              {
               Component comp = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
               if (comp instanceof JLabel)
                 { 
                  ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER); 
                  if (value instanceof String)
                    { 
                     if ("?".equals(value))
                       { ((JLabel) comp).setForeground(Color.red); }
                     else if (((String) value).length() > 1)
                       { ((JLabel) comp).setForeground(Color.green.darker()); }
                     else
                       { ((JLabel) comp).setForeground(Color.black); }
                    }
                 }
               return comp;
              }
           };

      /*========================================*/
      /* Create each of the nine 3x3 grids that */
      /* will go inside the main sudoku grid.   */
      /*========================================*/
            
      for (r = 0; r < 3; r++)
	  {
	       for (c = 0; c < 2; c++)
           {
            theSubGrid = 
               new JTable(2,3)
                 {
                  public boolean isCellEditable(int rowIndex,int vColIndex) 
                    { return false; }
                 };
              
            theSubGrid.setRowSelectionAllowed(false);
            theSubGrid.setShowGrid(true);
            theSubGrid.setRowHeight(25);
            theSubGrid.setGridColor(Color.black);
            theSubGrid.setBorder(BorderFactory.createLineBorder(Color.black,2));     
            theSubGrid.setDefaultRenderer(Object.class,renderer);
            
            theSubGrid.addFocusListener(this);
                                                                                            
            TableColumn column = null;
            for (int i = 0; i < 3; i++) 
              {
               column = theSubGrid.getColumnModel().getColumn(i);
               column.setMaxWidth(25);
              }
            // MENGISI SUBGRID LANGSUNG
           //CLPtoTXT.start();
           String input = "input6.txt";
           if(mode == 0) TXTtoCLP.start(input);
           else if(mode == 1) CLPtoTXT.start();
     	   puzzleParser = new PuzzleParser6();
    	   try {
				puzzleParser.FillPuzzleFromTxt();
				puzzleParser.PrintPuzzle();
    	   } catch (IOException e1) {
    		   	e1.printStackTrace();	
    		}
            for(int i=0;i<2;i++) {
            	for(int j=0;j<3;j++){
//            		if (puzzleParser.Puzzle[r*2+i+1][c*3+j+1]=='*')
//            		{
                		theSubGrid.setValueAt(null, i, j);            			
//            		}
//            		else
//            		{
//                		theSubGrid.setValueAt(puzzleParser.Puzzle[r*2+i+1][c*3+j+1], i, j);            			
//            		}
            	}
            }
            mainGrid.add(theSubGrid);
           }
        }
       
      /*========================================*/
      /* Set up the panel containing the Clear, */
      /* Reset, Solve, and Techniques buttons.  */
      /*========================================*/

      JPanel buttonGrid = new JPanel();
      
      theLayout = new GridLayout(5,1);

      buttonGrid.setLayout(theLayout);   
      buttonGrid.setOpaque(true);
            
      clearButton = new JButton(sudokuResources.getString("Clear")); 
      clearButton.setActionCommand("Clear");
      buttonGrid.add(clearButton);
      clearButton.addActionListener(this);
      clearButton.setEnabled(false);
      clearButton.setToolTipText(sudokuResources.getString("ClearTip")); 
      
      resetButton = new JButton(sudokuResources.getString("Reset")); 
      resetButton.setActionCommand("Reset");
      resetButton.setEnabled(false);
      buttonGrid.add(resetButton);
      resetButton.addActionListener(this);
      resetButton.setToolTipText(sudokuResources.getString("ResetTip")); 
      
      solveButton = new JButton(sudokuResources.getString("Solve")); 
      solveButton.setActionCommand("Solve");
      buttonGrid.add(solveButton);
      solveButton.setEnabled(false);
      solveButton.addActionListener(this);
      solveButton.setToolTipText(sudokuResources.getString("SolveTip")); 
      
      techniquesButton = new JButton(sudokuResources.getString("Techniques"));
      techniquesButton.setActionCommand("Techniques");
      techniquesButton.setEnabled(false); 
      buttonGrid.add(techniquesButton);
      techniquesButton.addActionListener(this);
      techniquesButton.setToolTipText(sudokuResources.getString("TechniquesTip")); 
      
      browseButton = new JButton(sudokuResources.getString("Browse")); 
      browseButton.setActionCommand("Browse");
      browseButton.setEnabled(true);
      buttonGrid.add(browseButton);
      browseButton.addActionListener(this);
      browseButton.setToolTipText(sudokuResources.getString("BrowseTip"));

      /*=============================================*/
      /* Add the grid and button panels to the pane. */
      /*=============================================*/

      JPanel mainPanel = new JPanel(); 
      mainPanel.setLayout(new FlowLayout());
      mainPanel.add(mainGrid);
      mainPanel.add(buttonGrid);
      jfrm.getContentPane().add(mainPanel,BorderLayout.NORTH);

      JLabel instructions = new JLabel("<html><p style=\"font-size:95%\">" + sudokuResources.getString("Instructions") + "</p><br>");
      JPanel labelPanel = new JPanel(); 
      labelPanel.setLayout(new FlowLayout());
      labelPanel.add(instructions);
      jfrm.getContentPane().add(labelPanel,BorderLayout.SOUTH);

      /*==========================*/
      /* Load the sudoku program. */
      /*==========================*/
      
      clips = new Environment();
      clips = new Environment();
      
      clips.eval("(load \"sudoku6.clp\")");
      clips.eval("(load \"solve6.clp\")");
      //clips.loadFacts("C:\\Users\\toshibapc\\workspace\\SudokuSolver\\src\\puzzles\\grid3x3-p1.clp");
      
      
      /**********************/
      /* Display the frame. */
      /**********************/
      
      jfrm.pack(); 
      jfrm.setVisible(true);    
     }    
   
   /********/
   /* main */
   /********/
   public static void main(
     String args[])
     {    
      /*===================================================*/
      /* Create the frame on the event dispatching thread. */
      /*===================================================*/
      
	   SwingUtilities.invokeLater(
        new Runnable() 
          {    
           public void run() { new SudokuSolver(); }  
          });    
     }    

   /*########################*/
   /* ActionListener Methods */
   /*########################*/

   /*******************/
   /* actionPerformed */
   /*******************/  
   public void actionPerformed(
     ActionEvent ae) 
     {
      try
        { onActionPerformed(ae); }
      catch (Exception e)
        { e.printStackTrace(); }
     }
     
   /*************/
   /* runSudoku */
   /*************/  
   public void runSudoku()
     {
      Runnable runThread = 
         new Runnable()
           {
            public void run()
              {
               clips.run();
               //System.out.println("tostr"+clips.toString());
               
               SwingUtilities.invokeLater(
                  new Runnable()
                    {
                     public void run()
                       {
                        try 
                          { updateGrid(); }
                        catch (Exception e)
                          { e.printStackTrace(); }
                       }
                    });
              }
           };
      
      isExecuting = true;
      
      executionThread = new Thread(runThread);
      
      jfrm.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         
      executionThread.start();
     }

   /*********************/
   /* onActionPerformed */
   /*********************/  
   public void onActionPerformed(
     ActionEvent ae) throws Exception 
     {      
      if (isExecuting) return;

      /*==========================*/
      /* Handle the Clear button. */
      /*==========================*/

      if (ae.getActionCommand().equals("Clear"))  
        { 
         solved = false;
         
         solveButton.setEnabled(true);
         techniquesButton.setEnabled(false);
         
         for (int i = 0; i < 6; i++)
           {
            JTable theTable = (JTable) mainGrid.getComponent(i);

            for (int r = 0; r < 2; r++)
              {
               for (int c = 0; c < 3; c++)
                 { theTable.setValueAt("",r,c);  }         
              }
           }
        }
        
      /*==========================*/
      /* Handle the Reset button. */
      /*==========================*/
        
      else if (ae.getActionCommand().equals("Reset"))  
        {
         solved = false;
         solveButton.setEnabled(true);
         techniquesButton.setEnabled(false);


         for (int i = 0; i < 6; i++)
           {
            JTable theTable = (JTable) mainGrid.getComponent(i);

            for (int r = 0; r < 2; r++)
              {
               for (int c = 0; c < 3; c++)
                 { 
             	  if (i==1)
             	  {
             		  if (puzzleParser.Puzzle[r+1][3+c+1]=='*')
             		  {
                           theTable.setValueAt("",r,c);                			  
             		  }
             		  else
             		  {
                           theTable.setValueAt(puzzleParser.Puzzle[r+1][3+c+1],r,c);                			  
             		  }
             	  }
             	  else if (i==2)
             	  {
             		  if (puzzleParser.Puzzle[2+r+1][c+1]=='*')
             		  {
                           theTable.setValueAt("",r,c);                			  
             		  }
             		  else
             		  {
                 		  theTable.setValueAt(puzzleParser.Puzzle[2+r+1][c+1],r,c);
             		  }
             	  }
             	  else if (i==3)
             	  {
             		  if (puzzleParser.Puzzle[2+r+1][3+c+1]=='*')
             		  {
                           theTable.setValueAt("",r,c);                			  
             		  }
             		  else
             		  {
                 		  theTable.setValueAt(puzzleParser.Puzzle[2+r+1][3+c+1],r,c);
             		  }
             	  }
             	  else if (i==4)
             	  {
             		  if (puzzleParser.Puzzle[4+r+1][c+1]=='*')
             		  {
                           theTable.setValueAt("",r,c);                			  
             		  }
             		  else
             		  {
                 		  theTable.setValueAt(puzzleParser.Puzzle[4+r+1][c+1],r,c);
             		  }
             	  }
             	  else if (i==5)
             	  {
             		  if (puzzleParser.Puzzle[4+r+1][3+c+1]=='*')
             		  {
                           theTable.setValueAt("",r,c);                			  
             		  }
             		  else
             		  {
                 		  theTable.setValueAt(puzzleParser.Puzzle[4+r+1][3+c+1],r,c);
             		  }
             	  }
             	  else if (i==0)
             	  {
             		  if (puzzleParser.Puzzle[r+1][c+1]=='*')
             		  {
                           theTable.setValueAt("",r,c);                			  
             		  }
             		  else
             		  {
                 		  theTable.setValueAt(puzzleParser.Puzzle[r+1][c+1],r,c);                		  
             		  }
             	  }
                 }
              }
           }
        }

      /*==========================*/
      /* Handle the Solve button. */
      /*==========================*/

      else if (ae.getActionCommand().equals("Solve"))  
        {
         /*==============*/
         /* Reset CLIPS. */
         /*==============*/
         
         clips.eval("(reset)");

         /*======================================*/
         /* Remember the initial starting values */
         /* of the puzzle for the reset command. */
         /*======================================*/
        
         
         clips.eval("(load \"tccoba.clp\")");

         /*===================================*/
         /* Update the status of the buttons. */
         /*===================================*/
         
         clearButton.setEnabled(false);
         resetButton.setEnabled(false);
         solveButton.setEnabled(false);
         techniquesButton.setEnabled(false);
         
         /*===================*/
         /* Solve the puzzle. */
         /*===================*/

         runSudoku();
        }

      /*===============================*/
      /* Handle the Techniques button. */
      /*===============================*/
      
      else if (ae.getActionCommand().equals("Techniques"))  
        {
         String evalStr;
         String messageStr = "<html><p style=\"font-size:95%\">";
         
         evalStr = "(find-all-facts ((?f technique)) TRUE)";
         
         PrimitiveValue pv = clips.eval(evalStr);
         int tNum = pv.size();
         
         for (int i = 1; i <= tNum; i++)
           {
            evalStr = "(find-fact ((?f technique-employed)) " +
                           "(eq ?f:rank " + i + "))";
                           
            pv = clips.eval(evalStr);
            if (pv.size() == 0) continue;
            
            pv = pv.get(0);

            messageStr = messageStr + pv.getFactSlot("rank").intValue() + ". " +
                                      pv.getFactSlot("reason").stringValue() + "<br>";
           }
         pv.retain();
        
         JOptionPane.showMessageDialog(jfrm,messageStr,sudokuResources.getString("SolutionTechniques"),JOptionPane.PLAIN_MESSAGE);
        }
      /*===============================*/
      /* Handle the Browse button. */
      /*===============================*/
      
      else if (ae.getActionCommand().equals("Browse"))
        {
    	  String input = "";
		  JFileChooser chooser = new JFileChooser();
	      // Demonstrate "Open" dialog:
		  int rVal = chooser.showOpenDialog(jfrm);
		  if (rVal == JFileChooser.APPROVE_OPTION) {
	           input = chooser.getCurrentDirectory().toString()+"\\"+chooser.getSelectedFile().getName();
	           System.out.println(input);
           /*========================================*/
           /* Create each of the nine 3x3 grids that */
           /* will go inside the main sudoku grid.   */
           /*========================================*/
                 // MENGISI SUBGRID LANGSUNG
                //CLPtoTXT.start();
                if(mode == 0) TXTtoCLP.start(input);
                else if(mode == 1) CLPtoTXT.start();
          	   puzzleParser = new PuzzleParser6();
         	   try {
     				puzzleParser.FillPuzzleFromTxt();
     				puzzleParser.PrintPuzzle();
         	   } catch (IOException e1) {
         		   	e1.printStackTrace();	
         		}
               for (int i = 0; i < 6; i++)
               {
                JTable theTable = (JTable) mainGrid.getComponent(i);
                for (int r = 0; r < 2; r++)
                  {
                   for (int c = 0; c < 3; c++)
                     { 
                	  if (i==1)
                	  {
                		  if (puzzleParser.Puzzle[r+1][3+c+1]=='*')
                		  {
                              theTable.setValueAt("",r,c);                			  
                		  }
                		  else
                		  {
                              theTable.setValueAt(puzzleParser.Puzzle[r+1][3+c+1],r,c);                			  
                		  }
                	  }
                	  else if (i==2)
                	  {
                		  if (puzzleParser.Puzzle[2+r+1][c+1]=='*')
                		  {
                              theTable.setValueAt("",r,c);                			  
                		  }
                		  else
                		  {
                    		  theTable.setValueAt(puzzleParser.Puzzle[2+r+1][c+1],r,c);
                		  }
                	  }
                	  else if (i==3)
                	  {
                		  if (puzzleParser.Puzzle[2+r+1][3+c+1]=='*')
                		  {
                              theTable.setValueAt("",r,c);                			  
                		  }
                		  else
                		  {
                    		  theTable.setValueAt(puzzleParser.Puzzle[2+r+1][3+c+1],r,c);
                		  }
                	  }
                	  else if (i==4)
                	  {
                		  if (puzzleParser.Puzzle[4+r+1][c+1]=='*')
                		  {
                              theTable.setValueAt("",r,c);                			  
                		  }
                		  else
                		  {
                    		  theTable.setValueAt(puzzleParser.Puzzle[4+r+1][c+1],r,c);
                		  }
                	  }
                	  else if (i==5)
                	  {
                		  if (puzzleParser.Puzzle[4+r+1][3+c+1]=='*')
                		  {
                              theTable.setValueAt("",r,c);                			  
                		  }
                		  else
                		  {
                    		  theTable.setValueAt(puzzleParser.Puzzle[4+r+1][3+c+1],r,c);
                		  }
                	  }
                	  else if (i==0)
                	  {
                		  if (puzzleParser.Puzzle[r+1][c+1]=='*')
                		  {
                              theTable.setValueAt("",r,c);                			  
                		  }
                		  else
                		  {
                    		  theTable.setValueAt(puzzleParser.Puzzle[r+1][c+1],r,c);                		  
                		  }
                	  }
                     } 
                   clearButton.setEnabled(true);
                   resetButton.setEnabled(true);
                   solveButton.setEnabled(true);
                   techniquesButton.setEnabled(false);
                  }
               }
	                 mainGrid.add(theSubGrid);
		  } 		      
		  if (rVal == JFileChooser.CANCEL_OPTION) {
			  
		  }
        }
     } 
     
   /**************/
   /* updateGrid */
   /**************/  
   private void updateGrid() throws Exception
     { 
      /*===================================*/
      /* Retrieve the solution from CLIPS. */
      /*===================================*/
 
      for (int i = 0; i < 6; i++)
        {
         JTable theTable = (JTable) mainGrid.getComponent(i);
         int rowGroup = i / 2;
         int colGroup = i % 2;
            
         for (int r = 0; r < 2; r++)
           {
            for (int c = 0; c < 3; c++)
              { 
               resetValues[i][r][c] = theTable.getValueAt(r,c); 

               if ((resetValues[i][r][c] != null) &&
                   (! resetValues[i][r][c].equals("")))
                 { continue; }
                  
               String evalStr = "(find-all-facts ((?f possible)) " +
                                    "(and (eq ?f:row " + (r + (rowGroup * 2) + 1) + ") " +
                                         "(eq ?f:column " + (c + (colGroup * 3) + 1) + ")))";
               
               PrimitiveValue pv = clips.eval(evalStr);
//               System.out.println(evalStr);
               
               //pv.retain();
               System.out.println("pvsize>>" + pv.size());
               
               if (pv.size() != 1) continue;
               
               PrimitiveValue fv = pv.get(0);
                  
               theTable.setValueAt(" " + fv.getFactSlot("value") + " ",r,c);
              }         
           }
        }

      /*===============================================*/
      /* Any cells that have not been assigned a value */
      /* are given a '?' for their content.            */
      /*===============================================*/
         
      for (int i = 0; i < 6; i++)
        {
         JTable theTable = (JTable) mainGrid.getComponent(i);

         for (int r = 0; r < 2; r++)
           {
            for (int c = 0; c < 3; c++)
              { 
               if ((theTable.getValueAt(r,c) == null) ||
                   (theTable.getValueAt(r,c).equals("")))
                 { theTable.setValueAt("?",r,c);  }
              }         
           }
        }

      /*===================================*/
      /* Update the status of the buttons. */
      /*===================================*/

      jfrm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         
      solved = true;
      clearButton.setEnabled(false);
      resetButton.setEnabled(false);
      solveButton.setEnabled(false);
      techniquesButton.setEnabled(true);
           
      executionThread = null;
      
      isExecuting = false;
     }     

   /*#######################*/
   /* FocusListener Methods */
   /*#######################*/
   
   /***************/
   /* focusGained */
   /***************/  
   public void focusGained(FocusEvent e) {}

   /*************/
   /* focusLost */
   /*************/
   public void focusLost(FocusEvent e)
     {
      JTable theTable = (JTable) e.getComponent();
      int r = theTable.getEditingRow();
      int c = theTable.getEditingColumn();

      /*====================================================*/
      /* If a cell wasn't being edited, do nothing further. */
      /*====================================================*/

      if ((r == -1) || (c == -1)) return;

      /*========================*/
      /* Stop editing the cell. */
      /*========================*/
      
      TableCellEditor tableCellEditor = theTable.getCellEditor(r,c);
      tableCellEditor.stopCellEditing();
      
      /*=================================*/
      /* Clear selections for the table. */
      /*=================================*/

      theTable.clearSelection();
     }     
        
  }
