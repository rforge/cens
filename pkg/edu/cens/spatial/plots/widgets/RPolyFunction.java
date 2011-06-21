package edu.cens.spatial.plots.widgets;

import org.rosuda.deducer.widgets.VariableSelectorWidget;
import org.rosuda.deducer.widgets.param.Param;
import org.rosuda.deducer.widgets.param.ParamWidget;
import org.rosuda.deducer.widgets.param.RFunction;

public class RPolyFunction extends RFunction{
	
	public RPolyFunction(String s){
		super(s);
	}
	public RPolyFunction(){
		super();
	}
	public ParamWidget getView(){
		RSpatialFunctionView v = new RSpatialFunctionView(this,"SpatialPolygonsDataFrame");
		return v;
	}
	public ParamWidget getView(VariableSelectorWidget sel){
		RSpatialFunctionView v = new RSpatialFunctionView(this,sel,"SpatialPolygonsDataFrame");
		return v;
	}
	
	public Object clone(){
		RPolyFunction s = new RPolyFunction();
		for(int i=0;i<params.size();i++)
			s.params.add(((Param)params.get(i)).clone());
		s.name = name;
		s.view = view;
		s.requiresVariableSelector = this.requiresVariableSelector;
		s.required = required;
		return s;
	}
	

}
