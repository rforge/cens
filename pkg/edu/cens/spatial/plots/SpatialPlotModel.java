package edu.cens.spatial.plots;

import javax.swing.*;

import org.rosuda.deducer.Deducer;

import java.awt.Toolkit;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: Neal Date: 1/10/11 Time: 8:56 PM
 */

public class SpatialPlotModel extends AbstractListModel
{

	private final List<PlottingElement>	_components		= new ArrayList<PlottingElement>();

	// Options
	private double[][]									_boundingBox	= {
			{ Double.NaN, Double.NaN }, { Double.NaN, Double.NaN } };

	private String											_maptype			= "osm";

	public SpatialPlotModel()
	{
	}

	private SpatialPlotModel(Collection<PlottingElement> spc,
			double[][] boundingBox)
	{
		_components.addAll(spc);
		_boundingBox = new double[][] { boundingBox[0].clone(),
				boundingBox[1].clone() }; // hacky deep copy
	}

	public String getCall()
	{
		StringBuilder sb = new StringBuilder();
		for (PlottingElement spc : _components)
		{
			if (spc.isActive())
			{
				sb.append("\n").append(spc.getModel().getCall()).append("");
			}
		}
		return sb.toString();
	}

	public SpatialPlotModel clone()
	{
		SpatialPlotModel spm = new SpatialPlotModel(_components, _boundingBox);
		spm._maptype = _maptype;
		return spm;
	}

	// ********************
	// Component Methods
	// ********************

	public boolean add(PlottingElement spatialPlotComponent)
	{
		boolean add = _components.add(spatialPlotComponent);
		fireContentsChanged(this, getSize(), getSize());
		return add;
	}

	public void insertElementAt(PlottingElement component, int index)
	{
		_components.add(index, component);
		fireContentsChanged(this, index, index);
	}

	public PlottingElement remove(int index)
	{
		PlottingElement remove = _components.remove(index);
		fireIntervalRemoved(this, index, index);
		return remove;
	}

	public boolean validate()
	{
		return true; // todo
	}

	public int getSize()
	{
		return _components.size();
	}

	public PlottingElement getElementAt(int index)
	{
		return _components.get(index);
	}
	
	//TODO subset text, and choropleth
	public boolean executeSubsetting(double minLat, double minLon, double maxLat, double maxLon, boolean keepSelected, String subsetName)
	{
		//Want to make sure only 1 set of data gets modified at a time
		
		//Options:
		//	1: force them to hide all but 1 item.
		//	2: Ignore items which are in no way contained or completely contained
		//		- Deleting: Raise error if more than 1 set has deletions
		//		- Masking: Ignore datasets for which nothing is selected, raised error
		//		if rect contains more than one dataset.
		
		boolean successful = false;
		
		int nActiveElements = 0;
		for (PlottingElement spc : _components)	
		{
			if (spc.isActive())
			{
				nActiveElements++;
			}
		}
		
			if (nActiveElements == 0)
			{
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(null,
					    "You have nothing to subset!",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				return false;
			}
			else if (nActiveElements > 1)
		{
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null,
				    "You may only subset 1 dataset at a time!" +
				    "\nPlease hide all but one dataset using the controls to the right.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		//TODO necessary to check name's uniqueness?
		String subsettedCopy = subsetName;//Deducer.getUniqueName("subsettedCopy");
		
		PlottingElement changedPlottingElement = null;
		
	//PlottingElement spc = null;
		//boolean noOverlap = true;
		//for (ListIterator<PlottingElement> i = _components.listIterator(); i.hasNext() && noOverlap; spc = i.next())
		for (PlottingElement spc : _components)	
		{
			if (spc.isActive())
			{
				//Back up all the subsetted data 
				//Deducer.execute(cmd);
				
				String varName = ( (ElementModel) spc.getModel() ).getDataFrameArgumentName();
				String subsetFunction = null;
				
				if (spc.getModel() instanceof PolyElementModel
						||
						spc.getModel() instanceof ChoroElementModel
						)
				{
					subsetFunction = ".subsetPoly";
				}
				else if (spc.getModel() instanceof LinesElementModel)
				{
					subsetFunction = ".subsetLines";
				}
				else if (
						spc.getModel() instanceof PointsElementModel
						||
						spc.getModel() instanceof ColoredPointsElementModel
						||
						spc.getModel() instanceof BubbleElementModel
						)
				{
					subsetFunction = ".subsetPoints";
				}
				else
				{
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null,
						    "Subsetting is not currently supported for plots of type \""+spc.getModel().getClass()+"\".",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
				if (varName != null)
				{
//					String backupVar = varName;
//					if (! backupVar.contains("_pre_subset"))
//					{
//						backupVar = backupVar + "_pre_subset";
//					}
//
//					backupVar = Deducer.getUniqueName(backupVar);
//					
//					Deducer.execute(backupVar + " <- " + varName);
				
					String subsetCommand = 
						subsettedCopy + " <- " + varName + "\n" +
						subsettedCopy + " <- " +
						subsetFunction +"(" + 
						minLat  + "," + 
						minLon  + "," + 
						maxLat  + "," + 
						maxLon  + "," + 
						varName + "," +
						"removeSelection=" + (""+keepSelected).toUpperCase() + ")";
					
					String printableSubsetCommand = subsetCommand;
					printableSubsetCommand.replace("\n", "\\n");
					
					//System.out.println("the string:\n"+subsetCommand+"\n==============\n");
					
					System.out.println();
					
					//output the subsetting code to the console, since eval won't,
					//and we need the blocking call only available in eval.
					//System.out.println could work too
					Deducer.timedEval("cat(\""+printableSubsetCommand+"\\n\")");
					
					Deducer.timedEval(subsetCommand);
					
					if (Deducer.timedEval(subsettedCopy).isNull())
					{
						//Deducer.execute(varName + " <- " + backupVar);
						//Deducer.eval("print('"+ varName + " ignored')" + "\n" + "rm(" + subsettedCopy + ")");
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null,
							    "Your subset contains no elements! reverting to original plot.+\n" +
							    "If you want to remove this component from the plot, you must use the controls at right.",
							    "Warning",
							    JOptionPane.WARNING_MESSAGE);
						successful = false;
					}
					else
					{
						changedPlottingElement = spc;
						//the below checks to see if anything was actually deleted
						//Deducer.eval("if (length(" + varName + ") == length("+ subsettedCopy + ")) { rm(" + subsettedCopy + ") }");
						successful = true;
					}
				}
			}
		}
		if (successful)
		{
			( (ElementModel) changedPlottingElement.getModel() ).setDataFrameArgumentName(subsetName);
		}
		return successful;
	}

}
