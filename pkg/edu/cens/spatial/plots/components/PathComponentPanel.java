package edu.cens.spatial.plots.components;

import org.rosuda.deducer.widgets.SingleVariableWidget;
import org.rosuda.deducer.widgets.VariableSelectorWidget;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 1/25/11
 * Time: 8:26 AM
 */

public class PathComponentPanel
        extends AbstractComponentPanel {

    private final VariableSelectorWidget _vsw = new VariableSelectorWidget();
    private final SingleVariableWidget _lat = new SingleVariableWidget("Lat:",_vsw);
    private final SingleVariableWidget _lon = new SingleVariableWidget("Lon:",_vsw);
    private final SingleVariableWidget _subj = new SingleVariableWidget("Subject:",_vsw);
    private final SingleVariableWidget _time = new SingleVariableWidget("Time:",_vsw);

    private final JCheckBox _arrow = new JCheckBox("Arrows");

    private final JTextField _col = new JTextField(8);
    private final JTextField _cex = new JTextField(8);
    private final JTextField _pch = new JTextField(8);

    private static enum k {
        dat, lat, lon, subj, time, col, cex, pch, arrow
    }


    @Override
    public void initFromArgs() {
        _vsw.setSelectedData(getArg(k.dat));
        _lon.setSelectedVariable(getArg(k.lon));
        _lat.setSelectedVariable(getArg(k.lat));
        _subj.setSelectedVariable(getArg(k.subj));
        _time.setSelectedVariable(getArg(k.time));


        _col.setText(getArg(k.col));
        _cex.setText(getArg(k.cex));
        _pch.setText(getArg(k.pch));
        _arrow.setSelected(Boolean.parseBoolean(getArg(k.arrow)));
    }

    public PathComponentPanel(Map<String, String> args) {
        super(args);
        initFields();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5,5,5,5);

        c.gridheight=5;
        add(_vsw,c);

        c.gridheight = 1; c.gridx = 1;  c.gridy = 0;
        add(_lat,c);

        c.gridy++;
        add(_lon,c);

        c.gridy++;
        add(_subj,c);

        c.gridy++;
        add(_time,c);

        c.gridy++;
        add(_arrow,c);


        addOkPanel(c, 2);
    }

    @Override
    boolean doOK() {
        String dat   = _vsw.getSelectedData();
        String lat   = _lat.getSelectedVariable();
        String lon   = _lon.getSelectedVariable();
        String subj  = _subj.getSelectedVariable();
        String time  = _time.getSelectedVariable();


        String col = _col.getText();
        String cex = _cex.getText();
        String pch = _pch.getText();

        //todo validate

        if(dat == null || lat == null || lon == null || subj == null || time == null)
            return false;

        putArg(k.dat, dat);
        putArg(k.lat, lat);
        putArg(k.lon, lon);
        putArg(k.time, time);
        putArg(k.subj, subj);

        putArg(k.cex, cex);
        putArg(k.col, col);
        putArg(k.pch, pch);

        putArg(k.arrow, Boolean.toString(_arrow.isSelected()).toUpperCase());


        return true;
    }




}
