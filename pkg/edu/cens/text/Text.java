package edu.cens.text;

import org.rosuda.JGR.JGR;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.deducer.Deducer;
import org.rosuda.deducer.WindowTracker;
import org.rosuda.ibase.toolkit.EzMenuSwing;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA. User: Neal Date: 1/15/11 Time: 1:08 PM
 */

public class Text
{

	private static TermFrequencyDialog viewOptionsDialog = new TermFrequencyDialog(JGR.MAINRCONSOLE);
	
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
				//Deducer.eval("cens.choose_and_do(print);");
				viewOptionsDialog.setViewMethod(TermFrequencyDialog.TOTAL_FREQUENCIES);
				viewOptionsDialog.setCopora(getCorpora());
				viewOptionsDialog.setVisible(true);
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
				//Deducer.eval("cens.choose_and_do(cens.txt_barplot);");
				viewOptionsDialog.setViewMethod(TermFrequencyDialog.BAR_CHART);
				viewOptionsDialog.setCopora(getCorpora());
				viewOptionsDialog.setVisible(true);
				
				//String[]
				//do as much in Java as possible.
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
public static String[] getCorpora()
{
	org.rosuda.REngine.REXP corpora = Deducer.eval("cens.getCorpusNames()");//.asList().keys();
	
	String[] v = {""};
	try
	{
		v = corpora.asStrings();
	}
	catch (REXPMismatchException e1)
	{
		e1.printStackTrace();
	}
	return v;
	
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
		frame.setVisible(true);
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

		
		final JMenuItem extractCorpusMenuItem = new JMenuItem("Extract Corpus");
		
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
		extractCorpusMenuItem.addActionListener(extractCorpusListener);
		main.add(extractCorpusMenuItem);
		

		// final JMenuItem createDocTermMatrixMenuItem = new JMenuItem("Create Document-Term Matrix");
//		final ActionListener createDocTermListener = new ActionListener()
//		{
//			
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//			
//				TermFrequencyDialog createDocTermMatrixDialog = new TermFrequencyDialog(JGR.MAINRCONSOLE, 
//						new String[]{"exquisite", "corpus"});
//				createDocTermMatrixDialog.setLocationRelativeTo(null);
//				createDocTermMatrixDialog.setVisible(true);
//				WindowTracker.addWindow(createDocTermMatrixDialog);
//			}
//		};
//		createDocTermMatrixMenuItem.addActionListener(createDocTermListener);
		//main.add(createDocTermMatrixMenuItem);
		

		for (Menu m : Menu.values())
		{
			EzMenuSwing.addJMenuItem(JGR.MAINRCONSOLE, text, m._label,
					m.name(), m);
			// todo accelerator keys
		}

	}

}
