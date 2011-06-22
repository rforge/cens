package edu.cens.text;

/**
 * TODO 
 * - Add in Run, cancel, help button bar
 * - Add individual options menus
 * - Implement reordering
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.sun.tools.javac.util.List;

import edu.cens.text.OptionsButtonPanel;

public class PreprocessingTable extends JTable
{
	private Object [][] tableContents;
	public PreprocessingTable()
	{	
		super();
		
		JPopupMenu menu1 = new JPopupMenu();
		menu1.add(new JMenuItem("Stop!"));
		menu1.add(new JMenuItem("Meaningless"));
		menu1.add(new JMenuItem("Text"));
		
		JPopupMenu menu2 = new JPopupMenu();
		menu2.add(new JMenuItem("Stem"));
		menu2.add(new JMenuItem("Pointless"));
		menu2.add(new JMenuItem("Words"));
		
		tableContents = new Object [][]
		                      		{ 
		                      			{ new UpDownButtonPanel(), new Boolean(false), "Stop Words", new OptionsButtonPanel(menu1) },
		                      			{ new UpDownButtonPanel(), new Boolean(true), "Stem", new OptionsButtonPanel(menu2) },
		                      			{ new UpDownButtonPanel(), new Boolean(true), "Magic", new OptionsButtonPanel(menu1) },
		                      		};
		setModel(new PreproListModel(tableContents));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getColumn("Options").setCellRenderer(new OptionsColumnRenderer());
		getColumn("Options").setCellEditor(new OptionsColumnEditor());
		
		getColumn("Reorder").setCellRenderer(new OptionsColumnRenderer());
		getColumn("Reorder").setCellEditor(new OptionsColumnEditor());
		setRowHeight(30);
		this.setCellSelectionEnabled(false);
		this.setColumnSelectionAllowed(false);
		this.setRowSelectionAllowed(false);
		this.setFocusable(false);
	
		//this.setBorder(new LineBorder(Color.BLACK));
		this.setGridColor(Color.BLACK);
		this.setIntercellSpacing(new Dimension(3,3));
		this.getTableHeader().setReorderingAllowed(false);
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		//checkbox width
		getColumn("Enable").setPreferredWidth(35);
		
		//Reorder arrow buttons
		getColumn("Reorder").setPreferredWidth(25);
		
		//Action name
		getColumn("Action").setPreferredWidth(125);
		
		//Options button
		getColumn("Options").setPreferredWidth(25);
	}
	
	@Override
	public void tableChanged(TableModelEvent e)
	{
		super.tableChanged(e);
		repaint();
	}
	
	public void moveRowUp(int row)
	{
		if (row - 1 >= 0)
		{
			swapRows(row, row - 1);
		}
	}
	
	public void moveRowDown(int row)
	{
		if (row + 1 < this.getRowCount())
		{
			swapRows(row, row + 1);
		}
	} 
	
	public void swapRows(int a, int b)
	{
		Object [] rowA =  this.tableContents[a];
		Object [] rowB =  this.tableContents[b];
		System.out.println("Row A : " + rowA[2]);
		System.out.println("Row B : " + rowB[2]);
		System.out.println("");
		tableContents[a] = rowB;
		tableContents[b] = rowA;
		//table.setModel(new PreproListModel(tableContents));
		this.repaint();
	}
	
	public static void main(String[] args)
	{
		PreprocessingDialog frame = new PreprocessingDialog();
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
	
}

class OptionsColumnRenderer implements TableCellRenderer
{
	private UpDownButtonPanel reorderPanel;
	public OptionsColumnRenderer() 
	{
		super();
		reorderPanel = new UpDownButtonPanel();
	}
	
    public void setValue(Object value) 
    {
    }
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		//return JRadioButtonTableExample.panels[row];
		if (table.getColumnName(column).equalsIgnoreCase("Reorder"))
		{
			return reorderPanel;
		}
		return (Component) table.getModel().getValueAt(row, column);
	}
}

class OptionsColumnEditor extends AbstractCellEditor implements ItemListener, TableCellEditor
{
	// private JRadioButton button;
	private OptionsButtonPanel panel;
	private JButton button;
	
	public OptionsColumnEditor()
	{
		super();
		 //panel = new OptionsButtonPanel2();
	}

	//public RadioButtonEditor(JCheckBox checkBox){super(checkBox);}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column)
	{
		//change the panel according to the row
		//return panel;
		
		//return JRadioButtonTableExample.panels[row];
		if (table.getColumnName(column).equalsIgnoreCase("Reorder"))
		{
			((UpDownButtonPanel) value).setRow(row);
			((UpDownButtonPanel) value).setTable((PreprocessingTable) table);
			
		}
		return (Component) table.getModel().getValueAt(row, column);
	}

	@Override
	public Object getCellEditorValue()
	{
		return null;
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		super.fireEditingStopped();
	}
}

class PreproListModel extends AbstractTableModel
{
	String[] columnNames = { "Reorder", "Enable", "Action", "Options" };
	
	Object[][] tableContents;

	
	public PreproListModel(Object [][] tableContents)
	{
		this.tableContents = tableContents;
	}
	
    public int getColumnCount() 
    {
        return columnNames.length;
    }

    public int getRowCount() 
    {
        return tableContents.length;
    }

    public String getColumnName(int col) 
    {
        return columnNames[col];
    }
    
    public void setValueAt(Object value, int row, int col) 
    {
    	if (this.getColumnClass(col) == Boolean.class)
    	{
    		tableContents[row][col] = value;
    		fireTableCellUpdated(row, col);
    	}
    }


    public Object getValueAt(int row, int col) 
    {
        return tableContents[row][col];
    }
    
    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) 
    {
        return getValueAt(0, c).getClass();
    }
    
    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) 
    {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
            return this.getColumnClass(col) != String.class;
    }	
}

class OptionsButtonPanel extends JPanel
{
	private JButton button;
	JPopupMenu men;
	OptionsButtonPanel thePanel;

	public OptionsButtonPanel(JPopupMenu menu)
	{
		super();
		thePanel = this;
		this.setBackground(Color.WHITE);
		//this.add(new JLabel("options"), BorderLayout.CENTER);

		men = menu;//new JPopupMenu(menu);
		// men.setSelectionModel(new Sin)

		//men.add(new JMenuItem("nothing"));
		//men.add(new JMenuItem("to"));
		//men.add(new JMenuItem("see"));

		button = new BasicArrowButton(SwingConstants.EAST);
		this.add(button, BorderLayout.EAST);
		ActionListener al = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				Point p = button.getLocationOnScreen();
				men.setLocation((int) p.getX() + button.getWidth(),
						(int) p.getY());
				men.setInvoker(thePanel);
				men.setVisible(true);
			}
		};
		button.addActionListener(al);
	}
}

class PreprocessingDialog extends JFrame
{
	
	//public static OptionsButtonPanel2[] panels;

	//TODO separate model and view!
	JTable table;
	
	
	public PreprocessingDialog()
	{
		super("Preprocess");
		UIDefaults ui = UIManager.getLookAndFeel().getDefaults();
		UIManager.put("RadioButton.focus", ui.getColor("control"));

		//panels = new OptionsButtonPanel2[2];
		//panels[0] = new OptionsButtonPanel2();
		//panels[1] = new OptionsButtonPanel2();
		
		

		
		final PreprocessingDialog thisDialog = this;

		table = new PreprocessingTable();
			/*new JTable(new PreproListModel(tableContents))
		{
			@Override
			public void tableChanged(TableModelEvent e)
			{
				super.tableChanged(e);
				repaint();
			}
		};*/
		
		JScrollPane scroll = new JScrollPane(table);
		getContentPane().add(scroll);
		setSize(200, 140);
		setVisible(true);
	}
}
