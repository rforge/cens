package edu.cens.text;

import org.rosuda.JGR.JGR;
import org.rosuda.deducer.Deducer;
import org.rosuda.deducer.toolkit.HelpButton;
import org.rosuda.deducer.toolkit.OkayCancelPanel;
import org.rosuda.deducer.widgets.ObjectChooserWidget;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA. User: Neal Date: 4/8/11 Time: 7:09 AM
 */

public class ProcessDialog extends JDialog
{

	PreprocessingTable _table;
	
	 JFrame parent = JGR.MAINRCONSOLE == null ? new JFrame() : JGR.MAINRCONSOLE;
	
	protected ObjectChooserWidget _source = new ObjectChooserWidget("Source Corpus:", this)
	{
		{
			setClassFilter("Corpus");
			refreshObjects();
		}
	};

	// private JLabel _source = new JLabel("Source");

	private JTextField saveAsNameField = new JTextField(15);

	private DefaultListModel _model = new DefaultListModel();
	{
		int n = ProcessCmd.values().length;
		for (int i = 0; i < n; i++)
		{
			_model.addElement(
					ProcessCmd.values()[i]
					//new ProcessActionPanel(ProcessCmd.values()[i])
					);
		}
	}


	public ProcessDialog()
	{
		setTitle("Preprocess Corpus...");
		
		_source.getComboBox().addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				if (saveAsNameField != null)
				{
					String uniqueName = Deducer.getUniqueName(_source.getComboBox().getSelectedItem() + ".processed");
					saveAsNameField.setText(uniqueName);
				}
			}
		});
		
		add(new JPanel()
		{
			{

				setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5, 5, 5, 5);
				c.gridy = 0;
				c.gridwidth = 2;

				add(_source, c);

				c.gridy++;

				
				///////////////////////////////////////////////////////
				/////////// Build the table of actions ////////////////
				///////////////////////////////////////////////////////
				
				int nActions = ProcessCmd.values().length;
		
				_table = new PreprocessingTable(nActions);
				
				for (int i = 0; i < nActions; i++)
				{
					//Set the action's name
					//_table.setActionName(ProcessCmd.values()[i].getLabel(), i);
					_table.setAction(ProcessCmd.values()[i], i
							//new ProcessActionPanel(ProcessCmd.values()[i]), i
							);
					
					//Add any relevant options to the action
					JPopupMenu ithMenu =  new JPopupMenu();
					JMenuItem [] items = ProcessCmd.values()[i].getExtraOptions();
					
					for (JMenuItem item : items)
					{
						ithMenu.add(item);
					}
					_table.setHasOption(items.length > 0, i);
					_table.setOptionsMenu(ithMenu, i);
				}
				
				JPanel p = new JPanel();
				p.setBorder(BorderFactory.createTitledBorder("Actions:"));
				p.add(_table);
				
				add(p, c); //this is the new prepocessing table
				//add(_list, c); // this is the old one

				c.weighty = 1;
				c.weightx = 1;
				c.gridy++;
				c.gridwidth = 2;
				c.fill = GridBagConstraints.BOTH;
				add(new JLabel(), c); // spacing blah
				
				c.gridy++;
				c.gridx = 0;
				c.gridwidth = 1;
				c.weighty = 0;
				c.weightx = 0;
				c.insets = new Insets(0, 15, 0, 0);
				c.ipadx = 10;
				
				add(new JLabel("Save Corpus As:"), c);
				
				c.gridx = 1;
				c.gridwidth = 1;
				c.weighty = 0;
				c.weightx = 1;
				c.insets = new Insets(0, 0, 0, 0);
				
				add(saveAsNameField, c);

				c.gridx = 0;
				c.gridy++;
				c.gridwidth = 2;
				c.weightx = 1;
				c.weighty = 0;
				//c.anchor = GridBagConstraints.SOUTH;
				c.fill = GridBagConstraints.HORIZONTAL;
				//c.insets = new Insets(10, 10, 10, 10);
				add(new DeducerOkCancelPanel(this.getRootPane(), "Save as:", "Cancel")
				{
					
					protected void ok()
					{
						doOK();
					}
					
					
					protected void cancel()
					{
						doCancel();
					}
				}, c);
			}
		});

		pack();
		setMinimumSize(this.getSize());

	}

	public boolean doCancel()
	{
		dispose();
		return true;
	}

	public boolean doOK()
	{
		String s = _source.getModel().toString();
		
		
		String t = //_source.getModel().toString(); //Just overwrite the unprocessed  corpus
		saveAsNameField.getText(); //use the new name
	
		int nEnabled = 0;
		
		for (int i = 0; i < ProcessCmd.values().length ; i++)
		{
			if (_table.isEnabled(i))
			{
				nEnabled ++;
				ProcessCmd command = (ProcessCmd) _table.getAction(i);
				Deducer.execute(t + "<- " + command.getRCmd(s) + ";\n");
				//System.out.println(t + "<- " + p._command.getRCmd(s) + ";\n");
				s = t;
			}
			
		}
	
		if (nEnabled == 0) //
		{
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(getContentPane(),
				    "No preprocessing actions were selected!" +
				    "\nYou can enable an action by checking the box next to its name.",
				    "Warning",
				    JOptionPane.WARNING_MESSAGE);
			return false;
		}
		else
		{
			dispose(); //TODO restore this line
			return true;
		}
	}

	private boolean debugForceShow = false;
	
	
	public void setVisible(boolean arg0)
	{
		if (_source.getModel() == null && !debugForceShow)
		{
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(getContentPane(),
			    "You have not yet created any corpuses."
				+
				"\nCreate a corpus with \"Extract Corpus\" in the Text menu.",
			    "Warning",
			    JOptionPane.WARNING_MESSAGE);
			dispose();
		}
		else
		{
			super.setVisible(arg0);
		}
	}
	
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ProcessDialog dlg = new ProcessDialog();
		f.setVisible(true);
		dlg.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				System.exit(0);
			}

		});
		dlg.debugForceShow = true;
		dlg.setVisible(true);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

}
