package edu.cens.spatial;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.rosuda.JGR.JGR;
import org.rosuda.deducer.Deducer;
import org.rosuda.deducer.data.DataViewerController;
import org.rosuda.ibase.toolkit.EzMenuSwing;

import edu.cens.spatial.plots.ColoredPointsElementModel;
import edu.cens.spatial.plots.PointsElementModel;
import edu.cens.spatial.plots.SpatialPlotBuilder;

public class DeducerSpatial
{

	//protected static SpatialPlotBuilder spatialWindow = null; 
	
	public static ActionListener cListener = new SpatialMenuListener();

	public static void init()
	{
		try
		{
			if (Deducer.isJGR())
			{
				int menuIndex = 6;
				insertMenu(JGR.MAINRCONSOLE, "Spatial", menuIndex);
				EzMenuSwing.addJMenuItem(JGR.MAINRCONSOLE, "Spatial", "Load shape file", "shape", cListener);
				EzMenuSwing.addJMenuItem(JGR.MAINRCONSOLE, "Spatial","Convert data.frame", "conv_pnt", cListener);
				EzMenuSwing.addJMenuItem(JGR.MAINRCONSOLE, "Spatial", "Spatial plot builder", "builder", cListener);
			}
			DataViewerController.init();
			DataViewerController.addDataType("SpatialPointsDataFrame", "sp-p");
			DataViewerController.addTabFactory("SpatialPointsDataFrame",
					"Data View", new SpatialDataViewFactory());
			DataViewerController.addTabFactory("SpatialPointsDataFrame",
					"Variable View", new SpatialVariableViewFactory());
			DataViewerController.addTabFactory("SpatialPointsDataFrame",
					"Coordinates", new CoordViewFactory(false));

			DataViewerController.addDataType("SpatialPolygonsDataFrame",
					"sp-py");
			DataViewerController.addTabFactory("SpatialPolygonsDataFrame",
					"Data View", new SpatialDataViewFactory());
			DataViewerController.addTabFactory("SpatialPolygonsDataFrame",
					"Variable View", new SpatialVariableViewFactory());
			DataViewerController.addTabFactory("SpatialPolygonsDataFrame",
					"Centroids", new CoordViewFactory(false));

			DataViewerController.addDataType("SpatialLinesDataFrame", "sp-l");
			DataViewerController.addTabFactory("SpatialLinesDataFrame",
					"Data View", new SpatialDataViewFactory());
			DataViewerController.addTabFactory("SpatialLinesDataFrame",
					"Variable View", new SpatialVariableViewFactory());
			DataViewerController.addTabFactory("SpatialLinesDataFrame",
					"Paths", new CoordViewFactory(true));

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void insertMenu(JFrame f, String name, int index)
	{
		JMenuBar mb = f.getJMenuBar();
		JMenu m = EzMenuSwing.getMenu(f, name);
		if (m == null && index < mb.getMenuCount())
		{
			JMenuBar mb2 = new JMenuBar();
			int cnt = mb.getMenuCount();
			for (int i = 0; i < cnt; i++)
			{
				if (i == index)
				{
					mb2.add(new JMenu(name));
				}
				mb2.add(mb.getMenu(0));
			}
			f.setJMenuBar(mb2);
		}
		else if (m == null && index == mb.getMenuCount())
		{
			EzMenuSwing.addMenu(f, name);
		}
	}

	public static void main(String[] args)
	{
		final SpatialPlotBuilder b = new SpatialPlotBuilder();
		JPanel temp = new JPanel();
		temp.add(new JButton("subset") // the menu doesn't work so add a button.
		{
			{
				addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						b.beginSubsetting();
					}
				});
			}
		});
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(b.getContentPane(), BorderLayout.CENTER);
		f.add(temp,BorderLayout.SOUTH);
		//f.pack();
		f.setVisible(true);
		//b.setVisible(true);
	}
}

class SpatialMenuListener implements ActionListener
{

	@Override
	public void actionPerformed(ActionEvent act)
	{
		String cmd = act.getActionCommand();
		if (cmd.equals("shape"))
		{
			ShapeFileLoader sfl = new ShapeFileLoader();
			sfl.setVisible(true);
			sfl.runInR();
		}
		else if (cmd.equals("conv_pnt"))
		{
			DFPointConvertDialog conv = DFPointConvertDialog.getInstance();;
			conv.run();
		}
		else if (cmd.equals("builder"))
		{
//			if (DeducerSpatial.spatialWindow == null)
//			{
//				DeducerSpatial.spatialWindow = new SpatialPlotBuilder();
//			}
//			DeducerSpatial.spatialWindow.setVisible(true);
			
			SpatialPlotBuilder b = new SpatialPlotBuilder();
			b.setVisible(true);
		}
//		else if (cmd.equals("subset"))
//		{
//			//The subset menu item should be grayed out if the window is not open.
//			if (DeducerSpatial.spatialWindow != null)
//			{
//				//setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
//				DeducerSpatial.spatialWindow.beginSubsetting();
//			}
//		}
	}

}
