package edu.cens.text;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.rosuda.JGR.JGR;
import org.rosuda.deducer.Deducer;

import com.sun.tools.javac.util.List;

public class TextFileChooser
{
	
	private JFileChooser fc;
	JFrame actualDialogPanel;
	
	boolean useExistingCorpus = false;

	//String newCorpusName = null;
	JTextField newNameField;
	String existingCorpus = null;
	
	private static String mostRecentPath = null;
	public TextFileChooser()
	{
		fc = new JFileChooser();
		
		fc.setMultiSelectionEnabled(true); 
		
		fc.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String command = e.getActionCommand();
		        if (command.equals(JFileChooser.APPROVE_SELECTION)) 
		        {
					//initialize an almost empty corpus
					//Deducer.execute(corpName + " <- Corpus(VectorSource(c('')))");
					//Build the corpus
					
					String patternString = "";
					File[] pickedFiles = fc.getSelectedFiles();
					
					System.out.println(pickedFiles.length + " " + fc.isMultiSelectionEnabled());
					
					String enclosingDir = fc.getCurrentDirectory().getAbsolutePath();//pickedFiles[0].getParent();
					
					for (int i = 0; i < pickedFiles.length; i++)
					{
						String fileName = pickedFiles[i].getName();
					
						if (i != 0)
						{
							patternString += "|";
						}
						
						patternString += fileName;
						
						mostRecentPath = enclosingDir;
						
						//System.out.println(enclosingDir + " ---- " + fileName);
						
					}
					
					//System.out.println(patternString);
					
					if (! useExistingCorpus)
					{
						String newCorpusName = newNameField.getText();
						
						boolean isUnique = newCorpusName.equals(Deducer.getUniqueName(newCorpusName));
						
					
						boolean validCommand = true;
						 if (newCorpusName == null || newCorpusName.equals("") || Character.isDigit(newCorpusName.charAt(0)))
						{
							validCommand = false;
							Toolkit.getDefaultToolkit().beep();
							JOptionPane.showMessageDialog(null,
								    "You must give the corpus a name.",
								    "Alert",
								    JOptionPane.ERROR_MESSAGE);
						}
						else if (!isUnique) //not unique
						{
							
							int n = JOptionPane.showConfirmDialog(
								    null,
								    "The corpus name \"" + newCorpusName + "\" is already in use."
								    +"\nWould you like to overwrite the existing variable?",
								    "Warning",
								    JOptionPane.YES_NO_OPTION);
							validCommand = n == 0;

						}
							
						 if (validCommand)
						 {
							Deducer.execute
							//System.out.println
							(newCorpusName + " <- Corpus( " +
							"DirSource(" 
							+ "directory = '" + enclosingDir + "',"
							+ "pattern='" + patternString +"'"
								
							+ "))");
							actualDialogPanel.dispose();
						 }
						
					
					}
					else //>>>>>>>>>>>>>>>>>>>>>>>> use existing corpus >>>>>>>>>>>>
					{
						//The corpus to which we are adding the new documents
						String corpusAddee = existingCorpus; 
						String tempCorp = Deducer.getUniqueName("tempCorp");
						
						Deducer.execute
						//System.out.println
						(tempCorp + " <- Corpus( " +
						"DirSource(" 
						+ "directory = '" + enclosingDir + "',"
						+ "pattern='" + patternString +"'"
							
						+ "))");
						
						
						Deducer.execute
						//System.out.println
						(corpusAddee + "<- c(" + corpusAddee + ", " + tempCorp +")");
						
						Deducer.execute
						//System.out.println
						("rm(" + tempCorp + ")");
						
						actualDialogPanel.dispose();
						
					} //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		        } 
		        else if (command.equals(JFileChooser.CANCEL_SELECTION)) 
		        {
		          actualDialogPanel.dispose();
		        }
				
			}
		});
		
		actualDialogPanel = new JFrame();
		actualDialogPanel.setLayout(new BorderLayout());
		
		//fc.setControlButtonsAreShown(false); //Unfortunately necessary
		//removeAllButtons(fc);
	
		actualDialogPanel.add(fc, BorderLayout.CENTER);
		actualDialogPanel.add(constructExtraOptionsPanel(), BorderLayout.NORTH);
		
		
		//Remove the 'Make New folder' button, if present
		
		
	//	setFileSelectionMode(JFileChooser.FILES_ONLY);
	//	setDialogType(CUSTOM_DIALOG);
//		addChoosableFileFilter(new FileFilter()
//		{
//			public String getDescription()
//			{
//				return "Text files";
//			}
//			
//			public boolean accept(File f)
//			{
//				return f.getName().toLowerCase().endsWith(".txt");
//			}
//		});
		
//		addChoosableFileFilter(new FileFilter()
//		{
//			public String getDescription()
//			{
//				return "CSV files";
//			}
//			
//			public boolean accept(File f)
//			{
//				return f.getName().toLowerCase().endsWith(".csv");
//			}
//		});
		
	}

	
	public JPanel constructExtraOptionsPanel()
	{
		//////////////////////////////////////////////////
		// Construct the buttons /////////////////////////
		//////////////////////////////////////////////////
		
		JRadioButton makeNewCorpusButton = new JRadioButton("Make New Corpus");
		JRadioButton addToCorpusButton = new JRadioButton("Add to Existing Corpus");
		
		ButtonGroup group = new ButtonGroup();
		group.add(makeNewCorpusButton);
		group.add(addToCorpusButton);
		
		final JPanel nameOrCorpusPanel = new JPanel(new CardLayout());
		final String makeNewCorpus = "make new corpus";
		final String chooseExistingCorpus = "choose existing corpus";
		
		makeNewCorpusButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				CardLayout cl = (CardLayout)(nameOrCorpusPanel.getLayout());
				cl.show(nameOrCorpusPanel, makeNewCorpus);
				useExistingCorpus = false;
			}
		});
		
		addToCorpusButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				CardLayout cl = (CardLayout)(nameOrCorpusPanel.getLayout());
				cl.show(nameOrCorpusPanel, chooseExistingCorpus);
				useExistingCorpus = true;
			}
		});
		
		////////////////////////////////////////////
		//  The extra options panel  ///////////////
		////////////////////////////////////////////
		
		JPanel retPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		////////////////////////////////////////////
		//  The radio button section  //////////////
		////////////////////////////////////////////
		
		JPanel radButPan = new JPanel();
		
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		
		radButPan.add(makeNewCorpusButton, c);
		
		c.gridx = 1;
		
		radButPan.add(addToCorpusButton, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		
		retPanel.add(radButPan, c);
		
		////////////////////////////////////////////////////////
		//  The new-name/existing-corpus section  //////////////
		////////////////////////////////////////////////////////
		
		//-- New name panel --------------------------------------
		JPanel newNamePanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		newNamePanel.add(new JLabel("Save as: "), c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		newNameField = new JTextField();
		newNameField.setText(Deducer.getUniqueName("untitled.corpus"));
		
		newNamePanel.add(newNameField, c);

		//-- Existing corpus panel --------------------------------------
		JPanel existingCorpusPanel = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		
		c.gridx = 0;
		c.weightx = 0;
		existingCorpusPanel.add(new JLabel("Add to: "),c);
		
		c.gridx = 1;
		c.weightx = 1;
		c.fill = c.HORIZONTAL;
		
		//JFrame f = new JFrame(); f.setVisible(true);
	
		final RObjectChooser corpusSelector = new RObjectChooser();
		corpusSelector.setClassFilter("Corpus");
		corpusSelector.refreshObjects();
		if (corpusSelector.getObjectCount() > 0)
		{
			corpusSelector.setSelectedIndex(0);
		}
		
		corpusSelector.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				existingCorpus = (String) corpusSelector.getSelectedObject();
			}
		});

		existingCorpusPanel.add(corpusSelector,c);
		
		//-- Panel that switches between new-name and existing-corpus panels -------
		
		nameOrCorpusPanel.add(newNamePanel, makeNewCorpus);
		nameOrCorpusPanel.add(existingCorpusPanel, chooseExistingCorpus);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = c.HORIZONTAL;
		
		c.insets = new Insets(5, 15, 5, 15);
		
		retPanel.add(nameOrCorpusPanel, c);
		
		////////////////////////////////////////////
		//  A separator  ///////////////////////////
		////////////////////////////////////////////
		
		c.gridy = 2;
		c.gridwidth = 2;
		c.insets = new Insets(0, 5, 0, 5);
		
		retPanel.add(new JSeparator(SwingConstants.HORIZONTAL),c);
		
		/////////////////////////////////////////////
		//  Default to making new corpus  ///////////
		/////////////////////////////////////////////
		makeNewCorpusButton.setSelected(true);
		
		return retPanel;
	}
	
	public void run()
	{	
		if (mostRecentPath != null)
		{
			fc.setCurrentDirectory(new File(mostRecentPath));
		}
		
		actualDialogPanel.pack();
		actualDialogPanel.setVisible(true);
	}

	public static void main(String[] args)
	{
		TextFileChooser tfc = new TextFileChooser();
		tfc.actualDialogPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tfc.run();
	}
}
