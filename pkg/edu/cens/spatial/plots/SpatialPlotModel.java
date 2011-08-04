package edu.cens.spatial.plots;

import javax.swing.*;

import org.rosuda.deducer.Deducer;

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
	
	public void executeSubsetting(double minLat, double minLon, double maxLat, double maxLon)
	{
		for (PlottingElement spc : _components)
		{
			if (spc.isActive())
			{
				if (spc.getModel() instanceof PolyElementModel)
				{
					String polyVarName = ( (PolyElementModel) spc.getModel() ).getParamX();
					System.out.println( polyVarName );
					
					//currently, we'll be a little destructive.
					Deducer.execute(polyVarName + " <- " +
							".subsetPoly(" + 
							minLat  + "," + 
							minLon  + "," + 
							maxLat  + "," + 
							maxLon  + "," + 
							polyVarName + ")");
					
				}
			}
		}
	}

}
