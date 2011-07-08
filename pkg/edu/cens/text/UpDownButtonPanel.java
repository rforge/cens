package edu.cens.text;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;


public class UpDownButtonPanel extends JPanel
{
	PreprocessingTable table;
	int row;
	public UpDownButtonPanel()
	//(PrepocessingDialog prepocessingDialog)
	{
		super();
		row = -1; //will be intialized later by "setRow"
		this.table = null;// Also, set later// prepocessingDialog;
		//this.add(new JLabel("up"), BorderLayout.CENTER); 
		this.setLayout(new GridLayout(2,0));
		JButton upButton = new BasicArrowButton(SwingConstants.NORTH);
		JButton downButton = new BasicArrowButton(SwingConstants.SOUTH);
		
		this.add(upButton);
		this.add(downButton);
		
		upButton.setSize(30, 17);
		downButton.setSize(30, 17);
		
		
		ActionListener upAction = new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				table.moveRowUp(row);
			}
		};
		
		ActionListener downAction = new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				table.moveRowDown(row);
			}
		};
		
		upButton.addActionListener(upAction);
		downButton.addActionListener(downAction);
	}
	
	public void setRow(int row)
	{
		this.row = row;
	}

	public void setTable(PreprocessingTable table)
	{
		this.table = table;
	}
}
