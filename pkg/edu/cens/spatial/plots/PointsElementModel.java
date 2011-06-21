package edu.cens.spatial.plots;

import java.util.Vector;

import javax.swing.JDialog;

import org.rosuda.deducer.widgets.param.Param;
import org.rosuda.deducer.widgets.param.ParamColor;
import org.rosuda.deducer.widgets.param.ParamLogical;
import org.rosuda.deducer.widgets.param.ParamNumeric;
import org.rosuda.deducer.widgets.param.ParamRObject;
import org.rosuda.deducer.widgets.param.RFunction;
import org.rosuda.deducer.widgets.param.RFunctionDialog;

public class PointsElementModel extends ElementModel {

	RFunction rf;
	
	

	public PointsElementModel(){
		init();
	}
	
	public void init(){
		rf = new RFunction("plot");
		ParamRObject o = new ParamRObject("x");
		o.setTitle("Spatial points");
		o.setRObjectClass("SpatialPointsDataFrame");
		o.setRequired(true);
		rf.add(o);
		
		ParamLogical pl = new ParamLogical("add");
		pl.setDefaultValue(false);
		pl.setValue(true);
		pl.setViewType(Param.VIEW_HIDDEN);
		rf.add(pl);
		
		ParamNumeric pn = new ParamNumeric("pch");
		pn.setTitle("Point Type ID");
		pn.setLowerBound(.5);
		pn.setUpperBound(25.5);
		pn.setOptions(new String[]{"1.0","2.0","3.0","4.0","5.0","6.0","7.0","8.0","9.0","10.0","11.0","12.0","13.0","14.0","15.0","16.0","17.0",
			"18.0","19.0","20.0"
		});
		
		pn.setLabels(new String[]{"open circle","open triangle","plus","cross","open diamond",
				"open triangle (rev)","cross square","star","plus diamond",
				"plus circle","double hourglass","plus square",
				"target","triange square","solid square","solid circle","solid triangle",
				"solid diamond","solid circle (big)","solid circle (small)"
			});
		pn.setValue(1.0);
		pn.setViewType(Param.VIEW_COMBO);
		pn.setRequired(false);
		rf.add(pn);
		
		pn = new ParamNumeric("cex");
		pn.setTitle("Point Size");
		pn.setDefaultValue(1);
		pn.setLowerBound(0);
		pn.setValue(1.0);
		rf.add(pn);
		
		ParamColor pc = new ParamColor("col");
		pc.setTitle("Color");
		pc.setRequired(false);
		rf.add(pc);
		
		iconLocation = "icons/geo_point.png";
		name = "Points";
	}
	
	public JDialog getView() {
		RFunctionDialog rfd = new RFunctionDialog(rf);
		rfd.setSize(300, 300);
		rfd.setRun(false);
		return rfd;
	}

	public String getCall() {
		return rf.getCall();
	}

	public String checkValid() {
		return rf.checkValid();
	}

	public PointsElementModel clone(){
		PointsElementModel newM = new PointsElementModel();
		newM.rf = (RFunction) rf.clone();
		return newM;
	}

}
