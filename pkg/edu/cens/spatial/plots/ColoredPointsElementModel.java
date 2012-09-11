package edu.cens.spatial.plots;

import javax.swing.JDialog;

import org.rosuda.deducer.Deducer;
import org.rosuda.deducer.widgets.param.Param;
import org.rosuda.deducer.widgets.param.ParamCharacter;
import org.rosuda.deducer.widgets.param.ParamLogical;
import org.rosuda.deducer.widgets.param.ParamNumeric;
import org.rosuda.deducer.widgets.param.ParamRObject;
import org.rosuda.deducer.widgets.param.ParamVariable;
import org.rosuda.deducer.widgets.param.RFunction;
import org.rosuda.deducer.widgets.param.RFunctionDialog;

import edu.cens.spatial.DeducerSpatial;
import edu.cens.spatial.plots.widgets.ParamSpatialVariable;
import edu.cens.spatial.plots.widgets.RPointFunction;
import edu.cens.spatial.plots.widgets.RSpatialFunctionView;

public class ColoredPointsElementModel extends ElementModel{
	RFunction rf;

	public ColoredPointsElementModel(){
		init();
	}
	
	public void init(){
		rf = new RPointFunction("spatialColoredPoints");
		
		//can't get this param
		//so added this code:
		//==========================================
//		ParamRObject o = new ParamRObject("x");
//		o.setRObjectClass("SpatialPointsDataFrame");
//		o.setRequired(true);
//		rf.add(o);
		//======= End new code =====================
		
		rf.setViewType(null);
		ParamSpatialVariable pv = new ParamSpatialVariable("color_var");
		pv.setFormat(ParamSpatialVariable.FORMAT_WITH_DATA_CHARACTER);
		pv.setTitle("Color");
		rf.add(pv);
		
		ParamNumeric pn = new ParamNumeric("pch");
		pn.setTitle("Type");
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
		pn.setValue(16.0);
		pn.setViewType(Param.VIEW_COMBO);
		pn.setRequired(false);
		rf.add(pn);
		
		
		pn = new ParamNumeric("cex");
		pn.setTitle("Size");
		pn.setDefaultValue(1);
		pn.setLowerBound(0);
		pn.setValue(1.0);
		rf.add(pn);
		
		rf.add(DeducerSpatial.makeColorScaleParam());
				
		ParamCharacter pc = new ParamCharacter("legend.loc");
		pc.setTitle("Legend location");
		pc.setOptions(new String[]{"bottomleft","bottomright","topleft","topright"
			});
		pc.setViewType(Param.VIEW_COMBO);
		pc.setDefaultValue("bottomleft");
		pc.setValue("bottomleft");
		rf.add(pc);
		

		pc = new ParamCharacter("legend.title");
		pc.setTitle("Legend title");
		rf.add(pc);
		
		iconLocation = "icons/geo_colored_point.png";
		name = "Colored";
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

	public ColoredPointsElementModel clone(){
		ColoredPointsElementModel newM = new ColoredPointsElementModel();
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
