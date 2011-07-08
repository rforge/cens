package edu.cens.text;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import org.rosuda.deducer.toolkit.HelpButton;

public abstract class DeducerOkCancelPanel extends JPanel
{	
	
	DeducerOkCancelPanel()
	{
		this(null);
	}
	
	 DeducerOkCancelPanel(JRootPane rootPane)
	 {
		 this(rootPane, "OK", "Cancel");
	 }
	
	 DeducerOkCancelPanel(
			 JRootPane rootPane,
			 String okLabel, 
			String cancelLabel
			)
	{

			super(new GridBagLayout());
			JButton okButton = new JButton("Ok");
			JButton cancelButton = new JButton("Cancel");
			
			if (rootPane != null)
			{
				rootPane.setDefaultButton(okButton);
			}

			ActionListener okAction = new ActionListener()
			{
				
				
				public void actionPerformed(ActionEvent e)
				{
					ok();
				}
			};
			
			ActionListener cancelAction = new ActionListener()
			{
				
				
				public void actionPerformed(ActionEvent e)
				{
					cancel();
				}
			};
				okButton.addActionListener(okAction);

				cancelButton.addActionListener(cancelAction);

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(10, 10, 5, 5);
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.fill = c.NONE;
			c.anchor = c.WEST;
			HelpButton helpButton = new HelpButton("");
			add(helpButton, c);

			c.fill = GridBagConstraints.NONE;
			c.weightx = 0;
			c.weighty = 0;
			
			c.gridx = 1;
			add(cancelButton, c);
			
			c.gridx = 2;
			add(okButton, c);
			
	}
	 
	 protected abstract void ok();
	 
	 protected abstract void cancel();
}
