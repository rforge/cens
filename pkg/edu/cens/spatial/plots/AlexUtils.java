package edu.cens.spatial.plots;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class AlexUtils
{
	public static void fixClicks(final Component c)
	{
		// This is a time bomb because when (if?) Sun fixes the bug, this
		// method will
		// add an extra click. We should put an if statement here that
		// immediately
		// returns if the Java version is greater than or equal to that in
		// which the bug
		// is fixed. Problem is, we don't know what that version will be. [Jon
		// Aquino]
		c.addMouseListener(new MouseListener()
		{

			public void mousePressed(MouseEvent e)
			{
				System.out.println("yeeha?");
				add(e);
			}

			public void mouseExited(MouseEvent e)
			{
				System.out.println("yeeha?");
				add(e);
			}

			public void mouseClicked(MouseEvent e)
			{
				System.out.println("failed click?");
				add(e);
			}

			public void mouseEntered(MouseEvent e)
			{
				System.out.println("yeeha?");
				add(e);
			}

			private MouseEvent event(int i)
			{
				return (MouseEvent) events.get(i);
			}

			public void mouseReleased(MouseEvent e)
			{
				System.out.println("release?");
				add(e);

				if ((events.size() == 4)
						&& (event(0).getID() == MouseEvent.MOUSE_PRESSED)
						&& (event(1).getID() == MouseEvent.MOUSE_EXITED)
						&& (event(2).getID() == MouseEvent.MOUSE_ENTERED))
				{
					System.out.println("clicked!");
					c.dispatchEvent(new MouseEvent(c, MouseEvent.MOUSE_CLICKED, System
							.currentTimeMillis(), e.getModifiers(), e.getX(), e.getY(), e
							.getClickCount(), e.isPopupTrigger()));
				}
			}

			private void add(MouseEvent e)
			{
				if (events.size() == 4)
				{
					events.remove(0);
				}
				events.add(e);
			}

			private ArrayList	events	= new ArrayList();
		});
	}
}
