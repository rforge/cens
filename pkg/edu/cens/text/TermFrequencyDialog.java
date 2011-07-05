package edu.cens.text;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.rosuda.JGR.layout.AnchorLayout;
import org.rosuda.deducer.Deducer;

import com.sun.java.swing.plaf.motif.MotifBorders.BevelBorder;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.xml.internal.ws.api.server.Container;

public class TermFrequencyDialog extends JDialog
{
	public static final String BAR_CHART = "Bar Chart";
	public static final String DOCUMENT_TERM_MATRIX = "Document-Term Matrix";
	public static final String TOTAL_FREQUENCIES = "Total Frequencies";
	private static final String[] VIEW_MODES = { TOTAL_FREQUENCIES, BAR_CHART, DOCUMENT_TERM_MATRIX };
	private static final Insets DIALOG_INSETS = new Insets(0, 7, 0, 7);
	private boolean decreasing;
	
	JTextField topPercentField;
	JTextField minFreqField;
	JTextField absoluteNTermsField;
	JTextField saveTotalsField;
	
	JComboBox dataSourceSelector;
	JComboBox viewMethodSelector;
	JComboBox sortMethodSelector;
	
	JRadioButton useAllButton = new JRadioButton();
	JRadioButton useTopNButton = new JRadioButton();
	JRadioButton useTopPercentButton = new JRadioButton();
	
	JPanel optionsPanel;
	
	GridBagConstraints optionsPanelConstraints;

	public TermFrequencyDialog(JFrame parent)
	{
		super(parent, "Term Frequency");
		decreasing = true;
		setSize(500, 300);
		
		//TODO : should probably separate construction / action listener setting from GUI arrangement.
		saveTotalsField = new JTextField();
		
		constructGui();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		// for (String corpus : corpuses)
		// {
		// dataSourceSelector.addItem(corpus);
		// }
	}

	public void setCopora(String[] newCorpora)
	{
		dataSourceSelector.removeAllItems();
		for (String s : newCorpora)
		{
			dataSourceSelector.addItem(s);
		}
	}

	public void setViewMethod(String method)
	{
		this.viewMethodSelector.setSelectedItem(method);
	}

	public void constructGui()
		{
			setLayout(new GridBagLayout());
			GridBagConstraints c = getTopLevelLayoutDefaults();
	
			// ////////////////////////////////////////////////////
			// / Source data selection panel //////////////////////
			// ////////////////////////////////////////////////////
			JPanel sourceDataSelectionPanel = constructSourceDataSelectionPanel();
			
	
			// ////////////////////////////////////////////////////
			// / OK/Cancel/Help panel /////////////////////////////
			// ////////////////////////////////////////////////////
			JPanel okPanel = constructOkHelpPanel();
	
	
			// --------------------------------------------------------------------------
			// +++++++++ Add data selector to toplevel dialog +++++++++++++
			c = getTopLevelLayoutDefaults();
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.NORTH;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 1;
			
			c.insets = DIALOG_INSETS;
			this.add(sourceDataSelectionPanel, c);
	
			// +++++++++ Add Use and Sort to toplevel dialog +++++++++++++
			// -- First, Put use and sort in the same panel
			JPanel useAndSortPanel = new JPanel(new GridBagLayout());
			c = getTopLevelLayoutDefaults();
			c.anchor = GridBagConstraints.FIRST_LINE_END;
			c.gridx = 0;
			//useAndSortPanel.add(usePanel, c);
	
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
			c.fill = GridBagConstraints.VERTICAL;
			//useAndSortPanel.add(sortPanel, c);
	
			// -- Then, put this panel in the top level dialog
			c = getTopLevelLayoutDefaults();
			c.anchor = GridBagConstraints.PAGE_START;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
	
			c.insets = DIALOG_INSETS;
			
			//this.add(useAndSortPanel, c);
			
			// +++++++++ Add min freq panel to toplevel dialog +++++++++++++
			c = getTopLevelLayoutDefaults();
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
	
			c.weighty = 0;
			c.weightx = 0;
			c.anchor = c.NORTH;
			c.insets = DIALOG_INSETS;
			this.add(constructFilteringPanel(), c);
	
			// +++++++++ Add save-name panel to toplevel dialog +++++++++++++
			// this.add(saveNamePanel, c);
	
			// +++++++++ Add View mode selection panel to toplevel dialog
			// +++++++++++++
			c = getTopLevelLayoutDefaults();
			c.fill = GridBagConstraints.BOTH;
			//c.anchor = GridBagConstraints.NORTH;
			c.weightx = 1;
			c.weighty = 1;
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 1;
			c.insets = DIALOG_INSETS;
			this.add(constructViewMethodPanel(), c);
	
			// +++++++++ Add OkCancel panel to toplevel dialog +++++++++++++
			c = getTopLevelLayoutDefaults();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.PAGE_START;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 4;
			c.gridwidth = 1;
	
			c.insets = DIALOG_INSETS;
			this.add(okPanel, c);
	
			this.pack();
			this.setMinimumSize(this.getSize());
			this.setResizable(false);// MAYBE not a good idea, but normal for dialogs.
		}

	public String getViewMethod()
	{
		return (String) this.viewMethodSelector.getSelectedItem();
	}

	// This is what the old options dialog used....
	public String getSorted()
	{
		// "alpha" : "freq";
		if (sortMethodSelector.getSelectedItem().equals("by frequency"))
		{
			return "freq";
		}
		else if (sortMethodSelector.getSelectedItem().equals("alphanumerically"))
		{
			return "alpha";
		}
		else
		{
			throw new IllegalStateException(
					"Unrecognized value '"+ sortMethodSelector.getSelectedItem() +"'  from combo box!");
		}
		// return ((SortOptions) _sortedCMB.getSelectedItem()).getType();
	}

	public int getMinFrequency()
	{
		return Integer.parseInt(minFreqField.getText());
	}

	public int getAbsoluteNTerms()
	{
		if (this.useTopNButton.isSelected())
		{
			return Integer.parseInt(this.absoluteNTermsField.getText());
		}
		else
		{
			return 0;
		}
	}

	public int getPercent()
	{
		if (this.useTopPercentButton.isSelected())
		{
			return Integer.parseInt(this.topPercentField.getText());
		}
		else
		{
			return 0;
		}
		// return _thresholdCMB.getSelectedItem().hashCode();
	}

	public boolean getAsc()
	{
		return decreasing;
		// return ((SortOptions) _sortedCMB.getSelectedItem()).getSortAsc();
	}

	public String getCorpus()
	{
		return this.dataSourceSelector.getSelectedItem().toString();
	}

	private GridBagConstraints getTopLevelLayoutDefaults()
	{
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.ipadx = 5;
		c.insets = new Insets(3, 3, 3, 3);
		//c.ipady = 5;
		return c;
	}

	private String getSourceCorpus() // the "sorpus", if you will
	{
		return (String) this.dataSourceSelector.getSelectedItem();
	}

	private JPanel constructOkHelpPanel()
	{
		JPanel okPanel = new JPanel(new GridBagLayout());
		JButton okButton = new JButton("View");
		JButton cancelButton = new JButton("Close");
		getRootPane().setDefaultButton(okButton);

		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				setVisible(false);
			}
		});
		
		ActionListener okAction = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				executeVisualization();
			}

		};

		okButton.addActionListener(okAction);

		GridBagConstraints c = getTopLevelLayoutDefaults();
		okPanel.add(new JLabel(""), c);

		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 1;
		okPanel.add(cancelButton, c);
		c.gridx = 2;
		okPanel.add(okButton, c);
		return okPanel;
	}

	private JPanel constructSourceDataSelectionPanel()
	{
		JPanel sourceDataSelectionPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = getTopLevelLayoutDefaults();
		// c.ipadx = 5;
		c.ipady = 5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0;

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;

		JLabel sourceDatLab = new JLabel("source data:");
		
		
		sourceDataSelectionPanel.add(sourceDatLab, c);
		
		
		dataSourceSelector = new JComboBox();
		
		dataSourceSelector.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				//TODO validate!
				saveTotalsField.setText(dataSourceSelector.getSelectedItem()
						.toString() + ".term_freq");
			}
		});

		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		 c.anchor = GridBagConstraints.WEST;

		sourceDataSelectionPanel.add(dataSourceSelector, c);
		return sourceDataSelectionPanel;
	}

	private JPanel constructFilteringPanel()
	{
		GridBagConstraints c = getTopLevelLayoutDefaults();
		c.insets = new Insets(0, 0, 0, 0);
	
		JPanel usePanel = new JPanel(new GridBagLayout());
	
		usePanel.setBorder(BorderFactory.createTitledBorder("Use:"));
		// warning! this panels default gridbag constraints perhaps shouldn't be
		// the same as the top level dialog's.
	
		useAllButton = new JRadioButton();
		useTopNButton = new JRadioButton();
		useTopPercentButton = new JRadioButton();
	
		useAllButton.setSelected(true);
		// this line ensures each this radio button row has same height as
		// others.
		useAllButton.setPreferredSize(new Dimension(useAllButton
				.getPreferredSize().width,
				new JTextField().getPreferredSize().height));
	
		ButtonGroup useGroup = new ButtonGroup();
		useGroup.add(useAllButton);
		useGroup.add(useTopNButton);
		useGroup.add(useTopPercentButton);
		
		c.anchor = c.WEST;
		c.fill = c.NONE;
		// ======== 'Use all' radio button row =======================
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		usePanel.add(useAllButton, c);
		// add text for use all
		c.gridx = 1;
		c.gridwidth = 3;
		c.weightx = 0;
		usePanel.add(new JLabel("All Terms"), c);
	
		// ======= 'Use top #' radio button row =======================
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		usePanel.add(useTopNButton, c);
	
		// add text for Top # terms
		c.gridx = 1;
		c.gridwidth = 1;
		usePanel.add(new JLabel("Top"), c);
	
		c.gridx = 2;
		this.absoluteNTermsField = new JTextField("10");
		absoluteNTermsField.setPreferredSize(new Dimension(50, absoluteNTermsField
				.getPreferredSize().height));
		usePanel.add(absoluteNTermsField, c);
	
		// ======= 'Use top N %' radio button row =======================
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		usePanel.add(useTopPercentButton, c);
	
		// add text for Top # terms
		c.gridx = 1;
		c.gridwidth = 1;
		usePanel.add(new JLabel("Top"), c);
	
		c.gridx = 2;
	
		this.topPercentField = new JTextField("100");
		topPercentField.setPreferredSize(new Dimension(50, topPercentField
				.getPreferredSize().height));
		usePanel.add(topPercentField, c);
	
		c.gridx = 3;
		c.weightx = 1;
		usePanel.add(new JLabel("%"), c);
		
		///////////////////////////////////////////////////
		// Min Frequency Stuff ///////////////////////////////////
		/////////////////////////////////////////////////
		
		JPanel minFreqPanel = new JPanel(new GridBagLayout());
		
		c = getTopLevelLayoutDefaults();
		
		c.fill = c.NONE;
		c.weighty = 0;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0;
		minFreqPanel.add(new JLabel("Frequency ³"), c);
		c.gridx = 1;
		c.weightx = 1;
		
		//c.fill = c.HORIZONTAL;
		minFreqField = new JTextField(4);
		minFreqField.setText("0");
		minFreqPanel.add(minFreqField,c);
		
		//p.setBorder(BorderFactory.createEtchedBorder());
		minFreqPanel.setBorder(BorderFactory.createTitledBorder("Filtering:"));
		minFreqPanel.setMinimumSize(minFreqPanel.getPreferredSize());
		
		c.gridy = 1;
		c.weighty = 1;
		minFreqPanel.add(new JLabel(""),c);
		
		JPanel allP = new JPanel(new GridBagLayout());
		c = getTopLevelLayoutDefaults();
		c.weighty = 1;
		c.fill = c.BOTH;
		allP.add(usePanel,c);
		c.gridx = 1;
		allP.add(minFreqPanel, c);
		
	
		
		return allP;
	}

	private JPanel constructViewOptionsPanel()
	{
		JPanel ret = new JPanel(new GridBagLayout());
		ret.setBorder(BorderFactory.createTitledBorder("View Options:"));
		
		JPanel allOptionsPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = getTopLevelLayoutDefaults();
	
		
		if (this.viewMethodSelector.getSelectedItem().equals(BAR_CHART))
		{
			c = getTopLevelLayoutDefaults();
			c.anchor = c.NORTH;
			//c.insets = new Insets(0, 5, 5, 5);
			c.ipady = 0; c.ipadx = 0;
			c.insets = new Insets(0, 0, 0, 0);
			allOptionsPanel.add(constructSortPanel(), c);
		}
		else if (this.viewMethodSelector.getSelectedItem().equals(TOTAL_FREQUENCIES))
		{
			c = getTopLevelLayoutDefaults();
			c.anchor = c.CENTER;
			c.gridwidth = 2;
			c.weighty = 0;
			//c.insets = new Insets(0, 5, 5, 5);
			allOptionsPanel.add(constructSortPanel(), c);
			
			c.gridy = 1;
			c.weightx = 1;
			c.weighty = 0;
			c.fill = c.HORIZONTAL;
			c.gridwidth = 2;
			allOptionsPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);
			
			c.weightx = 0;
			c.weighty = 0;
			c.gridy = 2;
			c.gridwidth = 1;
			allOptionsPanel.add(new JLabel("Save Frequencies as Variable:"), c);
			
			c.weightx = 1;
			c.gridx = 0;
			c.gridy = 3;
			allOptionsPanel.add(saveTotalsField,c);
			
			c.gridy = 3;
			c.gridx = 1;
			c.weightx = 0;
			c.fill = c.NONE;
			c.anchor = c.NORTHEAST;
			
			JButton saveTotalsButton = new JButton("Save");
			allOptionsPanel.add(saveTotalsButton, c);
			
			saveTotalsButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					saveTotalsAsDataFrame(saveTotalsField.getText());
					
				}
			});
			
		}
		else
		{
			allOptionsPanel.add(new JLabel("Everything else"), c);
		}
		c = getTopLevelLayoutDefaults();
		c.gridy = 1;
		c.anchor = c.NORTHWEST;
		c.fill = c.BOTH;
		
		ret.add(allOptionsPanel,c);
		return ret;
	}

	protected void saveTotalsAsDataFrame(String saveName)
	{
		// TODO Implement some basic redundancy avoidance.
		
		//Save the frequency totals to a temporary variable
		String tempTotals = Deducer.getUniqueName("freqTotals");
		Deducer.execute(tempTotals + " <- " + getTermFreqCall());
		
		//NOTE: format of the output of the above call is a little weird.
		//This is why I have the weird [1,] bracket indexing.
		
		//Save the totals 
		//saveName <- d(term=names(tempTotals[1,]),freq=tempTotals[1,])
		Deducer.execute(saveName + " <- d(term=names(" + tempTotals + "), freq=" + tempTotals +")");
		
		//delete the temporary variable
		Deducer.execute("rm(" + tempTotals + ")");
		
	}

	private JPanel constructSortPanel()
	{
		GridBagConstraints c = getTopLevelLayoutDefaults();
		JPanel sortPanel = new JPanel(new GridBagLayout());
	
		//sortPanel.setBorder(BorderFactory.createTitledBorder("Sort: "));
	
		JRadioButton descendingButton = new JRadioButton("descending");
		JRadioButton ascendingButton = new JRadioButton("ascending");
	
		ButtonGroup sortGroup = new ButtonGroup();
		sortGroup.add(descendingButton);
		sortGroup.add(ascendingButton);
	
		ascendingButton.addActionListener(new ActionListener()
		{
	
			@Override
			public void actionPerformed(ActionEvent e)
			{
				decreasing = false;
			}
		});
	
		descendingButton.addActionListener(new ActionListener()
		{
	
			@Override
			public void actionPerformed(ActionEvent e)
			{
				decreasing = true;
			}
		});
		
		descendingButton.setSelected(true);
	
		c.anchor =c.EAST;
		c.weighty = 0;
		
		sortPanel.add(new JLabel("Sort:"),c);
		
		sortMethodSelector = new JComboBox();
		sortMethodSelector.addItem("alphanumerically");
		sortMethodSelector.addItem("by frequency");
	
		// add combo box
		c.weighty = 0;
		c.weightx = 0;
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor =c.WEST;
		sortPanel.add(sortMethodSelector, c);
	
		JPanel radButtonPanel = new JPanel(new GridBagLayout());
		
		// add descending radio button
		c.anchor = GridBagConstraints.LINE_START;
		// c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		//sortPanel.add(descendingButton, c);
	
		// add ascending radio button
		c.gridx = 1;
		c.gridy = 2;
		//sortPanel.add(ascendingButton, c);
		
		//Put radio buttons on separate panel
		c = getTopLevelLayoutDefaults();
		radButtonPanel.add(descendingButton, c);
		c.gridx = 1;
		radButtonPanel.add(ascendingButton, c);
	
		c = getTopLevelLayoutDefaults();
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = c.HORIZONTAL;
		sortPanel.add(radButtonPanel, c);
		
		return sortPanel;
	}

	private JPanel constructViewMethodPanel()
	{
		final JPanel p = new JPanel(new GridBagLayout());
	
		GridBagConstraints c = getTopLevelLayoutDefaults();
		c.gridx = 0;
		c.gridy = 0;
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = c.EAST;
	
		p.add(new JLabel("View As:"), c);
	
		c.gridx = 1;
		c.weightx = 1;
		c.anchor = c.WEST;
	
		viewMethodSelector = new JComboBox();
		p.add(viewMethodSelector, c);
		
		final TermFrequencyDialog thisPtr = this;
		
		viewMethodSelector.addActionListener(new ActionListener()
		{		
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				//optionsPanel = getViewModeOptionsPanel();
				if (optionsPanel != null)
				{
					p.remove(optionsPanel);
					optionsPanel = constructViewOptionsPanel();
					p.add(optionsPanel, optionsPanelConstraints);
					thisPtr.setMinimumSize(null);
					thisPtr.pack();
					thisPtr.setMinimumSize(thisPtr.getSize());
					p.validate();
					thisPtr.repaint();
					
				}
			}
		});
	
		for (String s : VIEW_MODES)
		{
			viewMethodSelector.addItem(s);
		}
	
		
	
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		optionsPanel = this.constructViewOptionsPanel();
		optionsPanel.setEnabled(false);
		c.anchor = c.NORTH;
		c.fill = c.BOTH;
		c.weighty = 1;
		
		
		optionsPanelConstraints = c;
		p.add(optionsPanel, c);
		// p.setBorder(BorderFactory.createTitledBorder("View:"));
	
		return p;
	}

	private void executeVisualization()
	{
		// TODO validate selected name
		// TODO save matrix name somewhere?
		// String generateDTM = saveNameField.getText() +
		// " <-  DocumentTermMatrix(" + getSourceCorpus() + ")";
		// System.out.println(generateDTM);
		// Deducer.execute(generateDTM);
		// cens.txt_barplot(cens.term_freq(get(" + getCorpus() + "),
		// 100, sorted, decreasing));


		String termFreqCommand = getTermFreqCall();
		
		if (getViewMethod().equals(BAR_CHART))
		{
			Deducer.execute("cens.txt_barplot(" + termFreqCommand + ");");
			
			Deducer.execute("dev.set()", false); //give the plot focus
		}
		else if (getViewMethod().equals(TOTAL_FREQUENCIES))
		{
			Deducer.execute("print(" + termFreqCommand + ");");
//					+ getCorpus() + ", " + percentage + ", " + "\""
//					+ sorted + "\", "
//					+ new String("" + ascending).toUpperCase() + "));");
		}
	}
	
	private String getTermFreqCall()
	{
		int percentage = getPercent();
		int absoluteNTerms = getAbsoluteNTerms();
		int minFreq = getMinFrequency();
		String sorted = getSorted();
		boolean ascending = getAsc();

		String termFreqCall = "cens.term_freq(" + 
				"d=" + getCorpus() + ", " + 
				"percent=" + percentage + ", " +
				"topN=" + absoluteNTerms + ", " +
				"sorted=\"" + sorted + "\", " +
				"decreasing=" + ("" + ascending).toUpperCase() + ", " +
				"minFreq=" + minFreq + ")";
		return termFreqCall;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TermFrequencyDialog dlg = new TermFrequencyDialog(f)
		{
			@Override
			public void setVisible(boolean b)
			{
				super.setVisible(b);
				if (!b)
				{
					System.exit(0);
				}
			}
		};
		dlg.setCopora(new String[] { "AAAAAAA", "BBBBBBBBBBB",
				"^^^^^^^^^^^^^^^" });
		dlg.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				System.exit(0);
			}
			
			

		});

		dlg.setVisible(true);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

}
