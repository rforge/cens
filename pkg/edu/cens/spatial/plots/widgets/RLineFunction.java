package edu.cens.spatial.plots.widgets;

import org.rosuda.deducer.widgets.VariableSelectorWidget;
import org.rosuda.deducer.widgets.param.Param;
import org.rosuda.deducer.widgets.param.ParamWidget;
import org.rosuda.deducer.widgets.param.RFunction;

public class RLineFunction extends RFunction{
	
	public RLineFunction(String s){
		super(s);
	}
	public RLineFunction(){
		super();
	}
	public ParamWidget getView(){
		RSpatialFunctionView v = new RSpatialFunctionView(this,"SpatialLinesDataFrame");
		return v;
	}
	public ParamWidget getView(VariableSelectorWidget sel){
		RSpatialFunctionView v = new RSpatialFunctionView(this,sel,"SpatialLinesDataFrame");
		return v;
	}
	
	public Object clone(){
		RLineFunction s = new RLineFunction();
		for(int i=0;i<params.size();i++)
			s.params.add(((Param)params.get(i)).clone());
		s.name = name;
		s.view = view;
		s.requiresVariableSelector = this.requiresVariableSelector;
		s.required = required;
		return s;
	}
	

}
