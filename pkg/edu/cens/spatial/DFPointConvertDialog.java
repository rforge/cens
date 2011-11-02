package edu.cens.spatial;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.rosuda.JGR.layout.AnchorConstraint;
import org.rosuda.deducer.Deducer;
import org.rosuda.deducer.toolkit.HelpButton;
import org.rosuda.deducer.widgets.*;


public class DFPointConvertDialog extends RDialog implements ActionListener{

	private static final String HELP_URL = "index.php?n=Main.DeducerSpatial";
	
	private VariableSelectorWidget variableSelector;
	private SingleVariableWidget yaxis;
	private SingleVariableWidget xaxis;
	private TextFieldWidget dataNameField;

	private static DFPointConvertDialog inst;

	public static DFPointConvertDialog getInstance(){
		if(inst==null)
			inst = new DFPointConvertDialog();
		
		return inst;
	}
	
	public void initGUI(){
		super.initGUI();
		

		variableSelector = new VariableSelectorWidget();
		this.add(variableSelector, new AnchorConstraint(12, 428, 900, 12, 
				AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_REL, 
				AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_ABS));
		variableSelector.setPreferredSize(new java.awt.Dimension(216, 379));
		variableSelector.setTitle("Data");
		
		yaxis = new SingleVariableWidget("Latitude",variableSelector);
		this.add(yaxis, new AnchorConstraint(121, 978, 327, 460, 
				AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, 
				AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		yaxis.setPreferredSize(new java.awt.Dimension(276, 63));
		
		xaxis = new SingleVariableWidget("Longitude",variableSelector);
		this.add(xaxis, new AnchorConstraint(337, 978, 540, 460, 
				AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, 
				AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		xaxis.setPreferredSize(new java.awt.Dimension(276, 63));
		
		dataNameField = new TextFieldWidget("New data name");
		this.add(dataNameField, new AnchorConstraint(610, 978, 840, 460, 
				AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_REL, 
				AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		dataNameField.setPreferredSize(new java.awt.Dimension(187, 44));
		
		this.setTitle("Convert data.frame to Spatial Points");
		
		setOkayCancel(true,true,this);
		addHelpButton(HELP_URL);
		this.setSize(555, 445);
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		if(cmd=="Run"){
			String xvar = xaxis.getSelectedVariable();
			String yvar = yaxis.getSelectedVariable();
			String data = variableSelector.getSelectedData();
			String name = dataNameField.getValidatedText();
			if(name.length()==0)
				name = Deducer.getUniqueName(data+".sp");
			if(yvar==null || xvar==null || data==null){
				JOptionPane.showMessageDialog(this, "You must specify both an x and y variable");
				return;
			}
			
			//The below command converts non-numbers to the standard 'NA'
			//It seems however, that SpatialPointsDataFrame doesn't like 'NA'
			/*
			 		String command = name + " <- " +
					"SpatialPointsDataFrame(\n" +
					"as.data.frame(Map(function(x){as.numeric(as.character(x))}, \n" +
					data +"[,c('"+xvar+"', '"+yvar+"')] \n),\n" +
							"data="+data+",proj4string=CRS('+proj=longlat'))";
							*/
			
			//Filter out any rows w/o valid lat/lon
			String filteredData = Deducer.getUniqueName(data + "_temp");

			Deducer.execute(filteredData + " <- " + data);
			
			Deducer.execute("if (class(" + data + "[, '" + xvar +"']) == 'factor') {");
			Deducer.execute(filteredData + "[,'" + xvar + "'] <- as.numeric(as.character("+ filteredData + "[, '"+xvar+"']))");
			Deducer.execute("}");
			
			Deducer.execute("if (class(" + data + "[, '" + yvar +"']) == 'factor') {");
			Deducer.execute(filteredData + "[,'" + yvar + "'] <- as.numeric(as.character("+ filteredData + "[, '"+yvar+"']))");
			Deducer.execute("}");
			
		Deducer.execute(filteredData + " <- subset(" + filteredData + ",  \n" +
		"!is.na(" + xvar + ")  & !is.na("+yvar+") )");
			
			
//			Deducer.execute(filteredData + " <- subset(" + data + ",  \n" +
//					"!is.na(as.numeric(as.character(" + xvar + "))) \n" +
//							"&\n" +
//							"!is.na(as.numeric(as.character("+yvar+"))) \n" +
//									")");
			
			String command = name + " <- SpatialPointsDataFrame(" + filteredData + "[,c('"+xvar+"', '"+yvar+"')], data="+filteredData+",proj4string=CRS('+proj=longlat'))";
			command += "\n"+name+" <- spTransform("+name+",osm())";
			
			Deducer.execute(command);		//execute command as if it had been entered into the console
			
			Deducer.execute("rm(" + filteredData + ")");	
			
			this.setVisible(false);
			completed();	//dialog completed
		}else if(cmd=="Cancel")
			this.setVisible(false);
		else if(cmd=="Reset")
			reset();
	}
	
}
