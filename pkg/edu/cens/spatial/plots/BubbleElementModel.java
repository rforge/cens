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

public class BubbleElementModel extends ElementModel{
	RFunction rf;
	
	

	public BubbleElementModel(){
		init();
	}
	
	public void init(){
		rf = new RPointFunction("bubble_plot");
		rf.setViewType(null);
		ParamSpatialVariable pv = new ParamSpatialVariable("z");
		pv.setFormat(ParamSpatialVariable.FORMAT_WITH_DATA);
		pv.setTitle("Variable");//("Point size"); //Gail requested this name change
		rf.add(pv);
		
		
		
		ParamNumeric pn = new ParamNumeric("minRadius");
		pn.setTitle("Minimum size");
		pn.setDefaultValue(.01);
		pn.setLowerBound(0.0);
		pn.setValue(.01);
		rf.add(pn);
		
		pn = new ParamNumeric("maxRadius");
		pn.setTitle("Maximum size");
		pn.setDefaultValue(.05);
		pn.setLowerBound(0.0);
		pn.setValue(.05);
		rf.add(pn);
		
		ParamColor pc = new ParamColor("color");
		pc.setTitle("Color");
		pc.setDefaultValue(Color.decode("#F75252"));
		pc.setValue(Color.decode("#F75252"));
		pc.setRequired(false);
		rf.add(pc);
		
		
		iconLocation = "icons/geo_bubble.png";
		name = "Bubble";
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

	public BubbleElementModel clone(){
		BubbleElementModel newM = new BubbleElementModel();
		newM.rf = (RFunction) rf.clone();
		return newM;
	}
	
	public String getDataFrameArgumentName()
	{
		return ((ParamSpatialVariable) rf.get(0)).getData();
	}
	
	public void setDataFrameArgumentName(String argName)
	{
		((ParamSpatialVariable) rf.get(0)).setData(argName);
	}
}
