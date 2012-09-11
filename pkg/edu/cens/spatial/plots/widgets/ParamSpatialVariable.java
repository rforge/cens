package edu.cens.spatial.plots.widgets;

import org.rosuda.deducer.widgets.param.Param;
import org.rosuda.deducer.widgets.param.ParamVariable;

public class ParamSpatialVariable extends ParamVariable{

	public static String FORMAT_WITH_DATA = "wd";
	public static String FORMAT_WITH_DATA_CHARACTER = "wdc";
	public static String FORMAT_WITHOUT_DATA = "wod";
	
	public ParamSpatialVariable(){
		view = Param.VIEW_SINGLE_VARIABLE;
		format = FORMAT_WITHOUT_DATA;
	}
	
	public ParamSpatialVariable(String name){
		this.name = name;
		this.title = name;
		view = Param.VIEW_SINGLE_VARIABLE;
		format = FORMAT_WITHOUT_DATA;
	}
	
	
	public String[] getParamCalls() {
		String[] calls = new String[]{};
		if(getValue()!=null && !getValue().equals(getDefaultValue())){
			if(format.equals(FORMAT_WITH_DATA)){
			calls = new String[] {data,
					(name!=null ? (name + " = ") : "") +data +"@data[,'"+variable+"']"};
			}else if(format.equals(FORMAT_WITH_DATA_CHARACTER)){
				calls = new String[] {data,"'"+variable+"'"};				
			}else {
				calls = new String[] {(name!=null ? (name + " = ") : "") +data +"@data[,'"+variable+"']"};				
			}
		}
		return calls;
	}
	
	public Object clone() {
		ParamSpatialVariable p = new ParamSpatialVariable();
		p.setName(this.getName());
		p.setTitle(this.getTitle());
		p.setViewType(this.getViewType());
		p.format = format;
		p.data = this.data;
		p.variable = this.variable;
		p.defaultData = this.defaultData;
		p.defaultVariable = this.defaultVariable;
		p.view = this.view;
		p.required = required;
		return p;
	}
	
	
}
