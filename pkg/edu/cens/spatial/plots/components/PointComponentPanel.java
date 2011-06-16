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

public class PointComponentPanel
        extends AbstractComponentPanel {

    private final VariableSelectorWidget _vsw = new VariableSelectorWidget();
    private final SingleVariableWidget _x = new SingleVariableWidget("Lon", _vsw);
    private final SingleVariableWidget _y = new SingleVariableWidget("Lat", _vsw);


    private final JTextField _cex = new JTextField("1", 8){{
        setBorder(BorderFactory.createTitledBorder("Size:"));
    }};

    private final JTextField _pch = new JTextField("1", 8){{
        setBorder(BorderFactory.createTitledBorder("Symbol:"));
    }};
    
    private static enum k {
        dat, lat, lon, pch, cex
    }

    @Override
    public void initFromArgs() {
        _vsw.setSelectedData(_args.get(k.dat.name()));
        _x.setSelectedVariable(_args.get(k.lon.name()));
        _y.setSelectedVariable(_args.get(k.lat.name()));
        _cex.setText(_args.get(k.cex.name()));
        _pch.setText(_args.get(k.pch.name()));
    }

    public PointComponentPanel(Map<String, String> args) {
        super(args);
        initFields();

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5,5,5,5);

        c.gridy = c.gridx = 0;
        c.gridheight=4;
        add(_vsw,c);

        c.gridheight = 1; c.gridx = 1;
        add(_y, c);

        c.gridy++;
        add(_x, c);

        c.gridy++;
        add(_cex, c);

        c.gridy++;
        add(_pch, c);

        addOkPanel(c,2);

    }

    @Override
    boolean doOK() {
        String dat   = _vsw.getSelectedData();
        String lat   = _y.getSelectedVariable();
        String lon   = _x.getSelectedVariable();

        String cex   = _cex.getText();
        String pch   = _pch.getText();

        //todo validate
        if (dat == null || lat == null || lon == null) return false;

        putArg(k.dat, dat);
        putArg(k.lat, lat);
        putArg(k.lon, lon);
        putArg(k.cex, cex);
        putArg(k.pch, pch);



        return true;
    }

}
