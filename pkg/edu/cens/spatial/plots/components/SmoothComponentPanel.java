package edu.cens.spatial.plots.components;

import org.rosuda.deducer.widgets.SingleVariableWidget;
import org.rosuda.deducer.widgets.VariableSelectorWidget;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 1/25/11
 * Time: 8:26 AM
 */

public class SmoothComponentPanel
        extends AbstractComponentPanel {

    private final VariableSelectorWidget _vsw = new VariableSelectorWidget();
    private final SingleVariableWidget _lat = new SingleVariableWidget("Lat:",_vsw);
    private final SingleVariableWidget _lon = new SingleVariableWidget("Lon",_vsw);
    private final SingleVariableWidget _z = new SingleVariableWidget("Z:",_vsw);

    private final JSlider _smooth = new JSlider(0,100){{
        setBorder(BorderFactory.createTitledBorder("Alpha:"));
    }};


    private static enum k {
        dat, lat, lon, z, type, smooth
    }

    @Override
    public void initDefault() {
        _smooth.setValue(10);
    }

    @Override
    public void initFromArgs() {
        _vsw.setSelectedData(_args.get(k.dat.name()));
        _lon.setSelectedVariable(_args.get(k.lon.name()));
        _lat.setSelectedVariable(_args.get(k.lat.name()));
        _z.setSelectedVariable(_args.get(k.z.name()));
        _smooth.setValue((int) (100.0 *Double.parseDouble(_args.get(k.smooth.name()))));
    }

    public SmoothComponentPanel(Map<String, String> args) {
        super(args);
        initFields();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5,5,5,5);

        c.gridheight=5;
        add(_vsw,c);

        c.gridheight = 1; c.gridx = 1; c.gridy = 0;
        add(_lat,c);

        c.gridx = 1; c.gridy++;
        add(_lon,c);

        c.gridx = 1; c.gridy++;
        add(_z,c);

        c.gridx = 1; c.gridy++;
        add(_smooth,c);

        addOkPanel(c, 3);

    }

    @Override
    boolean doOK() {
        String dat   = _vsw.getSelectedData();
        String lat   = _lat.getSelectedVariable();
        String lon   = _lon.getSelectedVariable();
        String z  = _z.getSelectedVariable();
        int smooth = _smooth.getValue();

        //todo validate

        putArg(k.dat, dat);
        putArg(k.lat, lat);
        putArg(k.lon, lon);
        putArg(k.z, z);
        putArg(k.smooth, Double.toString(smooth * .01));



        return true;
    }

    public static void main(String[] args){
        JDialog jDialog = new JDialog();
        jDialog.setSize(new Dimension(400,400));
        jDialog.add(new SmoothComponentPanel(Collections.<String, String>emptyMap()));
        jDialog.pack();
        jDialog.setVisible(true);
    }


}
