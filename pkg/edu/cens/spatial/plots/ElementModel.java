package edu.cens.spatial.plots;

import java.util.Vector;

import javax.swing.JDialog;

import org.w3c.dom.Element;


public abstract class ElementModel {

	protected String iconLocation;
	protected String name;
	
	protected boolean active = true;
	
	public abstract String getCall();
	public abstract String checkValid();
	public abstract JDialog getView();
	public abstract Object clone();
	
	public String getIconUrl() {return iconLocation;}
	public String getName() {return name;}
	public boolean isActive(){return active;}
	public void setActive(boolean act){active=act;}
	
	//It turns out these all actually have the same body...
	//TODO make these inherited methods.
	public abstract String getDataFrameArgumentName();
	public abstract void setDataFrameArgumentName(String argName);
//	public void setDataFrameArgumentName(String argName)
//	{
//		throw new IllegalStateException("This method is yet undefined");
//	}
}
