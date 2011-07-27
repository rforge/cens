package edu.cens.text;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.rosuda.deducer.Deducer;

public class FrequencyBarChartViewer extends AbstractTermFrequencyViewer
{
	public FrequencyBarChartViewer(TermFrequencyDialog tfDialog)
	{
		super(tfDialog);
	}

	public JPanel getOptionsPanel()
	{
		JPanel retPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = getTopLevelLayoutDefaults();
		c.anchor = c.NORTH;
		//c.insets = new Insets(0, 5, 5, 5);
		c.ipady = 0; c.ipadx = 0;
		retPanel.add(constructSortPanel(), c);
		return retPanel;
	}

	public String toString()
	{
		return "Bar Plot";
	}

	public void executeView()
	{
		//TODO add options to set font size, margins?
		//barplot(words, las=2);
		String opar = Deducer.getUniqueName("opar");
		Deducer.execute(opar + " <- par()",false); //save the original margin parameters
		Deducer.execute("par(mar=c(8, 4, 4, 0.5))",false); //give the plot more space at the bottom for long words.
		Deducer.execute(
				"barplot(" 
				+ 
				tfDialog.getTermFreqCall(this.getExtraTermFreqArgs()) + "," 
				+
				"cex.names=0.8," //make the terms a bit smaller
				+
		" las=2);");

		Deducer.execute("dev.set()", false); //give the plot focus
		Deducer.execute("par("+ opar +")",false);
		
	}

}
