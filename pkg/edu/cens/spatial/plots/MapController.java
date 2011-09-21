package edu.cens.spatial.plots;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.RootPaneContainer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.rosuda.JGR.JGR;

import edu.cens.spatial.AcceptSubsetDialog;

public class MapController extends JMapController implements MouseListener,
MouseMotionListener, MouseWheelListener, ActionListener, ChangeListener
{

	protected static final int MOUSE_BUTTONS_MASK = MouseEvent.BUTTON3_DOWN_MASK
	| MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK;

	protected static final int MAC_MOUSE_BUTTON3_MASK = MouseEvent.CTRL_DOWN_MASK
	| MouseEvent.BUTTON1_DOWN_MASK;

	public MapController(JMapViewer map)
	{
		super(map);
	}

	public MapController(JMapViewer map, SpatialPlotBuilder b)
	{
		super(map);
		builder = b;
	}

	protected SpatialPlotBuilder builder;

	protected Point lastDragPoint;

	protected boolean isMoving = false;

	protected boolean movementEnabled = true;

	protected int movementMouseButton = MouseEvent.BUTTON1;
	protected int movementMouseButtonMask = MouseEvent.BUTTON1_DOWN_MASK;

	protected boolean wheelZoomEnabled = true;
	protected boolean doubleClickZoomEnabled = true;

	protected enum SubsetBoxState
	{
		DISABLED, // User has not requested to subset the data
		READY, // User has requested, but has not yet started "drawing" the box
		DRAGGING, // User is "drawing" the box
		ACCEPT_DIALOG
		// After a mouse release, an 'accept' dialog should appear
	};

	SubsetBoxState subsetBoxState = SubsetBoxState.DISABLED;
	// READY;

	// Point
	Coordinate subsetCorner1, subsetCorner2;

	public void mouseDragged(MouseEvent e)
	{
		if (!movementEnabled || !isMoving)
			return;


		if ((e.getModifiersEx() & MOUSE_BUTTONS_MASK) == movementMouseButtonMask
				&&
				subsetBoxState != SubsetBoxState.DRAGGING )
		{
			Point p = e.getPoint();
			if (lastDragPoint != null)
			{
				int diffx = lastDragPoint.x - p.x;
				int diffy = lastDragPoint.y - p.y;
				map.moveMap(diffx, diffy);
				builder.updatePlot();
			}
			lastDragPoint = p;
		}

		if (subsetBoxState == SubsetBoxState.DRAGGING  || 
				subsetBoxState == SubsetBoxState.ACCEPT_DIALOG)
		{
			if (subsetBoxState == SubsetBoxState.DRAGGING )
			{
				subsetCorner2 = map.getPosition(e.getPoint());
			}

			if (e.getPoint() != null)
			{
				((MapPanel) map).drawSubsetRectangle(
						subsetCorner1, 
						subsetCorner2, 
						subsetBoxState == SubsetBoxState.DRAGGING
				);
			}
		}

		////////////////////////////////////
	}

	public void mouseMoved(MouseEvent e)
	{
		// Mac OSX simulates with ctrl + mouse 1 the second mouse button hence
		// no dragging events get fired.
		//
		if (isPlatformOsx())
		{
			//			if (!movementEnabled || !isMoving)
			//				return;
			//			
			//			if (e.getModifiersEx() == MouseEvent.CTRL_DOWN_MASK)
			//			{
			//				Point p = e.getPoint();
			//				if (lastDragPoint != null)
			//				{
			//					int diffx = lastDragPoint.x - p.x;
			//					int diffy = lastDragPoint.y - p.y;
			//					map.moveMap(diffx, diffy);
			//				}
			//				lastDragPoint = p;
			//			}

		}

	}

	public void mouseClicked(MouseEvent e)
	{
		if (doubleClickZoomEnabled && e.getClickCount() == 2
				&& e.getButton() == MouseEvent.BUTTON1)
		{
			map.zoomIn(e.getPoint());
			builder.updatePlot();
		}
	}

	public void mousePressed(MouseEvent e)
	{

		if (e.getButton() == movementMouseButton || isPlatformOsx()
				&& e.getModifiersEx() == MAC_MOUSE_BUTTON3_MASK)
		{
			lastDragPoint = null;
			isMoving = true;

			if (subsetBoxState == SubsetBoxState.READY)
			{
				subsetCorner1 = map.getPosition(e.getPoint());
				subsetBoxState = SubsetBoxState.DRAGGING;
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == movementMouseButton || isPlatformOsx()
				&& e.getButton() == MouseEvent.BUTTON1)
		{
			lastDragPoint = null;
			isMoving = false;
			
			//Need to change cursor back.
			
			this.builder.setCursorNormal();

			if (subsetBoxState == SubsetBoxState.DRAGGING)
			{
				subsetBoxState = SubsetBoxState.ACCEPT_DIALOG;
				new AcceptSubsetDialog(this.builder, this).setVisible(true);
			}

			builder.updatePlot();
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{

	}

	public void stateChanged(ChangeEvent arg0)
	{
		doZoom(((MapPanel) map).getZoomSliderLevel(), null);
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (wheelZoomEnabled)
		{
			doZoom(map.getZoom() - e.getWheelRotation(), e.getPoint());
		}
	}

	private void doZoom(int amount, Point p)
	{
		map.setZoom(amount, p);
		builder.updatePlot();
		if (subsetBoxState != SubsetBoxState.DISABLED)
		{
			((MapPanel) map).drawSubsetRectangle(subsetCorner1, subsetCorner2, false);
		}
	}

	public void addListenersTo(Component comp)
	{
		if (this instanceof MouseListener)
			comp.addMouseListener((MouseListener) this);
		if (this instanceof MouseWheelListener)
			comp.addMouseWheelListener((MouseWheelListener) this);
		if (this instanceof MouseMotionListener)
			comp.addMouseMotionListener((MouseMotionListener) this);
	}

	public boolean isMovementEnabled()
	{
		return movementEnabled;
	}

	/**
	 * Enables or disables that the map pane can be moved using the mouse.
	 * 
	 * @param movementEnabled
	 */
	public void setMovementEnabled(boolean movementEnabled)
	{
		this.movementEnabled = movementEnabled;
	}

	public int getMovementMouseButton()
	{
		return movementMouseButton;
	}

	/**
	 * Sets the mouse button that is used for moving the map. Possible values
	 * are:
	 * <ul>
	 * <li>{@link MouseEvent#BUTTON1} (left mouse button)</li>
	 * <li>{@link MouseEvent#BUTTON2} (middle mouse button)</li>
	 * <li>{@link MouseEvent#BUTTON3} (right mouse button)</li>
	 * </ul>
	 * 
	 * @param movementMouseButton
	 */
	public void setMovementMouseButton(int movementMouseButton)
	{
		this.movementMouseButton = movementMouseButton;
		switch (movementMouseButton)
		{
			case MouseEvent.BUTTON1:
				movementMouseButtonMask = MouseEvent.BUTTON1_DOWN_MASK;
				break;
			case MouseEvent.BUTTON2:
				movementMouseButtonMask = MouseEvent.BUTTON2_DOWN_MASK;
				break;
			case MouseEvent.BUTTON3:
				movementMouseButtonMask = MouseEvent.BUTTON3_DOWN_MASK;
				break;
			default:
				throw new RuntimeException("Unsupported button");
		}
	}

	public boolean isWheelZoomEnabled()
	{
		return wheelZoomEnabled;
	}

	public void setWheelZoomEnabled(boolean wheelZoomEnabled)
	{
		this.wheelZoomEnabled = wheelZoomEnabled;
	}

	public boolean isDoubleClickZoomEnabled()
	{
		return doubleClickZoomEnabled;
	}

	public void setDoubleClickZoomEnabled(boolean doubleClickZoomEnabled)
	{
		this.doubleClickZoomEnabled = doubleClickZoomEnabled;
	}

	/**
	 * Replies true if we are currently running on OSX
	 * 
	 * @return true if we are currently running on OSX
	 */
	public static boolean isPlatformOsx()
	{
		String os = System.getProperty("os.name");
		return os != null && os.toLowerCase().startsWith("mac os x");
	}

	public void actionPerformed(ActionEvent arg0)
	{
		String cmd = arg0.getActionCommand();
		if (cmd.equals("in"))
		{
			map.zoomIn();
			builder.updatePlot();
		}
		else if (cmd.equals("out"))
		{
			map.zoomOut();
			builder.updatePlot();
		}
	}

	public void startSubsetting()
	{
		this.subsetBoxState = SubsetBoxState.READY;
	}

	public boolean executeSubsetting(boolean keepSelected, String subsetName)
	{
		//1: Find all the plotted points (worry about other objects later)
		//2: Find which points lie inside the shape
		//3: Reassign the dataframe to only those points

		double minLat = Math.min(subsetCorner1.getLat(), subsetCorner2.getLat());
		double minLon = Math.min(subsetCorner1.getLon(), subsetCorner2.getLon());

		double maxLat = Math.max(subsetCorner1.getLat(), subsetCorner2.getLat());
		double maxLon = Math.max(subsetCorner1.getLon(), subsetCorner2.getLon());

		boolean wasSuccessful =	this.builder.executeSubsetting(minLat, minLon, maxLat, maxLon, keepSelected, subsetName);

		if (wasSuccessful)
		{
			this.subsetBoxState = SubsetBoxState.DISABLED;
			((MapPanel) map).clearSubsetRectangle();
			map.repaint();
		}

		return wasSuccessful;
		//4: Redraw the plot
	}

	public void stopSubsetting()
	{
		//setCursorNormal();
		this.subsetBoxState = SubsetBoxState.DISABLED;
		((MapPanel) map).clearSubsetRectangle();
		this.builder.stopSubsetting();
		map.repaint();
	}
}
