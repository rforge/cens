/**
 * Dialog for viewing and manipulating term frequency in a variety of ways. 
 * You can currently view term frequencies as:
 * - Word clouds:
 * Controls for font size, color, and rotation are provided
 * 
 * - Simple lists:
 * Controls for sorting, and saving the list as a dataframe are provided
 * 
 * - Bar plots:
 * Controls for sorting are provided
 * 
 * TODO Too much functionality is crammed into this class. The particulars of 
 * each viewing method should be factored into its own class.
 * 
 * TODO it would make sense to view the term-document matrix from this dialog as well
 */
package edu.cens.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.rosuda.deducer.Deducer;

public class TermFrequencyDialog extends JDialog
{
	public static final String BAR_CHART = "Bar Chart";
	public static final String DOCUMENT_TERM_MATRIX = "Document-Term Matrix";
	public static final String TOTAL_FREQUENCIES = "Total Frequencies";
	public static final String WORD_CLOUD = "Word Cloud";
	private static final String[] VIEW_MODES = { TOTAL_FREQUENCIES, BAR_CHART, WORD_CLOUD };
	private static final Insets DIALOG_INSETS = new Insets(0, 7, 0, 7);
	private boolean decreasing;
	private boolean useDocumentFrequency;

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

	JFrame parent;

	GridBagConstraints optionsPanelConstraints;

	// Word cloud settings ============================
	//This is a sign that word cloud should somehow have its own class
	//A little abstract class that has getters for the important options,
	//and an abstract method for generating an options panel.  Something similar
	//for the other view methods would be sensible as well.
	double WC_rotatePercentage;
	double WC_minFontSize;
	double WC_maxFontSize;
	String WC_colors;


	public TermFrequencyDialog(JFrame parent)
	{
		super(parent, "Term Frequency");
		setLocationByPlatform(true);
		this.parent = parent;

		decreasing = true;
		useDocumentFrequency = false;

		//TODO : should probably separate construction / action listener setting from GUI arrangement.
		saveTotalsField = new JTextField();

		constructGui();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		// for (String corpus : corpuses)
		// {
		// dataSourceSelector.addItem(corpus);
		// }
	}

	public void setCorpora(String[] newCorpora)
	{
		if (newCorpora != null && dataSourceSelector != null)
		{
			dataSourceSelector.removeAllItems();
			for (String s : newCorpora)
			{
				dataSourceSelector.addItem(s);
			}
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

		//////////////////////////////////////////////////////
		// Source data selection panel //////////////////////
		//////////////////////////////////////////////////////
		JPanel sourceDataSelectionPanel = constructSourceDataSelectionPanel();


		//////////////////////////////////////////////////////
		// OK/Cancel/Help panel /////////////////////////////
		//////////////////////////////////////////////////////
		JPanel okPanel = constructOkHelpPanel();

		//////////////////////////////////////////////////////
		// Document/Term Frequency Panel /////////////////////
		//////////////////////////////////////////////////////




		// --------------------------------------------------------------------------
		// +++++++++ Add data selector to toplevel dialog +++++++++++++
		c = getTopLevelLayoutDefaults();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;

		//c.insets = DIALOG_INSETS;
		this.add(sourceDataSelectionPanel, c);

		// +++++++++ Add min freq panel to toplevel dialog +++++++++++++
		c = getTopLevelLayoutDefaults();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;

		c.weighty = 0;
		c.weightx = 0;
		c.anchor = c.NORTH;
		//c.insets = DIALOG_INSETS;
		this.add(constructFilteringPanel(), c);


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
		//c.insets = DIALOG_INSETS;
		this.add(constructViewMethodPanel(), c);

		// +++++++++ Add OkCancel panel to toplevel dialog +++++++++++++
		c = getTopLevelLayoutDefaults();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;

		//c.insets = DIALOG_INSETS;
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
		if (this.dataSourceSelector.getSelectedItem() != null)
		{
			return this.dataSourceSelector.getSelectedItem().toString();
		} 
		else
		{
			return null;
		}
	}

	private GridBagConstraints getTopLevelLayoutDefaults()
	{
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(3, 3, 3, 3);
		return c;
	}

	private JPanel constructOkHelpPanel()
	{
		JPanel okPanel = new JPanel(new GridBagLayout());
		JButton okButton = new JButton("View");
		JButton cancelButton = new JButton("Close");
		getRootPane().setDefaultButton(okButton);

		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				setVisible(false);
			}
		});

		ActionListener okAction = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				doVisualization();
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

		JLabel sourceDatLab = new JLabel("Source Data:");


		sourceDataSelectionPanel.add(sourceDatLab, c);


		dataSourceSelector = new JComboBox();

		dataSourceSelector.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//TODO validate!
				String selectedCorpus = getCorpus();
				if (selectedCorpus != null)
				{
					saveTotalsField.setText(selectedCorpus + ".term_freq");
				}
			}
		});

		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;

		sourceDataSelectionPanel.add(dataSourceSelector, c);
		return sourceDataSelectionPanel;
	}

	private JPanel constructFrequencyMethodPanel()
	{
		JPanel frequencyMethodPanel = new JPanel(new GridBagLayout());

		JRadioButton termFreqRadBut = new JRadioButton("Term Frequency");
		JRadioButton docFreqRadBut = new JRadioButton("Document Frequency");
		termFreqRadBut.setSelected(true);

		ButtonGroup freqButGroup = new ButtonGroup();
		freqButGroup.add(termFreqRadBut);
		freqButGroup.add(docFreqRadBut);

		termFreqRadBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				useDocumentFrequency = false;
			}
		});

		docFreqRadBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				useDocumentFrequency = true;
			}
		});

		GridBagConstraints c = getTopLevelLayoutDefaults();
		frequencyMethodPanel.add(termFreqRadBut, c);
		c.gridx = 1;
		frequencyMethodPanel.add(docFreqRadBut, c);
		return frequencyMethodPanel;
	}

	private JPanel constructFilteringPanel()
	{
		GridBagConstraints c = getTopLevelLayoutDefaults();
		//c.insets = new Insets(0, 0, 4, 0);
		c.ipadx = 0;
		c.ipady = 0;

		JPanel usePanelInner = new JPanel(new GridBagLayout());

		useAllButton = new JRadioButton();
		useTopNButton = new JRadioButton();
		useTopPercentButton = new JRadioButton();

		useAllButton.setSelected(true);
		//This line ensures each this radio button row has same height as others.
		useAllButton.setPreferredSize
		(
				new Dimension
				(
						useAllButton.getPreferredSize().width, 
						new JTextField().getPreferredSize().height
				)
		);

		ButtonGroup useGroup = new ButtonGroup();
		useGroup.add(useAllButton);
		useGroup.add(useTopNButton);
		useGroup.add(useTopPercentButton);

		c.anchor = c.WEST;
		c.fill = c.NONE;
		c.weighty = 0;
		c.ipady = 3;
		c.ipadx = 7;
		// ======== 'Use all' radio button row =======================
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		usePanelInner.add(useAllButton, c);
		// add text for use all
		c.gridx = 1;
		c.gridwidth = 3;
		c.weightx = 0;
		usePanelInner.add(new JLabel("All Terms"), c);

		// ======= 'Use top #' radio button row =======================
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		usePanelInner.add(useTopNButton, c);

		// add text for Top # terms
		c.gridx = 1;
		c.gridwidth = 1;
		usePanelInner.add(new JLabel("Top"), c);

		c.gridx = 2;
		c.gridwidth = 2;
		c.fill = c.HORIZONTAL;
		this.absoluteNTermsField = new JTextField("100",3);

		usePanelInner.add(absoluteNTermsField, c);

		// ======= 'Use top N %' radio button row =======================
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		usePanelInner.add(useTopPercentButton, c);

		// add text for Top # terms
		c.gridx = 1;
		c.gridwidth = 1;
		usePanelInner.add(new JLabel("Top"), c);

		c.gridx = 2;
		
		this.topPercentField = new JTextField("100", 3);
		//		topPercentField.setPreferredSize(new Dimension(50, topPercentField
		//				.getPreferredSize().height));
		usePanelInner.add(topPercentField, c);

		c.gridx = 3;
		c.weightx = 0;
		usePanelInner.add(new JLabel("%"), c);

		Border usePanelBorder = new TitledBorder("Use:")
		{
//			public Insets getBorderInsets(Component c)
//			{
//				return new Insets(20, 10, 10, 10);
//			}
		};//("Use:");
		usePanelInner.setBorder(usePanelBorder);

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
		//minFreqPanel.setMinimumSize(minFreqPanel.getPreferredSize());

		c.gridy = 1;
		c.weighty = 1;
		minFreqPanel.add(new JLabel(""),c);

		// ==================================================
		JPanel allP = new JPanel(new GridBagLayout());
		c = getTopLevelLayoutDefaults();

		c.weighty = 0;
		c.gridwidth = 2;
		allP.add(constructFrequencyMethodPanel(), c);

		c.gridy = 1;
		c.gridx = 0;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = c.BOTH;
		allP.add(usePanelInner,c);

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
				public void actionPerformed(ActionEvent e)
				{
					saveTotalsAsDataFrame(saveTotalsField.getText());

				}
			});

		}
		else if (this.viewMethodSelector.getSelectedItem().equals(WORD_CLOUD))
		{
			Double [] defaultFontSizes = {.25, 1.0, 2.0, 4.0, 8.0};

			final JComboBox minFontSizeComboBox = new JComboBox(defaultFontSizes);
			final JComboBox maxFontSizeComboBox = new JComboBox(defaultFontSizes);

			minFontSizeComboBox.setEditable(true);
			minFontSizeComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					WC_minFontSize = (Double) minFontSizeComboBox.getSelectedItem();//Double.parseDouble((String) minFontSizeComboBox.getSelectedItem());
					maxFontSizeComboBox.setSelectedItem(Math.max(WC_minFontSize, WC_maxFontSize));
				}
			});


			maxFontSizeComboBox.setEditable(true);
			maxFontSizeComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					WC_maxFontSize = (Double) maxFontSizeComboBox.getSelectedItem();//Double.parseDouble((String) maxFontSizeComboBox.getSelectedItem());
					minFontSizeComboBox.setSelectedItem(Math.min(WC_minFontSize, WC_maxFontSize));
				}
			});
			minFontSizeComboBox.setSelectedItem(.25);
			maxFontSizeComboBox.setSelectedItem(4.0);
			
			String[] colorings = 
					{
					"Black", 
					"Black/White",
					"Spectral", 
					"Dark2",
					"Purple/Green",
					"Red/Cyan",
					"Blue/Gold"
					};
			final JComboBox coloringComboBox = new JComboBox(colorings);
			//Use these: 
			//http://www.oga-lab.net/RGM2/func.php?rd_id=RColorBrewer:ColorBrewer
			coloringComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String colorItem = (String) coloringComboBox.getSelectedItem();
					WC_colors = "'black'";
					
					if (colorItem.equals("Black"))
					{
						WC_colors = "'black'";
					}
					else if (colorItem.equals("Black/White"))
					{
						WC_colors = "make.color.scale(c(.95,.95,.95),c(0,0,0),256,.4)";
					}
					else if (colorItem.equals("Spectral"))
					{
						WC_colors = "brewer.pal(11,'Spectral')";
					}
					else if (colorItem.equals("Dark2"))
					{
						WC_colors = "brewer.pal(8,'Dark2')";
					}
					else if (colorItem.equals("Purple/Green"))
					{
						WC_colors = "brewer.pal(11,'PRGn')";
					}
					else if (colorItem.equals("Red/Cyan"))
					{
						WC_colors  = "make.color.scale(c(0,1,1), c(1,0,0),256,.25)";
					}
					else if (colorItem.equals("Blue/Gold"))
					{
						WC_colors = "make.color.scale(c(1,1,0), c(0,0,.85),256,.5)";
					}
					else
					{
						throw new IllegalStateException("unrecognized color! '"+ colorItem +"' from combo box!");
					}
				}
			});
			coloringComboBox.setSelectedIndex(0);

			final JCheckBox rotateTermsCheckBox = new JCheckBox("Randomly Rotate Terms");
			rotateTermsCheckBox.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					if (e.getStateChange() == ItemEvent.SELECTED)
					{
						WC_rotatePercentage = .25;
					} 
					else
					{
						WC_rotatePercentage = 0;
					}
				}
			});
			rotateTermsCheckBox.setSelected(true);

			c = getTopLevelLayoutDefaults();	

			c.ipadx = 0;
			c.ipady = 0;

			c.gridx = 0;
			c.anchor = c.EAST;
			c.fill = c.NONE;
			c.weightx = 0;
			allOptionsPanel.add(new JLabel("Min Font Size:"),c);


			c.gridx = 1;
			c.anchor = c.WEST;
			c.fill = c.NONE;
			c.weightx = 1;
			allOptionsPanel.add(minFontSizeComboBox,c);

			c.gridx = 2;
			c.gridy = 0;
			c.anchor = c.EAST;
			c.fill = c.NONE;
			c.weightx = 0;
			allOptionsPanel.add(new JLabel("Max Font Size:"),c);

			c.gridx = 3;
			c.gridy = 0;
			c.anchor = c.WEST;
			c.fill = c.NONE;
			c.weightx = 1;
			allOptionsPanel.add(maxFontSizeComboBox,c);

			c.gridx = 0;
			c.gridy = 1;
			c.anchor = c.EAST;
			c.fill = c.NONE;
			c.weightx = 0;
			c.gridwidth =1;
			allOptionsPanel.add(new JLabel("Coloring:"),c);

			c.gridx = 1;
			c.gridy = 1;
			c.anchor = c.WEST;
			c.fill = c.HORIZONTAL;
			c.weightx = 1;
			c.gridwidth =3;
			allOptionsPanel.add(coloringComboBox,c);

			c.gridx = 1;
			c.gridy = 2;
			c.gridwidth = 3;
			c.anchor = c.WEST;
			c.fill = c.NONE;
			allOptionsPanel.add(rotateTermsCheckBox,c);
			
			minFontSizeComboBox.setPreferredSize(new Dimension(60, minFontSizeComboBox.getPreferredSize().height));
			maxFontSizeComboBox.setPreferredSize(new Dimension(60, maxFontSizeComboBox.getPreferredSize().height));

			//allOptionsPanel.add(new JLabel("HEY! YOU!"), c);
			//c.gridy++;
			//allOptionsPanel.add(new JLabel("Get off of my word cloud."), c);
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
			public void actionPerformed(ActionEvent e)
			{
				decreasing = false;
			}
		});

		descendingButton.addActionListener(new ActionListener()
		{

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
		"decreasing=" + (""+ascending).toUpperCase() + ", " +
		"minFreq=" + minFreq + "," +
		"useDocFreq=" + (""+useDocumentFrequency).toUpperCase() + ")";
		return termFreqCall;
	}

	private void doVisualization()
	{
		// TODO validate selected name
		// TODO save matrix name somewhere?
		// String generateDTM = saveNameField.getText() +
		// " <-  DocumentTermMatrix(" + getSourceCorpus() + ")";
		// System.out.println(generateDTM);
		// Deducer.execute(generateDTM);
		// cens.txt_barplot(cens.term_freq(get(" + getCorpus() + "),
		// 100, sorted, decreasing));

		if (this.getCorpus() == null)
		{
			JOptionPane.showMessageDialog(getContentPane(),
					"You do not have any corpuses to visualize!",
					"Warning",
					JOptionPane.WARNING_MESSAGE);
		} 
		else
		{

			String termFreqCommand = getTermFreqCall();

			if (getViewMethod().equals(BAR_CHART))
			{
				//TODO add options to set font size, margins?
				//barplot(words, las=2);
				String opar = Deducer.getUniqueName("opar");
				Deducer.execute(opar + " <- par()",false); //save the original margin parameters
				Deducer.execute("par(mar=c(8, 4, 4, 0.5))",false); //give the plot more space at the bottom for long words.
				Deducer.execute(
						"barplot(" 
						+ 
						termFreqCommand + "," 
						+
						"cex.names=0.8," //make the terms a bit smaller
						+
				" las=2);");

				Deducer.execute("dev.set()", false); //give the plot focus
				Deducer.execute("par("+ opar +")",false);
			}
			else if (getViewMethod().equals(TOTAL_FREQUENCIES))
			{
				Deducer.execute("print(" + termFreqCommand + ");");
				//					+ getCorpus() + ", " + percentage + ", " + "\""
				//					+ sorted + "\", "
				//					+ new String("" + ascending).toUpperCase() + "));");
			} 
			else if (getViewMethod().equals(WORD_CLOUD))
			{
				//TODO sanity check # of words in cloud, give warning if too huge



				String tempFreq = Deducer.getUniqueName("tempFreq");
				Deducer.execute(tempFreq + "<-" + termFreqCommand);
				Deducer.execute("wordcloud(" +
						"names("+ tempFreq + "), " + 
						tempFreq +
						", min.freq=0 " +
						
						//given its penchant for not being able to display every word, 
						//it's probably best to display the most frequent words first.
						", random.order=FALSE" + 
						", scale = c(" + WC_maxFontSize + ", " + WC_minFontSize + ")" +
						", colors=" + WC_colors +
						", rot.per=" + WC_rotatePercentage +")");
				Deducer.execute("rm(" + tempFreq + ")");
				Deducer.execute("dev.set()", false); //give the plot focus
			}
		}
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
		dlg.setCorpora(new String[] { 
				//"AAAAAAA", "BBBBBBBBBBB", "^^^^^^^^^^^^^^^" 
		});
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
