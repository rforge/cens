package edu.cens.spatial.plots;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.rosuda.JGR.layout.AnchorConstraint;
import org.rosuda.JGR.layout.AnchorLayout;
import org.rosuda.deducer.Deducer;
import org.rosuda.javaGD.PlotPanel;

public class ViewPanel extends JPanel{
	JLayeredPane pane = new JLayeredPane();
	JMapViewer map;
	JPanel plotPanel;
	
	
	public ViewPanel(int w,int h){
		super();
		this.setLayout(new BorderLayout());
		this.add(pane);
		initMap();
		plotPanel = new PlotPanel(w,h);
		plotPanel.setOpaque(false);
		plotPanel.setBackground(null);
		pane.setLayer(plotPanel, 2);
	}
	
	public ViewPanel(PlotPanel p){
		super();
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
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS), 0);	
		initMap();
		this.add(pane);
		this.validate();
		this.repaint();
	}
	
    public void initMap(){
        map = new JMapViewer();

        map.setTileSource(new OsmTileSource.Mapnik());

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
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS), 1);
    }
}
