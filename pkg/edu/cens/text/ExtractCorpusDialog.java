package edu.cens.text;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 


/*
 * GridBagLayoutDemo.java requires no other files.
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.rosuda.JGR.layout.AnchorConstraint;
import org.rosuda.JGR.layout.AnchorLayout;
import org.rosuda.deducer.Deducer;
import org.rosuda.deducer.toolkit.OkayCancelPanel;
import org.rosuda.deducer.toolkit.SingletonAddRemoveButton;
import org.rosuda.deducer.toolkit.SingletonDJList;
import org.rosuda.deducer.toolkit.VariableSelector;
import org.rosuda.deducer.widgets.TextFieldWidget;

import com.sun.codemodel.internal.JLabel;
import com.sun.xml.internal.ws.Closeable;

// TODO add help button

public class ExtractCorpusDialog extends JDialog implements ActionListener
{
	private VariableSelector variableSelector;
	private SingletonDJList factor;
	private SingletonAddRemoveButton addTextButton;
	private TextFieldWidget newCorpusNameField;
	private OkayCancelPanel okayCancelPanel;

	private boolean userHasNamed;

	public ExtractCorpusDialog(JFrame parent)
	{
		super(parent, "Extract Corpus");

		///////////////////////////////////////
		////// ASSEMBLE THE GUI ///////////////
		///////////////////////////////////////
		if (parent != null) 
		{
			Dimension parentSize = parent.getSize(); 
			Point p = parent.getLocation(); 
			setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
		}
		GridBagConstraints c = new GridBagConstraints();

		////////////////////////////////////////
		////// The Variable selection panel ////
		////////////////////////////////////////
		JPanel messagePane = new JPanel(new GridBagLayout());
		int insetVal = 7;
		c.insets = new Insets(insetVal, insetVal, insetVal, insetVal);
		// ======= The variable selector list ==========
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.8;
		c.weighty = 1;
		c.gridheight = 2;
		c.anchor = GridBagConstraints.LINE_START;
		//messagePane.add(new JButton("Button 1"), c);
		variableSelector = new VariableSelector();
		messagePane.add(variableSelector, c);
		variableSelector.setPreferredSize(new java.awt.Dimension(100, 100));
		c = new GridBagConstraints(); //reset;
		c.insets = new Insets(insetVal, insetVal, insetVal, insetVal);

		// ====== The selected variable text field =======
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 0;
		c.ipady = 0;
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = .2;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;

		JPanel factorPanel = new JPanel(new BorderLayout());
		factorPanel.setPreferredSize(new Dimension(100,50));
		factorPanel.setBorder(BorderFactory.createTitledBorder("Text Variable"));
		factor = new SingletonDJList();
		//factor.setSize(new Dimension(100,25));
		//factor.setPreferredSize(new Dimension(100,25));
		factorPanel.add(factor, BorderLayout.CENTER);
		messagePane.add(factorPanel, c);
		c = new GridBagConstraints(); //reset;
		c.insets = new Insets(insetVal, insetVal, insetVal, insetVal);

		// ====== The arrow button =======================

		c.fill = GridBagConstraints.NONE;
		c.ipadx = 0;
		c.ipady = 0;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		addTextButton = new SingletonAddRemoveButton(
				new String[]{"Add Factor","Remove Factor"},
				new String[]{"Add Factor","Remove Factor"}, factor,variableSelector);
		addTextButton.setPreferredSize(new java.awt.Dimension(34, 34));
		messagePane.add(addTextButton, c);

		c = new GridBagConstraints(); //reset;
		c.insets = new Insets(insetVal, insetVal, insetVal, insetVal);

		// ====== The new corpus name text field =======================

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 0;
		c.ipady = 2;
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = .2;
		c.weighty = 0;
		c.anchor = GridBagConstraints.CENTER;
		newCorpusNameField = new TextFieldWidget("Corpus Name");
		//newCorpusNameField.setSize(new Dimension(100,25));
		newCorpusNameField.setPreferredSize(new Dimension(100,50));
		messagePane.add(newCorpusNameField, c);

		c = new GridBagConstraints(); //reset;
		c.insets = new Insets(insetVal, insetVal, insetVal, insetVal);

		getContentPane().add(messagePane,BorderLayout.CENTER);

		//======  The OK/Cance/help button panel =======
		JPanel footerPanel = new JPanel(new GridBagLayout());
		okayCancelPanel = new OkayCancelPanel(false,true,this);
		okayCancelPanel.setPreferredSize(new Dimension(250,25));
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		//messagePane.add(okayCancelPanel, c);
		//messagePane.add(new JButton("VERY BOTTOM"), c);

		c.fill = GridBagConstraints.NONE;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.8;
		c.weighty = 1;
		c.anchor = GridBagConstraints.LINE_END;

		footerPanel.add(okayCancelPanel, c);
		c = new GridBagConstraints(); //reset;
		c.insets = new Insets(insetVal, insetVal, insetVal, insetVal);

		getContentPane().add(footerPanel, BorderLayout.SOUTH);

		c = new GridBagConstraints(); //reset;
		c.insets = new Insets(insetVal, insetVal, insetVal, insetVal);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack(); 
		
		////////////////////////////////////////
		///// Register action listeners ////////
		////////////////////////////////////////
		addTextButton.addActionListener(new ActionListener() 
		{	
			public void actionPerformed(ActionEvent e) 
			{
				if (variableSelector.getJList().getSelectedValue() != null && factor.getModel().getSize() == 0)
				//(factor.getModel().getSize() > 0)
				{
					//newCorpusNameField.getTextField().setText("something there: " + factor.getModel().getElementAt(0));
					newCorpusNameField.getTextField().setText(variableSelector.getJList().getSelectedValue()+".corpus");
				}
			}
		});
				
		ActionListener runAction = new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String newCorpusName = getNewCorpusName();
				String dataFrame = getSelectedDataFrame();
				String variable = getSelectedVariable();
				String cmd = newCorpusName + "<- Corpus(VectorSource("+dataFrame+"$"+variable+"))";
				//System.out.println(cmd);
				
				//Possible problems:
				// - No variable selected
				// - Corpus name is empty
				// - Corpus name already defined
				boolean validCommand = true;
				if (variable == null)
				{
					validCommand = false;
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(getContentPane(),
						    "You must select a text variable before you can extract a corpus.",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
				}
				else if (newCorpusName == null)
				{
					validCommand = false;
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(getContentPane(),
						    "You must give the corpus a name.",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
				}
				else if (! newCorpusName.equals(Deducer.getUniqueName(newCorpusName))) //not unique
				{
					
					int n = JOptionPane.showConfirmDialog(
						    getContentPane(),
						    "The corpus name \"" + newCorpusName + "\" is already in use."
						    +"\nWould you like to overwrite the existing variable?",
						    "Warning",
						    JOptionPane.YES_NO_OPTION);
					validCommand = n == 0;

				}
				
				if (validCommand)
				{
					dispose();
					Deducer.execute(cmd);
				}
			}
		};	
		
		ActionListener cancelAction = new ActionListener() 
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
			}
		
		};
		
		okayCancelPanel.getApproveButton().addActionListener(runAction);
		okayCancelPanel.getCancelButton().addActionListener(cancelAction);
		
		//factor.setMo
		//((DefaultListModel) factor.getModel()).add(0, "1thing");
		setMinimumSize(new Dimension(300, 200));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
	}

	public String getNewCorpusName()
	{
		String proposedName = newCorpusNameField.getTextField().getText();
		if (!proposedName.equals(""))
		{
			return proposedName;
		}
		else
		{
			return null;
		}
	}
	
	public String getSelectedVariable()
	{
		if (factor.getModel().getSize() > 0)
		{
			return factor.getModel().getElementAt(0).toString();
		}
		else
		{
			return null;
		}
		//variableSelector.getJList().getSelectedValue().toString();
	}
	
	public String getSelectedDataFrame()
	{
		// TODO might crash if no data frames are present
		if (variableSelector.getSelectedData() != null)
		{
			return variableSelector.getSelectedData().toString();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Test the dialog
	 * @param args unused
	 */
	public static void main(String[] args) 
	{

		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ExtractCorpusDialog dlg = new ExtractCorpusDialog(f);
		f.setVisible(true);
		dlg.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				System.exit(0);
			}

		});
		
		dlg.setVisible(true);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		// TODO Auto-generated method stub

	}
}
