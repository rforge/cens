package edu.cens.spatial.plots;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PlottingElement implements Transferable{
	
	protected static DataFlavor flavor = new DataFlavor(PlottingElement.class,"Plot element");
	
	private ElementModel model;
	
	
	private ImageIcon icon;
	
	
	
	public static DataFlavor DATAFLAVOR = new DataFlavor(PlottingElement.class,"Plotting element");
	
	public PlottingElement(ElementModel mod){
		model = mod;
		setIconFromUrl(mod.getIconUrl());
	}
	
	
	public void setIconFromUrl(String url){
		URL loc = getClass().getResource(url);
		if(loc!=null){
			icon = new ImageIcon(loc);
		}
	}
	
	public Object clone(){
		PlottingElement p = new PlottingElement((ElementModel) this.model.clone());
		return p;
	}

	public JDialog getJDialog(){
		return model.getView();
	}
	
	public Image getImage(){
		return icon.getImage();
	}
	
	public void setImage(Image i){
		icon = new ImageIcon(i);
	}
	
	public ElementModel getModel(){
		return model;
	}
	
	public void setModel(ElementModel m){
		model = m;
		setIconFromUrl(m.getIconUrl());
	}
	

	public boolean isActive(){return model.isActive();}
	public void setActive(boolean act){
		model.setActive(act);
	}
	
	public JPanel makeComponent(){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel iconLabel;
		if(isActive())
			iconLabel = new JLabel(icon);
		else{
			URL url = getClass().getResource("/icons/edit_remove_32.png");
			iconLabel = new JLabel(new ImageIcon(url));
		}
		iconLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		panel.add(iconLabel);
		String[] s = model.getName().split("_");
		JLabel label;
		for(int i=0;i<s.length;i++){
			label = new JLabel(s[i]);
			label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			label.setFont(new Font("Dialog", Font.PLAIN, 12) );
			panel.add(label);			
		}
		if(!isActive())
		panel.setPreferredSize(new Dimension(80,70));
		panel.setBorder(new EtchedBorder());		
		return panel;
	}
	
	public static DataFlavor getFlavor(){
		return flavor;
	}
	

	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		return this;
	}

	public DataFlavor[] getTransferDataFlavors() {
			DataFlavor[] f = new DataFlavor[] {flavor};
		return f;
	}

	public boolean isDataFlavorSupported(DataFlavor arg0) {
		return true;
	}
	
}

