package edu.cens.spatial.plots.components;

import org.rosuda.deducer.widgets.SingleVariableWidget;
import org.rosuda.deducer.widgets.VariableSelectorWidget;

import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 3/25/11
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class HeatMeanComponentPanel extends AbstractComponentPanel {

    private final VariableSelectorWidget _vsw = new VariableSelectorWidget();
    private final SingleVariableWidget _lat = new SingleVariableWidget("Lat:",_vsw);
    private final SingleVariableWidget _lon = new SingleVariableWidget("Lon:",_vsw);
    private final SingleVariableWidget _z = new SingleVariableWidget("Z:",_vsw);


    private static enum k {
        dat, lat, lon, z
    }


    @Override
    public void initFromArgs() {
        _vsw.setSelectedData(getArg(k.dat));
        _lon.setSelectedVariable(getArg(k.lon));
        _lat.setSelectedVariable(getArg(k.lat));
        _z.setSelectedVariable(getArg(k.z));

    }

    public HeatMeanComponentPanel(Map<String, String> args) {
        super(args);
        initFields();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5,5,5,5);

        c.gridheight=3;
        add(_vsw,c);

        c.gridheight = 1; c.gridx = 1;  c.gridy = 0;
        add(_lat,c);

        c.gridy++;
        add(_lon,c);

        c.gridy++;
        add(_z,c);

        addOkPanel(c, 2);
    }

    @Override
    boolean doOK() {
        String dat   = _vsw.getSelectedData();
        String lat   = _lat.getSelectedVariable();
        String lon   = _lon.getSelectedVariable();
        String z  = _z.getSelectedVariable();


        //todo validate

        if(dat == null || lat == null || lon == null || z == null)
            return false;

        putArg(k.dat, dat);
        putArg(k.lat, lat);
        putArg(k.lon, lon);
        putArg(k.z, z);

        return true;
    }




}
