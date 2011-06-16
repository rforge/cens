package edu.cens.spatial.plots.components;

import org.rosuda.deducer.widgets.SingleVariableWidget;
import org.rosuda.deducer.widgets.VariableSelectorWidget;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 1/25/11
 * Time: 8:26 AM
 */

public class DistributionComponentPanel
        extends AbstractComponentPanel {

    private final VariableSelectorWidget _vsw = new VariableSelectorWidget();
    private final SingleVariableWidget _x = new SingleVariableWidget("Lon:",_vsw);
    private final SingleVariableWidget _y = new SingleVariableWidget("Lat",_vsw);

    private static enum k {
        dat, lat, lon, type
    }

    @Override
    public void initFromArgs() {
        _vsw.setSelectedData(getArg(k.dat));
        _x.setSelectedVariable(getArg(k.lon));
        _y.setSelectedVariable(getArg(k.lat));

    }

    public DistributionComponentPanel(Map<String, String> args) {
        super(args);
        initFields();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        c.gridy = 0;
        c.gridheight=3;
        c.anchor = GridBagConstraints.WEST;
        add(_vsw,c);

        c.gridheight = 1;
        c.gridx = 1;
        add(_y,c);

        c.gridy++;
        add(_x,c);

        addOkPanel(c,2);
    }

    @Override
    boolean doOK() {
        String dat   = _vsw.getSelectedData();
        String lat   = _y.getSelectedVariable();
        String lon   = _x.getSelectedVariable();

        if (dat == null || lat == null || lon == null) return false;

        putArg(k.dat, dat);
        putArg(k.lat, lat);
        putArg(k.lon, lon);



        return true;
    }

}
