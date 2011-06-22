package edu.cens.spatial.plots;

import javax.swing.JDialog;

import org.rosuda.deducer.widgets.param.Param;
import org.rosuda.deducer.widgets.param.ParamCharacter;
import org.rosuda.deducer.widgets.param.ParamNumeric;
import org.rosuda.deducer.widgets.param.RFunction;
import org.rosuda.deducer.widgets.param.RFunctionDialog;

import edu.cens.spatial.plots.widgets.ParamSpatialVariable;
import edu.cens.spatial.plots.widgets.RPointFunction;
import edu.cens.spatial.plots.widgets.RPolyFunction;

public class ChoroElementModel extends ElementModel{
	RFunction rf;
	
	

	public ChoroElementModel(){
		init();
	}
	
	public void init(){
		rf = new RPolyFunction("choro_plot");
		rf.setViewType(null);
		ParamSpatialVariable pv = new ParamSpatialVariable("dem");
		pv.setFormat(ParamSpatialVariable.FORMAT_WITH_DATA);
		pv.setTitle("Color");
		rf.add(pv);
		
		ParamNumeric pn = new ParamNumeric("alpha");
		pn.setTitle("Alpha");
		pn.setDefaultValue(.5);
		pn.setLowerBound(0);
		pn.setUpperBound(1);
		pn.setValue(.5);
		rf.add(pn);
		
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
		
		iconLocation = "icons/geo_choropleth.png";
		name = "Choropleth";
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

	public ChoroElementModel clone(){
		ChoroElementModel newM = new ChoroElementModel();
		newM.rf = (RFunction) rf.clone();
		return newM;
	}
}
