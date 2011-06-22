package edu.cens.text;

import org.rosuda.JGR.JGR;
import org.rosuda.deducer.Deducer;
import org.rosuda.deducer.WindowTracker;
import org.rosuda.ibase.toolkit.EzMenuSwing;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA. User: Neal Date: 1/15/11 Time: 1:08 PM
 */

public class Text
{

	private enum Menu implements ActionListener
	{
		transform("Preprocess corpus...")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new ProcessDialog().setVisible(true);
			}
		},

		view("View Corpus")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Deducer.eval("cens.viewer();");
			}
		},

		tf("Term Frequency")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Deducer.eval("cens.choose_and_do(print);");
			}
		},
		wc("Word Cloud")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Deducer.eval("cens.choose_and_do(cens.word_cloud);");
			}
		},
		barplot("Bar Chart")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Deducer.eval("cens.choose_and_do(cens.txt_barplot);");
			}
		},

		// MDS("MDS"){ //todo
		// public void actionPerformed(ActionEvent e) {
		// Deducer.eval("cens.choose_and_do(cens.mds);");
		// }
		// },
		;

		private final String _label;

		private Menu(String label)
		{
			_label = label;
		}

	}

	public static class MenuTest extends JFrame
	{
		public MenuTest()
		{
			super();

			MenuListener listener = new MenuListener()
			{
				@Override
				public void menuCanceled(MenuEvent e)
				{
					dumpInfo("Canceled", e);
				}

				@Override
				public void menuDeselected(MenuEvent e)
				{
					dumpInfo("Deselected", e);
				}

				@Override
				public void menuSelected(MenuEvent e)
				{
					dumpInfo("Selected", e);
				}

				private void dumpInfo(String s, MenuEvent e)
				{
					JMenu menu = (JMenu) e.getSource();
					System.out.println(s + ": " + menu.getText());
				}
			};

			JMenu fileMenu = new JMenu("File");
			fileMenu.addMenuListener(listener);
			fileMenu.add(new JMenuItem("Open"));
			fileMenu.add(new JMenuItem("Close"));
			fileMenu.add(new JMenuItem("Exit"));
			JMenu helpMenu = new JMenu("Help");
			helpMenu.addMenuListener(listener);
			helpMenu.add(new JMenuItem("About MenuTest"));
			helpMenu.add(new JMenuItem("Class Hierarchy"));
			helpMenu.addSeparator();
			helpMenu.add(new JCheckBoxMenuItem("Balloon Help"));
			final JMenu subMenu = new JMenu("Categories");
			final JMenuItem dynm = new JMenuItem("test");
			subMenu.addMenuListener(new MenuListener()
			{
				@Override
				public void menuSelected(MenuEvent e)
				{
					subMenu.add(dynm);
				}

				@Override
				public void menuDeselected(MenuEvent e)
				{
					subMenu.removeAll();
				}

				@Override
				public void menuCanceled(MenuEvent e)
				{
					subMenu.removeAll();
				}
			});
			JRadioButtonMenuItem rb;
			ButtonGroup group = new ButtonGroup();
			subMenu.add(rb = new JRadioButtonMenuItem("A Little Help", true));
			group.add(rb);
			subMenu.add(rb = new JRadioButtonMenuItem("A Lot of Help"));
			group.add(rb);
			helpMenu.add(subMenu);
			JMenuBar mb = new JMenuBar();
			mb.add(fileMenu);
			mb.add(helpMenu);
			setJMenuBar(mb);
		}

	}

	public static void main(String args[])
	{
		JFrame frame = new MenuTest();
		frame.setSize(300, 300);
		frame.show();
	}

	public void initJGR()
	{

		String text = "Text";

		// EzMenuSwing.addMenu(JGR.MAINRCONSOLE, text);
		// EzMenuSwing.addMenuBefore(JGR.MAINRCONSOLE, text, "Help");

		// Add the menu item to left of "Help" menu
		// TODO : this should probably be folded into EzMenuSwing
		String toRightOfItem = "Help";
		JMenuBar mb = JGR.MAINRCONSOLE.getJMenuBar();
		int insertPos = 0;
		boolean found = false;
		for (; insertPos < mb.getMenuCount(); insertPos++)
		{
			if (mb.getMenu(insertPos).getLabel().equals(toRightOfItem))
			{
				found = true;
				break;
			}
		}
		if (!found)
		{
			throw new IllegalArgumentException("Could not find '"
					+ toRightOfItem + "' in the menu bar.");
		}
		mb.add(new JMenu(text), insertPos);

		JMenu main = EzMenuSwing.getMenu(JGR.MAINRCONSOLE, text);
		// final JMenu im = new JMenu("Extract Corpus");

		final ActionListener extractCorpusListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				//JFrame f = new JFrame();
				// needsRLocked=true;
				ExtractCorpusDialog inst = new ExtractCorpusDialog( JGR.MAINRCONSOLE );
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
				WindowTracker.addWindow(inst);
			}
		};

		final JMenuItem im = new JMenuItem("Extract Corpus");
		// final JMenu im = new JMenu("Extract Corpus");
		im.addActionListener(extractCorpusListener);
		main.add(im);

		for (Menu m : Menu.values())
		{
			EzMenuSwing.addJMenuItem(JGR.MAINRCONSOLE, text, m._label,
					m.name(), m);
			// todo accelerator keys
		}

		final MenuListener extractListener = new MenuListener()
		{
			@Override
			public void menuSelected(MenuEvent e)
			{
				final JMenu source = (JMenu) e.getSource();
				source.removeAll();
				try
				{
					for (final String s : Deducer.eval(
							"names(" + source.getText() + ")").asStrings())
					{
						source.add(new JMenuItem(s)).addActionListener(
								new ActionListener()
								{
									@Override
									public void actionPerformed(ActionEvent e)
									{
										Deducer.execute(String
												.format("%s.Corpus <- Corpus(VectorSource(%s$%s))",
														s, source.getText(), s));
									}
								});
					}
				}
				catch (Exception exception)
				{
				}
			}

			@Override
			public void menuDeselected(MenuEvent e)
			{
			}

			@Override
			public void menuCanceled(MenuEvent e)
			{
			}
		};

		/*
		 * * /im.addMenuListener(new MenuListener() { public void
		 * menuSelected(MenuEvent e) { im.removeAll(); for(Object o :
		 * Deducer.getData()) { RObject rObject = (RObject) o; final JMenu child
		 * = new JMenu(rObject.getName());
		 * child.addMenuListener(extractListener); im.add(child); } }
		 * 
		 * public void menuDeselected(MenuEvent e) {} public void
		 * menuCanceled(MenuEvent e) {} }); /*
		 */

	}

}
