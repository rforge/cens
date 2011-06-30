package edu.cens.text;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA. User: Neal Date: 5/12/11 Time: 11:28 AM
 */
public class TermFreqOptionsDialog extends JDialog
{

	private boolean _ok = false;

	private enum SortOptions
	{
		alphaAsc("Alpha (Asc)"), alphaDesc("Alpha (Desc)"), freqAsc(
				"Freq (Asc)"), freqDesc("Freq (Desc)");

		final String _label;

		private SortOptions(String label)
		{
			_label = label;
		}

		private String getType()
		{
			return this == alphaAsc || this == alphaDesc ? "alpha" : "freq";
		}

		private boolean getSortAsc()
		{
			return this == alphaAsc || this == freqAsc;
		}

		@Override
		public String toString()
		{
			return _label;
		}

	}

	private JComboBox _corpusCMB = new JComboBox()
	{
		{
			setEditable(false);
			setBorder(BorderFactory.createTitledBorder("Corpus:"));
			setPreferredSize(new Dimension(200, 45));
		}
	};

	private JComboBox _thresholdCMB = new JComboBox()
	{
		{
			setEditable(true);
			setBorder(BorderFactory.createTitledBorder("Percent:"));
			setPreferredSize(new Dimension(200, 45));
			addItem(100);
			addItem(1);
			addItem(10);
		}
	};

	private JComboBox _sortedCMB = new JComboBox()
	{
		{
			setEditable(false);
			setPreferredSize(new Dimension(200, 45));
			setBorder(BorderFactory.createTitledBorder("Sort:"));
			for (SortOptions so : SortOptions.values())
			{
				addItem(so);
			}
		}
	};

	final ActionListener _okListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String cmd = e.getActionCommand();
			_ok = "OK".equals(cmd);
			TermFreqOptionsDialog.this.dispose();
		}
	};

	public TermFreqOptionsDialog(String... corpuses)
	{
		super((Frame) null, "Corpus Options", true);

		setPreferredSize(new Dimension(220, 220));

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		for (String corpus : corpuses)
		{
			_corpusCMB.addItem(corpus);
		}

		JPanel jPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		jPanel.setLayout(layout);
		GridBagConstraints cc = new GridBagConstraints();
		cc.gridx = cc.gridy = 1;
		cc.gridwidth = 2;

		jPanel.add(_corpusCMB, cc);
		cc.gridy++;
		jPanel.add(_thresholdCMB, cc);
		cc.gridy++;
		jPanel.add(_sortedCMB, cc);
		cc.gridy++;

		jPanel.add(new JLabel(""));
		cc.gridy++;

		cc.gridwidth = 1;
		cc.ipadx = 10;

		JButton ok = new JButton("OK");
		ok.setPreferredSize(new Dimension(80, 30));
		ok.addActionListener(_okListener);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(_okListener);
		cancel.setPreferredSize(new Dimension(90, 30));

		jPanel.add(ok, cc);
		cc.gridx++;
		jPanel.add(cancel, cc);

		setContentPane(jPanel);

		pack();

	}

	public String getSorted()
	{
		return ((SortOptions) _sortedCMB.getSelectedItem()).getType();
	}

	public boolean getAsc()
	{
		return ((SortOptions) _sortedCMB.getSelectedItem()).getSortAsc();
	}

	public int getPercent()
	{
		return _thresholdCMB.getSelectedItem().hashCode();
	}

	public String getCorpus()
	{
		return _corpusCMB.getSelectedItem().toString();
	}

	public boolean isOk()
	{
		return _ok;
	}

	public static void main(String[] args)
	{
		new TermFreqOptionsDialog("A", "B", "C").setVisible(true);
	}

}
