package edu.cens.spatial.plots;

import javax.swing.JDialog;

import org.rosuda.deducer.widgets.param.Param;
import org.rosuda.deducer.widgets.param.ParamColor;
import org.rosuda.deducer.widgets.param.ParamLogical;
import org.rosuda.deducer.widgets.param.ParamNumeric;
import org.rosuda.deducer.widgets.param.ParamRObject;
import org.rosuda.deducer.widgets.param.RFunction;
import org.rosuda.deducer.widgets.param.RFunctionDialog;

public class PolyElementModel extends ElementModel{
	RFunction rf;
	
	

	public PolyElementModel(){
		init();
	}
	
	public void init(){
		rf = new RFunction("plot");
		ParamRObject o = new ParamRObject("x");
		o.setTitle("Spatial Polygons");
		o.setRObjectClass("SpatialPolygonsDataFrame");
		o.setRequired(true);
		rf.add(o);
		
		ParamLogical pl = new ParamLogical("add");
		pl.setDefaultValue(false);
		pl.setValue(true);
		pl.setViewType(Param.VIEW_HIDDEN);
		rf.add(pl);
		
		ParamNumeric pn = new ParamNumeric("lty");
		pn.setTitle("Type");
		pn.setLowerBound(.5);
		pn.setUpperBound(25.5);
		pn.setOptions(new String[]{"0.0","1.0","2.0","3.0","4.0","5.0","6.0"});
		
		pn.setLabels(new String[]{"blank","solid","dashed","dotted","dot dash","long dash","two dash"
			});
		pn.setValue(1.0);
		pn.setViewType(Param.VIEW_COMBO);
		pn.setRequired(false);
		rf.add(pn);
		
		pn = new ParamNumeric("lwd");
		pn.setTitle("Width");
		pn.setDefaultValue(1);
		pn.setLowerBound(0);
		pn.setValue(1.0);
		rf.add(pn);
		
		ParamColor pc = new ParamColor("border");
		pc.setTitle("Color");
		pc.setRequired(false);
		rf.add(pc);

		
		iconLocation = "icons/geo_poly.png";
		name = "Polygons";
	}
	
	public JDialog getView() {
		RFunctionDialog rfd = new RFunctionDialog(rf);
		rfd.setSize(300, 300);
		rfd.setRun(false);
		return rfd;
	}
	
	public String getDataFrameArgumentName()
	{
		return (String) rf.get(0).getValue();
	}
	
	public void setDataFrameArgumentName(String argName) 
	{
		rf.get(0).setValue(argName);
		//.get(0).getValue();
	}

	public String getCall() {
		return rf.getCall();
	}

	public String checkValid() {
		return rf.checkValid();
	}

	public PolyElementModel clone(){
		PolyElementModel newM = new PolyElementModel();
		newM.rf = (RFunction) rf.clone();
		return newM;
	}
}
