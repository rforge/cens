package edu.cens.text;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.deducer.Deducer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA. User: Neal Date: 5/23/11 Time: 7:59 AM To change
 * this template use File | Settings | File Templates.
 */
public class CorpusViewer extends JDialog
{

	String _corpus;

	int _maxLength;

	final JTextArea _text = new JTextArea(10, 35)
	{
		{
			setEnabled(true);
			setEditable(false);
			setBorder(BorderFactory.createRaisedBevelBorder());
		}
	};

	final JComboBox _corpuses = new JComboBox();

	final JSpinner _indexSpinner = new JSpinner();

	public CorpusViewer(String[] corpuses) throws REXPMismatchException
	{
		super((Frame) null, "Viewing ", true);

		for (String co : corpuses)
		{
			_corpuses.addItem(co);
		}

		_corpuses.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				setCorpus();
			}
		});

		_indexSpinner.addChangeListener(new ChangeListener()
		{
			
			public void stateChanged(ChangeEvent e)
			{
				viewDocument(_indexSpinner.getValue().hashCode());
			}
		});

		setPreferredSize(new Dimension(560, 350));

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JPanel jPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		jPanel.setLayout(layout);
		GridBagConstraints cc = new GridBagConstraints();
		cc.gridx = cc.gridy = 1;

		jPanel.add(_corpuses, cc);
		cc.gridx++;
		jPanel.add(_indexSpinner, cc);
		cc.gridy++;
		cc.gridx = 1;
		cc.gridwidth = 2;
		cc.weightx = 1;
		cc.weighty = 1;
		cc.fill = cc.BOTH;
		cc.insets = new Insets(10, 25, 25, 25);
		jPanel.add(new JScrollPane(_text), cc);
		cc.gridy++;

		setContentPane(jPanel);

		setCorpus();

		pack();
		this.setMinimumSize(this.getSize());

	}

	private void setCorpus()
	{
		try
		{
			_corpus = _corpuses.getSelectedItem().toString();
			if (_corpus != null && !_corpus.equals(""))
			{
			setTitle("Viewing " + _corpus);
			_maxLength = Deducer.eval(String.format("length(%s)", _corpus)).asInteger();
			_indexSpinner.setModel(new SpinnerNumberModel(1, 1, _maxLength, 1));
			viewDocument(1);
			}
			else
			{
				setTitle("No Corpus to View");
			}
		}
		catch (REXPMismatchException rexpm)
		{
		}
	}

	private void viewDocument(int idx)
	{
		try
		{
			_text.setText(Deducer.eval(
					String.format("%s[%s][[1]]", _corpus, idx)).asString());
		}
		catch (REXPMismatchException rexpm)
		{
		}

	}

	public static void main(String[] args)
	{
		CorpusViewer cv;
		try
		{
			cv = new CorpusViewer(new String[]{""});
			cv.setVisible(true);
			cv.setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		catch (REXPMismatchException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
