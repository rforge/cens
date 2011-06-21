package edu.cens.spatial;

import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.rosuda.JGR.RController;
import org.rosuda.JGR.toolkit.FileSelector;
import org.rosuda.deducer.Deducer;

public class ShapeFileLoader extends FileSelector{
	
	private JTextField rDataNameField;
	JTextField proj ;
	public ShapeFileLoader(Frame f) {
		super(f, "Load shape file", FileSelector.LOAD, null, true);
		JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		namePanel.add(new JLabel("Set name: "));
		rDataNameField = new JTextField(12);
		namePanel.add(rDataNameField);
		namePanel.add(new JLabel("      Proj: "));

		proj = new JTextField(10);
		proj.setText("+proj=longlat");
		namePanel.add(proj);
		this.addFooterPanel(namePanel);
	}
	
	public ShapeFileLoader() {
		this(null);
	}
	
	public boolean runInR(){
		if (this.getFile() == null)
			return false;
		String rName = rDataNameField.getText();
		if (rName.length() == 0)
			rName = (this.getFile().indexOf(".") <= 0 ? Deducer.getUniqueName(this.getFile()) : 
					Deducer.getUniqueName(this.getFile().substring(0, this.getFile().indexOf("."))));
		rName = RController.makeValidVariableName(rName);
		String path =(this.getDirectory() + Deducer.addSlashes(this.getFile())).replace('\\', '/');
		if(!path.toLowerCase().endsWith(".shp") &&
				!path.toLowerCase().endsWith(".shp") &&
				!path.toLowerCase().endsWith(".dbf") &&
				!path.toLowerCase().endsWith(".prj") &&
				!path.toLowerCase().endsWith(".sbn") &&
				!path.toLowerCase().endsWith(".sbx")){
			JOptionPane.showMessageDialog(this, "This does not appear to be a shape file.\nAcceptable extensions are: .shp,.dbf,.prj,.sbx,.sbn");
			return false;
		}
		path = path.substring(0, path.length()-4);
		String command =rName +" <- readShapeSpatial(\""+path+"\", proj=CRS('"+proj.getText()+"'))\n";
		command += rName +" <- spTransform("+rName+",osm())";
		Deducer.execute(command);
		
		return true;
	}
	
}
