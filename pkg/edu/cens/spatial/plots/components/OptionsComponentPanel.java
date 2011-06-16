package edu.cens.spatial.plots.components;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 2/7/11
 * Time: 8:04 PM
 */


public class OptionsComponentPanel extends AbstractComponentPanel{



    private final JTextField _lon = new JTextField(8);
    private final JTextField _lat = new JTextField(8);
    private final JComboBox _zoom = new JComboBox();

    private static String ds(double d)
    {
        return Double.isNaN(d) ? "" : "" + d;
    }


    public OptionsComponentPanel(double[][] box) {
        super(Collections.<String, String>emptyMap());

        for(int i = -10; i < 5; i++) _zoom.addItem(i);


        _lon.setText(ds((box[0][1] + box[0][0]) *.5));
        _lat.setText(ds((box[1][1] + box[1][0]) * .5));
        int round = (int) Math.round(Math.log(Math.abs(.5 * (box[0][1] - box[0][0]))));
        _zoom.setSelectedItem(round);



        _lat.setBorder(BorderFactory.createTitledBorder("Lat:"));
        _lon.setBorder(BorderFactory.createTitledBorder("Lon:"));
        _zoom.setBorder(BorderFactory.createTitledBorder("Zoom:"));

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;

        c.gridx = 0;     c.gridy=0;
        add(_lon,c);      c.gridy++;
        add(_lat,c);      c.gridy++;
        JPanel jPanel = new JPanel();
        jPanel.add(_zoom);
        add(jPanel,c);      c.gridy++;

        addOkPanel(c,1);

    }

    public String[][] getBox(){

        double[][] box = new double[2][2];

        try {
            double  latitude = Double.parseDouble(_lat.getText());
            double longitude = Double.parseDouble(_lon.getText());
            int zoom = _zoom.getSelectedItem().hashCode();

            box[0][0] = longitude - Math.exp(zoom);
            box[0][1] = longitude + Math.exp(zoom);
            box[1][0] = latitude - Math.exp(zoom);
            box[1][1] = latitude + Math.exp(zoom);

            _lat.setText(ds((box[1][1] + box[1][0]) *.5));
            _zoom.setSelectedItem((int) Math.log(box[0][1] - box[0][0]));

            return new String[][]{{ds(box[0][0]), ds(box[0][1])},{ds(box[1][0]), ds(box[1][1])}};

        }
        catch (NumberFormatException nfe) {

        }


        return null;
    }

    @Override
    boolean doOK() {
        String lat = _lat.getText();
        String lon = _lon.getText();

        //todo validate

        try {



            if(lat != null && lat.length() > 0)
                Double.parseDouble(lat);
            if(lon != null && lon.length() > 0)
                Double.parseDouble(lon);


        }
        catch (NumberFormatException nfe) {
            return false;
        }


        return true;
    }

}
