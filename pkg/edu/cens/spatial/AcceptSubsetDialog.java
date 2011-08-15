package edu.cens.spatial;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import edu.cens.spatial.plots.MapController;

public class AcceptSubsetDialog extends JDialog
{
	private final MapController mc;
	private boolean keepSelected = true;
	//DeducerDataFrameNameField nameField;
	
	public AcceptSubsetDialog(JFrame parent, final MapController mc)
	{
		super(parent);
		this.mc = mc;
		setModal(false); //should be able to drag around and stuff
		//this.setAlwaysOnTop(true);
		//nameField = new DeducerDataFrameNameField();
		
		this.addWindowListener(new WindowAdapter() 
		{
		  public void windowClosing(WindowEvent e)
		  {
		  	mc.stopSubsetting();
		  }
		});

		initGui();
	}

	private void initGui()
	{
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JButton acceptButton = new JButton("Accept");
		acceptButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				boolean ok = true;//nameField.tryExecute();
				if (ok)
				{
					setCursor(new Cursor(Cursor.WAIT_CURSOR));
//					try
//					{
//						Thread.sleep(3000);
//					}
//					catch (InterruptedException e1)
//					{
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
					
					mc.executeSubsetting(keepSelected);
					setVisible(false);
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					//dispose(); //TODO remove this if we need to remember the state from the last subset action
				}
			}
		});
		
		JButton rejectButton = new JButton("Cancel");
		rejectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				mc.stopSubsetting();
				setVisible(false);
			}
		});
		
		final JRadioButton keepOnlySelectionButton = new JRadioButton("Keep Only Selected");
		final JRadioButton removeSelectionButton = new JRadioButton("Delete Selected");
		ButtonGroup radButGrp = new ButtonGroup();
		radButGrp.add(keepOnlySelectionButton);
		radButGrp.add(removeSelectionButton);
		
		keepOnlySelectionButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
					keepSelected = keepOnlySelectionButton.isSelected();
			}
		});
		
		removeSelectionButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//keepSelected = keepOnlySelectionButton.isSelected();
				keepSelected = !removeSelectionButton.isSelected();
			}
		});
		keepOnlySelectionButton.setSelected(true);
		
		
//		c.gridx = 0;
//		c.gridy = 0;
//		//this.add(new JLabel("Subset Name:"), c);
//		
//		c.gridy = 0;
//		c.gridx = 1;
//		c.gridwidth = 1;
//		c.weightx = 1;
//		c.fill = c.HORIZONTAL;
////		nameField.setPreferredSize(new Dimension(
////				250,
////				nameField.getPreferredSize().height
////		));
////		this.add(nameField, c);
		
		c.gridy = 0;
		c.gridx = 0;
		this.add(keepOnlySelectionButton, c);
		c.gridy = 1;
		c.gridx = 0;
		this.add(removeSelectionButton, c);
		
		c.gridy = 2;
		c.gridx = 1;
		c.gridwidth = 1;
		this.add(acceptButton, c);
		
		c.gridx = 0;
		this.add(rejectButton, c);
		
		this.pack();
		this.setResizable(false);
	}

	public void setVisible(boolean b)
	{
		this.setLocationRelativeTo(getParent());
		super.setVisible(b);
	}
	
	public static void main(String[] args)
	{
		JDialog d = new AcceptSubsetDialog(null, null)
		{
			public void setVisible(boolean b)
			{
				if (!b)
				{
					System.exit(0);
				}
				super.setVisible(b);
			}
			
		};
		d.removeWindowListener(d.getWindowListeners()[0]);
		d.addWindowListener(new WindowAdapter()
		{
		  public void windowClosing(WindowEvent e)
		  {
		  	System.exit(0);
		  }
		});
		d.setVisible(true);
		
	}
}
