package edu.cens.spatial.plots;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.rosuda.JGR.layout.AnchorConstraint;

public class MapPanel extends JMapViewer{

	JLayeredPane pane;
	
	Rectangle2D.Double subsetRectangle = null;
	
	public MapPanel(JLayeredPane pa){
		super();
		pane = pa;
		setDisplayPositionByLatLon(34.0522222, -118.2427778, 3);
		addControls();
		
	}
	
	protected void initializeZoomSlider() {
        zoomSlider = new JSlider(MIN_ZOOM, tileController.getTileSource().getMaxZoom());
        zoomSlider.setOrientation(JSlider.VERTICAL);
        zoomSlider.setBounds(10, 10, 30, 150);
        zoomSlider.setPreferredSize(new Dimension(30,150));
        zoomSlider.setOpaque(false);

        int size = 18;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("images/plus.png"));
            zoomInButton = new JButton(icon);
        } catch (Exception e) {
            zoomInButton = new JButton("+");
            zoomInButton.setFont(new Font("sansserif", Font.BOLD, 9));
            zoomInButton.setMargin(new Insets(0, 0, 0, 0));
        }
        zoomInButton.setActionCommand("in");
        zoomInButton.setPreferredSize(new Dimension(size,size));
        zoomInButton.setBounds(4, 155, size, size);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("images/minus.png"));
            zoomOutButton = new JButton(icon);
        } catch (Exception e) {
            zoomOutButton = new JButton("-");
            zoomOutButton.setFont(new Font("sansserif", Font.BOLD, 9));
            zoomOutButton.setMargin(new Insets(0, 0, 0, 0));
        }
        zoomOutButton.setActionCommand("out");
        zoomOutButton.setPreferredSize(new Dimension(size,size));
        zoomOutButton.setBounds(8 + size, 155, size, size);
    }
	
	public int getZoomSliderLevel(){
		return zoomSlider.getValue();
	}
	
	public void addControls(){
		pane.add(zoomSlider, new AnchorConstraint(10, 0, 0, 10,
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE,
                AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS), 0);	
		pane.setLayer(zoomSlider, 3);
		pane.add(zoomInButton, new AnchorConstraint(160, 0, 0, 4,
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE,
                AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS), 0);
		pane.setLayer(zoomInButton, 3);
		pane.add(zoomOutButton, new AnchorConstraint(160, 0, 0, 26,
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE,
                AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS), 0);
		pane.setLayer(zoomOutButton, 3);
	}
	
	public void addPlusListener(ActionListener al){
		zoomInButton.addActionListener(al);
	}
	public void addMinusListener(ActionListener al){
		zoomOutButton.addActionListener(al);
	}
	
	public void addSliderListener(ChangeListener cl){
		zoomSlider.addChangeListener(cl);
	}
	

	//Let's try doing some of our drawing in java.
	public void paint(Graphics g)
	{
		super.paint(g);
		
		if (subsetRectangle != null)
		{
			Graphics2D g2 = (Graphics2D) g; 
			g2.draw(subsetRectangle);
		}
	}

	//public void drawSubsetRectangle(Point corner1, Point corner2)
	public void drawSubsetRectangle(Coordinate corner1, Coordinate corner2, boolean scrollIfClickedOutside)
	//public void drawSubsetRectangle(Coordinate corner1, Point corner2)
	{
//		double minLat = Math.min(corner1.getLat(), corner2.getLat());
//		double minLong = Math.min(corner1.getLon(), corner2.getLon());
//		
//		double maxLat = Math.max(corner1.getLat(), corner2.getLat());
//		double maxLong = Math.max(corner1.getLon(), corner2.getLon());
		
		
		Point activePt = //corner2; 
			getMapPosition(corner2, false); //The one the user drags
		
		int maxScroll = 10;
		
		if (scrollIfClickedOutside)
			{
			if(activePt.x <= 0)
			{
				int disp = (activePt.x - 0) ;
				disp = Math.max(disp, -maxScroll);
				moveMap(disp, 0);
			} 
			else if(activePt.x >= getWidth() - 0)
			{
				int disp = -(getWidth() - activePt.x);
				disp = Math.min(disp, maxScroll);
				moveMap(disp, 0);
			}
		}
		
		Point p1 = getMapPosition(corner1, false);
		Point p2 = //corner2; 
			getMapPosition(corner2, false);
		//p2.x += activeDisp.x;
		
		double x1 = Math.min(p1.x, p2.x);
		double y1 = Math.min(p1.y, p2.y);
		
		double x2 = Math.max(p1.x, p2.x);
		double y2 = Math.max(p1.y, p2.y);
		
		Point minPt = new Point((int) x1, (int)  y1);
		Point maxPt = new Point((int) x2, (int)  y2);
		
//		double x1 = Math.min(corner1.x, corner2.x);
//		double y1 = Math.min(corner1.y, corner2.y);
//		
//		double x2 = Math.max(corner1.x, corner2.x);
//		double y2 = Math.max(corner1.y, corner2.y);
		
//		Point clickPt1 = new Point((int)x1, (int)y1);
//		Point clickPt2 = new Point((int)x2, (int)y2);
//		
//		Coordinate latlon = getPosition(clickPt1);
//		
//		clickPt1 = getMapPosition(latlon);
		
		subsetRectangle = new Rectangle2D.Double(minPt.x, minPt.y, maxPt.x - minPt.x, maxPt.y - minPt.y);
		
			//new Rectangle2D.Double(clickPt1.x, clickPt1.y, 5 + x2 - x1,  5 + y2 - y1);
		repaint();
	}
	
	public void clearSubsetRectangle()
	{
		subsetRectangle = null;
	}

	
	
}
