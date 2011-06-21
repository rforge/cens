package edu.cens.spatial.plots;

import javax.swing.*;
import java.util.*;

/**
* Created by IntelliJ IDEA.
* User: Neal
* Date: 1/10/11
* Time: 8:56 PM
*/

public class SpatialPlotModel extends AbstractListModel  {

    private final List<PlottingElement> _components = new ArrayList<PlottingElement>();


    // Options
    private String _title = null;
    private double[][] _boundingBox = {{Double.NaN, Double.NaN}, {Double.NaN, Double.NaN}};
    private boolean _axes =false;

    public enum MapType{None, Mobile, Satellite, Hybrid};
    private MapType _maptype = MapType.None;


    public SpatialPlotModel() {}

    private SpatialPlotModel(Collection<PlottingElement> spc, String title, double[][] boundingBox ) {
        _components.addAll(spc);
        _title = title;
        _boundingBox = new double[][]{ boundingBox[0].clone(), boundingBox[1].clone()  }; //hacky deep copy
    }

    public String getCall() {
        StringBuilder sb = new StringBuilder();
        for(PlottingElement spc : _components)
            if(spc.isActive())
                sb.append("\n").append(spc.getModel().getCall()).append("");
        return sb.toString();
    }

    public SpatialPlotModel clone() {
        return new SpatialPlotModel(_components, _title, _boundingBox);
    }

    //********************
    // Component Methods
    //********************

    public boolean add(PlottingElement spatialPlotComponent) {
        boolean add = _components.add(spatialPlotComponent);
        fireContentsChanged(this, getSize(), getSize());
        return add;
    }

    public void insertElementAt(PlottingElement component, int index) {
        _components.add(index, component);
        fireContentsChanged(this, index, index);
    }

    public PlottingElement remove(int index) {
    	PlottingElement remove = _components.remove(index);
        fireIntervalRemoved(this, index, index);
        return remove;
    }

    public boolean validate() {
        return true;  //todo
    }


    public int getSize() {
        return _components.size();
    }

    public PlottingElement getElementAt(int index) {
        return _components.get(index);
    }
    

}
