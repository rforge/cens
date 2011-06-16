package edu.cens.spatial.plots;

import javax.swing.*;
import java.util.*;

/**
* Created by IntelliJ IDEA.
* User: Neal
* Date: 1/10/11
* Time: 8:56 PM
*/

public class SpatialPlotModel extends AbstractListModel implements IPlotModel<SpatialPlotComponent> {

    private final List<SpatialPlotComponent> _components = new ArrayList<SpatialPlotComponent>();


    // Options
    private String _title = null;
    private double[][] _boundingBox = {{Double.NaN, Double.NaN}, {Double.NaN, Double.NaN}};
    private boolean _axes =false;

    public enum MapType{None, Mobile, Satellite, Hybrid};
    private MapType _maptype = MapType.None;


    public SpatialPlotModel() {}

    private SpatialPlotModel(Collection<SpatialPlotComponent> spc, String title, double[][] boundingBox ) {
        _components.addAll(spc);
        _title = title;
        _boundingBox = new double[][]{ boundingBox[0].clone(), boundingBox[1].clone()  }; //hacky deep copy
    }

    public String getCall() {
        StringBuilder sb = new StringBuilder();
        sb.append("#### Begin Plot;\n{ function() {\n\n");

        List<String> lats = new ArrayList<String>();
        List<String> lons = new ArrayList<String>();

        for(SpatialPlotComponent spc : _components)
        {
            Map<String,String> args = spc.getArgs();
            String lat = args.get("lat");
            String lon = args.get("lon");
            String dat = args.get("dat");
            if(lat!= null && lon != null)
            {
                if(dat != null)
                {
                    lat = dat + "$" + lat;
                    lon = dat + "$" + lon;
                }
                lats.add(lat);
                lons.add(lon);
            }
        }

        if(!lats.isEmpty() && !lons.isEmpty()) {
            assert lats.size() == lons.size();
            String latCat="lat <- c(";
            String lonCat="lon <- c(";

            // Java has no join :(
            for(int i = 0; i < lats.size(); i++)
            {
                boolean last = i+1 == lats.size();
                latCat += lats.get(i) +  (last?"":",")      ;
                lonCat += lons.get(i) +  (last?"":",")      ;                
            }


            latCat += ");\n";
            lonCat += ");\n";
            
            sb.append(latCat);
            sb.append(lonCat);
        }
        else {
            //Initialize boundingbox to usa
            sb.append("lat <- c(38,40);\nlon<- c(-96,-94);\n");

        }


        sb.append("bb <- qbbox(lat,lon, TYPE = \"all\", margin = list(m=rep(5,4), TYPE = c(\"perc\", \"abs\")[1]));\n");




        // user specified bounding box
        if(!Double.isNaN(_boundingBox[0][0] )) sb.append("bb$lonR[1] <- "+ _boundingBox[0][0] + ";\n");
        if(!Double.isNaN(_boundingBox[0][1] )) sb.append("bb$lonR[2] <- "+ _boundingBox[0][1] + ";\n");
        if(!Double.isNaN(_boundingBox[1][0] )) sb.append("bb$latR[1] <- "+ _boundingBox[1][0] + ";\n");
        if(!Double.isNaN(_boundingBox[1][1] )) sb.append("bb$latR[2] <- "+ _boundingBox[1][1] + ";\n");

        if(_maptype != MapType.None)
            sb.append("map<-download_google_map(bb,maptype='"+ _maptype.name().toLowerCase() + "');\n")
              .append("bb<-map$qbbox;\n");

        // Make the window
        sb.append("frame();\n");
        sb.append("plot.window(bb$lonR, bb$latR, xaxt=\"n\", yaxt=\"n\", xlab=\"\", ylab=\"\");\n");

        // Other options
        if(_title != null) sb.append("title(main='"+_title+"');\n");

        if(_axes) sb.append("axis(1); axis(2);\n");

        if(_maptype != MapType.None)
            sb.append("plot_google_map2(map);\n");




        for(SpatialPlotComponent spc : _components)
            if(spc.isActive())
                sb.append("\n{\n").append(spc.getCall()).append("\n}\n");

        sb.append("\ninvisible();\n}}()\n#### End Plot;\n");
        return sb.toString();
    }

    public SpatialPlotModel clone() {
        return new SpatialPlotModel(_components, _title, _boundingBox);
    }

    //********************
    // Component Methods
    //********************

    public boolean add(SpatialPlotComponent spatialPlotComponent) {
        boolean add = _components.add(spatialPlotComponent);
        fireContentsChanged(this, getSize(), getSize());
        return add;
    }

    public void insertElementAt(SpatialPlotComponent component, int index) {
        _components.add(index, component);
        fireContentsChanged(this, index, index);
    }

    public SpatialPlotComponent remove(int index) {
        SpatialPlotComponent remove = _components.remove(index);
        fireIntervalRemoved(this, index, index);
        return remove;
    }

    public boolean validate() {
        return true;  //todo
    }


    public int getSize() {
        return _components.size();
    }

    public SpatialPlotComponent getElementAt(int index) {
        return _components.get(index);
    }
    

    //********************
    // Old option Methods
    //********************    


    public String getTitle() {
        return _title;
    }

    public void setTitle(String title)
    {
        _title = title == null || title.replaceAll("\\s","").length() == 0 ? null : title;
    }

    public double[][] getBoundingBox() {
        return _boundingBox;
    }

    public void setBoundingBox(String[][] bounds)
    {
        for(int i = 0; i < bounds.length; i++)
        for(int j = 0; j < bounds.length; j++)
            try {
                _boundingBox[i][j] = Double.parseDouble(bounds[i][j]);
            } catch (NumberFormatException nfe) {
                _boundingBox[i][j] = Double.NaN;
            }
    }

    public MapType getMapType() {
        return _maptype;
    }

    public void setMapType(MapType maptype) {
        _maptype = maptype;
    }

    public boolean isAxes() {
        return _axes;
    }

    public void setAxes(boolean axes) {
        _axes = axes;
    }
}
