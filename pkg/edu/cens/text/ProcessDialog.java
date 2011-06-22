package edu.cens.text;

import org.rosuda.JGR.JGR;
import org.rosuda.deducer.Deducer;
import org.rosuda.deducer.toolkit.HelpButton;
import org.rosuda.deducer.toolkit.OkayCancelPanel;
import org.rosuda.deducer.widgets.ObjectChooserWidget;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA. User: Neal Date: 4/8/11 Time: 7:09 AM
 */

public class ProcessDialog extends JDialog
{

	private ObjectChooserWidget _source = new ObjectChooserWidget(
			"Source Corpus:", new JFrame() //TODO replace with JGR.MAINRCONSOLE 
			//JGR.MAINRCONSOLE
			)
	{
		{
			setClassFilter("Corpus");
			refreshObjects();
		}
	};

	// private JLabel _source = new JLabel("Source");

	private JTextField _target = new JTextField(20)
	{
		{
			setBorder(BorderFactory.createTitledBorder("New Corpus Name:"));
		}
	};

	private class ProcessActionPanel extends JTextField implements
			MouseListener
	{
		ProcessCmd _command;

		@Override
		public void mousePressed(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON3
					|| e.getClickCount() == 2
					|| ((e.getModifiersEx() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK))
			{

				_list.setSelectedValue(this, false);

				final JPopupMenu jpm = new JPopupMenu();

				final JMenuItem up = new JMenuItem("Move Up");
				final JMenuItem down = new JMenuItem("Move Down");

				ActionListener al = new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						int i = e.getSource() == up ? -1 : 1;

						for (int j = 0; j < _model.getSize(); j++)
						{
							if (_model.get(j) == ProcessActionPanel.this
									&& j + i >= 0 && j + i < _model.getSize())
							{
								_model.remove(j);
								_model.insertElementAt(ProcessActionPanel.this,
										j + i);
								_list.setSelectedValue(ProcessActionPanel.this,
										false);
								break;
							}
						}
					}
				};
				up.addActionListener(al);
				down.addActionListener(al);

				final JMenuItem disable = new JMenuItem(isEnabled() ? "Disable"
						: "Enable");
				disable.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						setEnabled(!isEnabled());
					}
				});

				jpm.add(up);
				jpm.add(down);
				jpm.add(disable);
				for (JMenuItem item : _command.getExtraOptions())
				{
					jpm.add(item);
				}

				jpm.show(e.getComponent(), e.getX(), e.getY());

			}
		}

		private ProcessActionPanel(ProcessCmd command)
		{
			super(20);
			_command = command;
			setEnabled(true);
			setEditable(false);

			setBorder(new EtchedBorder());
			setText(_command.getLabel());

		}
	}

	private DefaultListModel _model = new DefaultListModel();
	{
		int n = ProcessCmd.values().length;
		for (int i = 0; i < n; i++)
		{
			_model.addElement(new ProcessActionPanel(ProcessCmd.values()[i]));
		}
	}

	private JList _list = new JList(_model)
	{
		{
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setCellRenderer(new ListCellRenderer()
			{
				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus)
				{
					Component c = (Component) value;
					if (isSelected)
					{
						c.setBackground(list.getSelectionBackground());
						c.setForeground(list.getSelectionForeground());
					}
					else
					{
						c.setBackground(list.getBackground());
						c.setForeground(list.getForeground());
					}
					return c;
				}
			});

			addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{

					int index = locationToIndex(e.getPoint());

					if (index != -1)
					{
						ProcessActionPanel checkbox = (ProcessActionPanel) getModel()
								.getElementAt(index);
						checkbox.mouseClicked(e);
						repaint();
					}
				}
			});

			setBorder(BorderFactory.createTitledBorder("Actions:"));

		}
	};

	boolean _ok, _cancel;

	final OkayCancelPanel _okCancel = new OkayCancelPanel(false, false,
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					String cmd = e.getActionCommand();
					if ("OK".equals(cmd))
					{
						_ok = doOK();
					}
					else if ("Cancel".equals(cmd))
					{
						_cancel = doCancel();
					}

				}
			});

	private final HelpButton _help = new HelpButton("");

	private final JPanel _okPanel = new JPanel()
	{
		{
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			_help.setPreferredSize(new java.awt.Dimension(36, 36));
			_okCancel.setPreferredSize(new java.awt.Dimension(267, 39));

			c.anchor = GridBagConstraints.WEST;
			add(_help, c);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			c.anchor = GridBagConstraints.CENTER;
			add(new JLabel(), c);

			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.NORTHEAST;
			add(_okCancel, c);

			setPreferredSize(new Dimension(400, 39));
		}
	};

	public ProcessDialog()
	{
		setTitle("Preprocess Corpus...");
		add(new JPanel()
		{
			{

				setLayout(new GridBagLayout());
				setPreferredSize(new Dimension(500, 390));
				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5, 5, 5, 5);
				c.gridy = 0;

				add(_source, c);

				c.gridy++;
				add(_target, c);

				c.gridy++;

				add(_list, c);
				PreprocessingTable table = new PreprocessingTable();
				table.setBorder(BorderFactory.createTitledBorder("Actions:"));
				//add(table, c); //this is the new prepocessing table

				c.weighty = 1;
				c.weightx = 1;
				c.gridy++;
				c.gridwidth = 1;
				c.fill = GridBagConstraints.BOTH;
				add(new JLabel(), c); // spacing blah

				c.gridy++;
				c.weighty = 0;
				c.anchor = GridBagConstraints.SOUTH;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.insets = new Insets(10, 10, 10, 10);
				add(_okPanel, c);
			}
		});

		pack();

	}

	public static void main(String[] args)
	{
		new ProcessDialog().setVisible(true);
	}

	public boolean doCancel()
	{
		dispose();
		return true;
	}

	public boolean doOK()
	{

		String s = _source.getModel().toString();
		String t = _target.getText();

		Enumeration<?> e = _model.elements();

		while (e.hasMoreElements())
		{
			ProcessActionPanel p = (ProcessActionPanel) e.nextElement();
			if (p.isEnabled())
			{
				Deducer.execute(t + "<- " + p._command.getRCmd(s) + ";\n");
				s = t;
			}
		}

		dispose();

		return true;
	}

}
