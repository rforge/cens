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

public class BubbleComponentPanel
        extends AbstractComponentPanel {

    private final VariableSelectorWidget _vsw = new VariableSelectorWidget();
    private final SingleVariableWidget _lat = new SingleVariableWidget("Lat:",_vsw);
    private final SingleVariableWidget _lon = new SingleVariableWidget("Lon:",_vsw);
    private final SingleVariableWidget _z = new SingleVariableWidget("Z:",_vsw);

    private final JTextField _minRadius = new JTextField(8);
    private final JTextField _maxRadius = new JTextField(8);



    private static enum k {
        dat, lat, lon, z, minRadius, maxRadius
    }

    @Override
    public void initDefault() {
        _maxRadius.setText(".1");
        _minRadius.setText(".05");
    }

    @Override
    public void initFromArgs() {
        _vsw.setSelectedData(_args.get(k.dat.name()));
        _lon.setSelectedVariable(_args.get(k.lon.name()));
        _lat.setSelectedVariable(_args.get(k.lat.name()));
        _z.setSelectedVariable(_args.get(k.z.name()));
        _maxRadius.setText(_args.get(k.maxRadius.name()));
        _minRadius.setText(_args.get(k.minRadius.name()));
    }

    public BubbleComponentPanel(Map<String, String> args) {
        super(args);
        initFields();


        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        _vsw.setBorder(BorderFactory.createLoweredBevelBorder());
        c.anchor = GridBagConstraints.WEST;
        c.gridheight=4;
        add(_vsw,c);

        c.gridheight = 1; c.gridx = 1;
        c.gridy = 0; c.gridwidth=1;
        c.weightx = c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        add(_lat,c);

        c.gridy++;
        add(_lon,c);

        c.gridy++;
        add(_z,c);

        c.gridy++;
        add(new JPanel(){{
            setBorder(BorderFactory.createTitledBorder("Radius"));
            add(_minRadius);
            add(_maxRadius);
        }},c);

        c.gridy++;
        addOkPanel(c, 4);
    }

    @Override
    boolean doOK() {
        String dat   = _vsw.getSelectedData();
        String lat   = _lat.getSelectedVariable();
        String lon   = _lon.getSelectedVariable();
        String z     = _z.getSelectedVariable();

        if(dat == null || lat == null || lon == null || z == null)
            return false;


        String min = _minRadius.getText();
        String max = _maxRadius.getText();

        try {
            double x = Double.parseDouble(min);
            double y = Double.parseDouble(max);
            if (x > y) {
                String t = min;
                min = max;
                max = t;
            }
        }
        catch(NumberFormatException pe) {
            return false;
        }


        putArg(k.dat, dat);
        putArg(k.lat, lat);
        putArg(k.lon, lon);
        putArg(k.z, z);
        putArg(k.minRadius, min);
        putArg(k.maxRadius, max);



        return true;
    }


}
