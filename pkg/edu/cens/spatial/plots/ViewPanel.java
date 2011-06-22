package edu.cens.spatial.plots;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.rosuda.JGR.layout.AnchorConstraint;
import org.rosuda.JGR.layout.AnchorLayout;
import org.rosuda.REngine.REXP;
import org.rosuda.deducer.Deducer;
import org.rosuda.javaGD.PlotPanel;

public class ViewPanel extends JPanel{
	JLayeredPane pane = new JLayeredPane();
	MapPanel map;
	PlotPanel plotPanel;
	String tp = "osm";
	SpatialPlotBuilder parent;
	
	public ViewPanel(int w,int h,SpatialPlotBuilder par){
		super();
		parent = par;
		this.setLayout(new BorderLayout());
		this.add(pane);
		initMap();
		plotPanel = new PlotPanel(w,h);
		plotPanel.setOpaque(false);
		plotPanel.setBackground(null);
	}
	
	public void refreshPlot(){
		plotPanel.initRefresh();
	}
	
	public ViewPanel(PlotPanel p, SpatialPlotBuilder par){
		super();
		parent=par;
		this.setLayout(new BorderLayout());
		pane.setLayout(new AnchorLayout());		
		plotPanel = p;
		plotPanel.setOpaque(false);
		plotPanel.setBackground(new Color(255,0,0,0));
		
		JPanel tmp = new JPanel();
		tmp.setOpaque(false);
		tmp.setBackground(new Color(255,0,0,0));
		tmp.add(new JLabel("blaa blaa"));
		pane.add(plotPanel, new AnchorConstraint(0, 0, 0, 0,
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS,
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS), 1);	
		pane.setLayer(plotPanel, 1);
		initMap();
		this.add(pane);
		this.validate();
		this.repaint();
	}
	
    public void initMap(){
        map = new MapPanel(pane);
        map.setTileSource(new OsmTileSource.Mapnik());
        MapController mapContr = new MapController(map,parent);
        map.addPlusListener(mapContr);
        map.addMinusListener(mapContr);
        map.addSliderListener(mapContr);
        mapContr.addListenersTo(plotPanel);
        try {
			map.setTileLoader(new OsmFileCacheTileLoader(map));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        map.setMapMarkerVisible(false);
        map.setZoomContolsVisible(true);
        pane.add(map,new AnchorConstraint(0, 0, 0, 0,
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS,
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS), 0);
        pane.setLayer(map, 0);

    }
    
    public void printBBox(){
        Coordinate c = map.getPosition(0,0);
        System.out.println(c.getLat() + "  " + c.getLon());
        Dimension d = this.getSize();
        c = map.getPosition(0+getWidth(),0+getHeight());
        System.out.println(c.getLat() + "  " + c.getLon());
    }
    
    public Vector<Double> getUpperLeftCoordinate(){
    	Vector<Double> d = new Vector<Double>(2);
    	Coordinate c = map.getPosition(0,0);
    	if(c.getLon()>180.0 || c.getLon()< -180.0 || c.getLat()>85.0 || c.getLat()< -85.0){
    		d.add(0.0);
    		d.add(0.0);
    		return d;
    	}
    	String cmd = "project_mercator("+c.getLat()+","+c.getLon()+")";
    	//System.out.println(cmd);
    	REXP ul = Deducer.eval(cmd);
    	try{
    		double[] vals = ul.asDoubles();
    		d.add(vals[0]);
    		d.add(vals[1]);
    	}catch(Exception e){e.printStackTrace();}
    	return d;
    }

    public Vector<Double> getLowerRightCoordinate(){
    	Vector<Double> d = new Vector<Double>(2);
    	Coordinate c = map.getPosition(0+plotPanel.getWidth(),0+plotPanel.getHeight());
    	if(c.getLon()>180.0 || c.getLon()< -180.0 || c.getLat()>85.0 || c.getLat()< -85.0){
    		d.add(0.0);
    		d.add(0.0);
    		return d;
    	}
    	String cmd = "project_mercator("+c.getLat()+","+c.getLon()+")";
    	//System.out.println(cmd);
    	REXP ul = Deducer.eval(cmd);
    	try{
    		double[] vals = ul.asDoubles();
    		d.add(vals[0]);
    		d.add(vals[1]);
    	}catch(Exception e){e.printStackTrace();}
    	return d;
    }
    
    public Vector<Double> getLowerRightLatLong(){
    	Coordinate c = map.getPosition(0+plotPanel.getWidth(),0+plotPanel.getHeight());
    	Vector<Double> d = new Vector<Double>(2);
    	d.add(c.getLat());
    	d.add(c.getLon());
    	return d;
    }
    public Vector<Double> getUpperLeftLatLong(){
    	Coordinate c = map.getPosition(0,0);
    	Vector<Double> d = new Vector<Double>(2);
    	d.add(c.getLat());
    	d.add(c.getLon());
    	return d;
    }    
    public int getZoom(){
    	return map.getZoom();
    }
    public void setTileSource(String type){
    	if("bing".equals(type)){
    		tp = "bing";
    		map.setTileSource(new BingAerialTileSource());
    	}else if("osm".equals(type)){
    		tp="osm";
    		map.setTileSource(new OsmTileSource.Mapnik());
    	}
    }
    public String getTileSourceType(){
    	return tp;
    }
    
}
