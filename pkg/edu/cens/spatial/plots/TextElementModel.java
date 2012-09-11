package edu.cens.spatial.plots;

import java.awt.Color;

import javax.swing.JDialog;

import org.rosuda.deducer.widgets.param.Param;
import org.rosuda.deducer.widgets.param.ParamCharacter;
import org.rosuda.deducer.widgets.param.ParamColor;
import org.rosuda.deducer.widgets.param.ParamNumeric;
import org.rosuda.deducer.widgets.param.RFunction;
import org.rosuda.deducer.widgets.param.RFunctionDialog;

import edu.cens.spatial.plots.widgets.ParamSpatialVariable;
import edu.cens.spatial.plots.widgets.RPointFunction;

public class TextElementModel extends ElementModel{
	RFunction rf;
	
	

	public TextElementModel(){
		init();
	}
	
	public void init(){
		rf = new RPointFunction("spatialTextPlot");
		rf.setViewType(null);
		ParamSpatialVariable pv = new ParamSpatialVariable("text");
		pv.setFormat(ParamSpatialVariable.FORMAT_WITH_DATA_CHARACTER);
		pv.setTitle("Text");
		rf.add(pv);
		
		ParamNumeric pn = new ParamNumeric("cex");
		pn.setTitle("Size");
		pn.setDefaultValue(1);
		pn.setLowerBound(0);
		pn.setValue(1.0);
		rf.add(pn);
		
		pn = new ParamNumeric("adj");
		pn.setTitle("Position");
		pn.setDefaultValue(.5);
		pn.setLowerBound(0);
		pn.setUpperBound(1);
		pn.setValue(.5);
		rf.add(pn);
		
		ParamColor pc = new ParamColor("col");
		pc.setTitle("Color");
		pc.setRequired(false);
		rf.add(pc);
		
		iconLocation = "icons/geo_text.png";
		name = "Text";
	}
	
	public JDialog getView() {
		RFunctionDialog rfd = new RFunctionDialog(rf);
		rfd.setSize(500, 400);
		rfd.setRun(false);
		return rfd;
	}

	public String getCall() {
		return rf.getCall();
	}

	public String checkValid() {
		return rf.checkValid();
	}

	public TextElementModel clone(){
		TextElementModel newM = new TextElementModel();
		newM.rf = (RFunction) rf.clone();
		return newM;
	}
	
	public String getDataFrameArgumentName()
	{
		return (String) ((ParamSpatialVariable) rf.get(0)).getData();
	}
	
	public void setDataFrameArgumentName(String argName)
	{
		((ParamSpatialVariable) rf.get(0)).setData(argName);
		//rf.get(0).setValue(argName);
	}
}
