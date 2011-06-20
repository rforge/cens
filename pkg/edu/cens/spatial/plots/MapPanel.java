package edu.cens.spatial.plots;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.rosuda.JGR.layout.AnchorConstraint;

public class MapPanel extends JMapViewer{

	JLayeredPane pane;
	
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

	
}
