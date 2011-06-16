package edu.cens.spatial.plots.components;

import org.rosuda.JGR.JGR;
import org.rosuda.deducer.widgets.ObjectChooserWidget;

import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 3/25/11
 * Time: 1:36 PM
 */

public class ShapeComponentPanel extends AbstractComponentPanel{

    ObjectChooserWidget _osw = new ObjectChooserWidget("Shapes Files:", JGR.MAINRCONSOLE){{
        setClassFilter("SpatialPolygonsDataFrame");
        refreshObjects();
    }};


    private static enum k {
        shapefile
    }


    @Override
    public void initFromArgs() {
        _osw.setModel(_args.get(k.shapefile));

    }

    public ShapeComponentPanel(Map<String, String> args) {
        super(args);
        initFields();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5,5,5,5);

        add(_osw, c);

        addOkPanel(c, 2);
    }

    @Override
    boolean doOK() {
        Object shape   = _osw.getModel();

        if(shape == null) return false;

        putArg(k.shapefile, shape.toString());

        return true;
    }




}
