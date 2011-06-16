package edu.cens.spatial.plots.components;

import org.rosuda.JGR.layout.AnchorConstraint;
import org.rosuda.JGR.layout.AnchorLayout;
import org.rosuda.deducer.toolkit.HelpButton;
import org.rosuda.deducer.toolkit.OkayCancelPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 1/20/11
 * Time: 9:16 AM
 */

public class AbstractComponentPanel extends JPanel {

    final Map<String, String> _args;

    protected String _errorMsg = "Check Settings";

    private boolean _ok = false, _cancel = false;

    final OkayCancelPanel _okCancel = new OkayCancelPanel(false, false, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if("OK".equals(cmd)) _ok= doOK();
            else if ("Cancel".equals(cmd)) _cancel = doCancel();
            _onOK.actionPerformed(e);//chaining instead of lifo
        }
    });

    ActionListener _onOK;

    private final HelpButton _help = new HelpButton("");

    final JPanel _okPanel = new JPanel(){{
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        _help.setPreferredSize(new java.awt.Dimension(36, 36));
        _okCancel.setPreferredSize(new java.awt.Dimension(267, 39));


        c.anchor = GridBagConstraints.WEST  ;
        add(_help, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx=1.0;
        c.anchor = GridBagConstraints.CENTER;
        add(new JLabel(), c);

        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHEAST;
        add(_okCancel, c);

        setPreferredSize(new Dimension(400,39));
    }};


    public AbstractComponentPanel(Map<String, String> args) {
        _args = args;
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(550, 440));
    }




    public void initDefault(){}
    public void initFromArgs(){}


    public void addActionListener(ActionListener lis) {
        _onOK = lis;
    }

    public void setHelpUrl(String url) {
        _help.setUrl(url);
    }

    public boolean isOk() {
        return _ok;
    }

    public boolean isCancel() {
        return _cancel;
    }

    boolean doOK(){ return  true;}
    boolean doCancel() {return true;}

    public Map<String, String> getArgs() {
        return _args;
    }

    protected final <T extends Enum> String getArg(T key) {
        return _args.get(key.name());
    }


    protected final <T extends Enum> void putArg(T key, String value) {
        if(value == null || value.length() == 0) _args.remove(key);
        else _args.put(key.name(), value);
    }

    protected final void addLabelAdvance(String label, GridBagConstraints c) {
        add(new JLabel(label), c); c.gridx++;
    }


    protected void addOkPanel(GridBagConstraints c, int width) {
        c.weighty = 1;
        c.weightx =1;
        c.gridx= 0;
        c.gridy++;
        c.gridwidth = width;
        c.fill = GridBagConstraints.BOTH;
        add(new JLabel(),c);        //spacing blah

        c.gridy++;
        c.weighty = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10,10,10,10);
        add(_okPanel,c);
    }

    protected final void initFields() {
        if(_args.isEmpty()) {
            initDefault();
        } else {
            initFromArgs();
        }
    }

    public String getErrorMsg() {
        return _errorMsg;
    }
}
